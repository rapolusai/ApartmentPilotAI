package com.rapolus.apartmentpilotai.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapolus.apartmentpilotai.api.*;
import com.rapolus.apartmentpilotai.store.Db;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

final class PhoneVerifierTest {
    private static final String PROJECT="apartmentcare-test";
    private static final Instant NOW=Instant.parse("2026-11-15T12:00:00Z");
    private final KeyPair keys=generate();
    private final KeyPair other=generate();
    private final PhoneVerifier verifier=new PhoneVerifier(PROJECT,kid->"k1".equals(kid)?keys.getPublic():null,Clock.fixed(NOW,ZoneOffset.UTC));

    private static KeyPair generate() {
        try {
            KeyPairGenerator g=KeyPairGenerator.getInstance("RSA");
            g.initialize(2048);
            return g.generateKeyPair();
        } catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }
    private static String b64(byte[] b) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
    private String token(Map<String,Object> overrides,PrivateKey signer,String kid) throws Exception {
        Map<String,Object> claims=new HashMap<>(Map.of("iss","https://securetoken.google.com/"+PROJECT,"aud",PROJECT,"sub","uid-1",
            "iat",NOW.minusSeconds(30).getEpochSecond(),"exp",NOW.plusSeconds(3000).getEpochSecond(),"auth_time",NOW.minusSeconds(30).getEpochSecond(),"phone_number","+919876543210"));
        claims.putAll(overrides);
        ObjectMapper json=new ObjectMapper();
        String head=b64(json.writeValueAsBytes(Map.of("alg","RS256","kid",kid,"typ","JWT")));
        String body=b64(json.writeValueAsBytes(claims));
        Signature s=Signature.getInstance("SHA256withRSA");
        s.initSign(signer);
        s.update((head+"."+body).getBytes(StandardCharsets.US_ASCII));
        return head+"."+body+"."+b64(s.sign());
    }
    private String token(Map<String,Object> overrides) throws Exception {
        return token(overrides,keys.getPrivate(),"k1");
    }
    private HttpStatus status(String token) {
        return assertThrows(ApiError.class,()->verifier.verifiedMobile(token)).status();
    }

    @Test void validTokenReturnsTenDigitMobile() throws Exception {
        assertEquals("9876543210",verifier.verifiedMobile(token(Map.of())));
    }
    @Test void rejectsWrongSignerProjectExpiryAndCountry() throws Exception {
        assertEquals(HttpStatus.UNAUTHORIZED,status(token(Map.of(),other.getPrivate(),"k1")));
        assertEquals(HttpStatus.UNAUTHORIZED,status(token(Map.of(),keys.getPrivate(),"unknown")));
        assertEquals(HttpStatus.UNAUTHORIZED,status(token(Map.of("aud","someone-else"))));
        assertEquals(HttpStatus.UNAUTHORIZED,status(token(Map.of("iss","https://securetoken.google.com/someone-else"))));
        assertEquals(HttpStatus.UNAUTHORIZED,status(token(Map.of("exp",NOW.minusSeconds(1).getEpochSecond()))));
        assertEquals(HttpStatus.UNAUTHORIZED,status(token(Map.of("iat",NOW.plusSeconds(3600).getEpochSecond()))));
        assertEquals(HttpStatus.UNAUTHORIZED,status(token(Map.of("phone_number","+14155550100"))));
        assertEquals(HttpStatus.UNAUTHORIZED,status("not-a-token"));
        assertEquals(HttpStatus.UNAUTHORIZED,status(""));
    }
    @Test void tamperedClaimsFailSignature() throws Exception {
        String[] p=token(Map.of()).split("\\.");
        String forged=b64(new String(Base64.getUrlDecoder().decode(p[1]),StandardCharsets.UTF_8).replace("9876543210","9000000000").getBytes(StandardCharsets.UTF_8));
        assertEquals(HttpStatus.UNAUTHORIZED,status(p[0]+"."+forged+"."+p[2]));
    }
    @Test void disabledVerifierAnswersServiceUnavailable() {
        assertFalse(PhoneVerifier.disabled().enabled());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE,assertThrows(ApiError.class,()->PhoneVerifier.disabled().verifiedMobile("x")).status());
    }

    private final Db db=mock(Db.class);
    private final PasswordEncoder encoder=mock(PasswordEncoder.class);
    private final AuthService production=new AuthService(db,encoder,new InviteCipher(""),false,12,verifier);

    @Test void productionRegistrationNeedsMatchingVerifiedMobile() throws Exception {
        var mismatch=new Requests.Register("Owner","9000000000","482915","Community","Hyderabad",5,"","",token(Map.of()));
        assertEquals(HttpStatus.BAD_REQUEST,assertThrows(ApiError.class,()->production.register(mismatch)).status());
        var missing=new Requests.Register("Owner","9876543210","482915","Community","Hyderabad",5,"","",null);
        assertEquals(HttpStatus.UNAUTHORIZED,assertThrows(ApiError.class,()->production.register(missing)).status());
        verifyNoInteractions(db,encoder);
        when(encoder.encode("482915")).thenReturn("hashed");
        when(db.one(anyString(),any())).thenReturn(Map.of("role","ADMIN"));
        var ok=production.register(new Requests.Register("Owner","9876543210","482915","Community","Hyderabad",5,"","",token(Map.of())));
        assertNotNull(ok.get("token"));
    }
    @Test void productionJoinNeedsVerifiedMobile() {
        var join=new Requests.Join("CODE","A-101","Resident","9876543210","482915","OWNER");
        assertEquals(HttpStatus.UNAUTHORIZED,assertThrows(ApiError.class,()->production.join(join)).status());
        verifyNoInteractions(db,encoder);
    }
    @Test void resetPinUpdatesHashAndEndsSessions() throws Exception {
        UUID user=UUID.randomUUID(),tenant=UUID.randomUUID();
        when(db.find(contains("from ap_user where mobile=?"),eq("9876543210"))).thenReturn(Optional.of(Map.of("id",user,"tenantId",tenant)));
        when(encoder.encode("482915")).thenReturn("new-hash");
        production.resetPin(new Requests.ResetPin("9876543210","482915",token(Map.of())));
        verify(db).update(startsWith("update ap_user set pin_hash=?"),eq("new-hash"),eq(user));
        verify(db).update(startsWith("delete from ap_session"),eq(user));
    }
    @Test void resetPinRefusesOtherNumbersAndUnknownAccounts() throws Exception {
        assertEquals(HttpStatus.BAD_REQUEST,assertThrows(ApiError.class,()->production.resetPin(new Requests.ResetPin("9000000000","482915",token(Map.of())))).status());
        when(db.find(anyString(),eq("9876543210"))).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND,assertThrows(ApiError.class,()->production.resetPin(new Requests.ResetPin("9876543210","482915",token(Map.of())))).status());
        verify(db,never()).update(anyString(),any(),any());
    }
}
