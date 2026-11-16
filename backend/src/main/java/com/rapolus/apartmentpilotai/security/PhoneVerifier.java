package com.rapolus.apartmentpilotai.security;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rapolus.apartmentpilotai.api.ApiError;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.regex.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
/**
 * Checks that the person really holds the mobile number they typed. The app proves it with Firebase Authentication
 * (Google sends and checks the SMS code) and sends us the resulting Firebase ID token. We verify that token ourselves:
 * RS256 signature against Google's published keys, issuer and audience for our Firebase project, expiry, and the
 * phone_number claim. No Firebase SDK or service account is needed on the server, only the public project id.
 * With no project id configured (APP_FIREBASE_PROJECT_ID) verification is off and sign-up stays disabled.
 */
@Component public class PhoneVerifier {
    static final String CERTS="https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gserviceaccount.com";
    private static final Duration SKEW=Duration.ofMinutes(2);
    private static final Pattern INDIA=Pattern.compile("\\+91([6-9][0-9]{9})");
    private static final Pattern MAX_AGE=Pattern.compile("max-age=(\\d+)");
    private final ObjectMapper json=new ObjectMapper();
    private final String projectId;
    private final Function<String,PublicKey> keys;
    private final Clock clock;
    private final HttpClient http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private Map<String,PublicKey> cached=Map.of();
    private Instant cachedUntil=Instant.EPOCH;
    @Autowired public PhoneVerifier(@Value("${app.firebase.project-id:}")String projectId) {
        this.projectId=projectId==null?"":projectId.trim();
        this.keys=this::googleKey;
        this.clock=Clock.systemUTC();
    }
    /** For tests: a fixed key lookup and clock instead of Google's certificate endpoint. */
    PhoneVerifier(String projectId,Function<String,PublicKey> keys,Clock clock) {
        this.projectId=projectId;
        this.keys=keys;
        this.clock=clock;
    }
    public static PhoneVerifier disabled() {
        return new PhoneVerifier("");
    }
    public boolean enabled() {
        return !projectId.isEmpty();
    }
    /** Returns the verified 10-digit Indian mobile number from a Firebase ID token, or throws 401. */
    public String verifiedMobile(String idToken) {
        if(!enabled())throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"Phone verification is not set up on this server yet.");
        if(idToken==null||idToken.isBlank())throw invalid();
        String[] parts=idToken.trim().split("\\.");
        if(parts.length!=3)throw invalid();
        JsonNode header,claims;
        try {
            header=json.readTree(decode(parts[0]));
            claims=json.readTree(decode(parts[1]));
        } catch(Exception e) {
            throw invalid();
        }
        if(!"RS256".equals(header.path("alg").asText()))throw invalid();
        PublicKey key=keys.apply(header.path("kid").asText());
        if(key==null)throw invalid();
        try {
            Signature verifier=Signature.getInstance("SHA256withRSA");
            verifier.initVerify(key);
            verifier.update((parts[0]+"."+parts[1]).getBytes(StandardCharsets.US_ASCII));
            if(!verifier.verify(Base64.getUrlDecoder().decode(parts[2])))throw invalid();
        } catch(ApiError e) {
            throw e;
        } catch(Exception e) {
            throw invalid();
        }
        Instant now=clock.instant();
        if(!("https://securetoken.google.com/"+projectId).equals(claims.path("iss").asText()))throw invalid();
        if(!projectId.equals(claims.path("aud").asText()))throw invalid();
        if(claims.path("sub").asText().isEmpty())throw invalid();
        if(!claims.path("exp").canConvertToLong()||Instant.ofEpochSecond(claims.path("exp").asLong()).isBefore(now))throw expired();
        if(!claims.path("iat").canConvertToLong()||Instant.ofEpochSecond(claims.path("iat").asLong()).isAfter(now.plus(SKEW)))throw invalid();
        if(claims.has("auth_time")&&Instant.ofEpochSecond(claims.path("auth_time").asLong()).isAfter(now.plus(SKEW)))throw invalid();
        Matcher phone=INDIA.matcher(claims.path("phone_number").asText());
        if(!phone.matches())throw new ApiError(HttpStatus.UNAUTHORIZED,"Verify an Indian mobile number (+91) to continue.");
        return phone.group(1);
    }
    /** Throws 400 unless the verified number is the one typed in the form. */
    public void requireSameMobile(String typed,String idToken) {
        if(!verifiedMobile(idToken).equals(typed))
            throw new ApiError(HttpStatus.BAD_REQUEST,"The verified phone number does not match the mobile number entered.");
    }
    private static byte[] decode(String part) {
        return Base64.getUrlDecoder().decode(part);
    }
    private static ApiError invalid() {
        return new ApiError(HttpStatus.UNAUTHORIZED,"Phone verification failed. Request a new code and try again.");
    }
    private static ApiError expired() {
        return new ApiError(HttpStatus.UNAUTHORIZED,"Phone verification expired. Request a new code and try again.");
    }
    private synchronized PublicKey googleKey(String kid) {
        if(clock.instant().isAfter(cachedUntil)||!cached.containsKey(kid)) {
            try {
                var response=http.send(HttpRequest.newBuilder(URI.create(CERTS)).timeout(Duration.ofSeconds(15)).GET().build(),HttpResponse.BodyHandlers.ofString());
                if(response.statusCode()!=200)throw new IllegalStateException("status "+response.statusCode());
                Map<String,PublicKey> fresh=new HashMap<>();
                CertificateFactory factory=CertificateFactory.getInstance("X.509");
                for(var entry:json.readTree(response.body()).properties())
                    fresh.put(entry.getKey(),factory.generateCertificate(new ByteArrayInputStream(entry.getValue().asText().getBytes(StandardCharsets.US_ASCII))).getPublicKey());
                long maxAge=response.headers().firstValue("cache-control").map(MAX_AGE::matcher).filter(Matcher::find).map(m->Long.parseLong(m.group(1))).orElse(3600L);
                cached=fresh;
                cachedUntil=clock.instant().plusSeconds(maxAge);
            } catch(Exception e) {
                if(cached.isEmpty())throw new ApiError(HttpStatus.SERVICE_UNAVAILABLE,"Phone verification is temporarily unavailable. Try again in a minute.");
            }
        }
        return cached.get(kid);
    }
}
