package com.rapolus.apartmentpilotai.security;

import com.rapolus.apartmentpilotai.api.*;
import com.rapolus.apartmentpilotai.store.Db;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

final class AuthRegistrationTest {
    private final Db db=mock(Db.class);
    private final PasswordEncoder encoder=mock(PasswordEncoder.class);
    private final AuthService service=new AuthService(db,encoder,new InviteCipher(""),true,12);
    private Requests.Register details() {return new Requests.Register("Owner","9876543210","482915","Community","Hyderabad",5,"Street 1","500001");}
    @Test void productionRegistrationRemainsDisabledWithoutVerification() {
        var disabled=new AuthService(db,encoder,new InviteCipher(""),false,12);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE,assertThrows(ApiError.class,()->disabled.register(details())).status());
        verifyNoInteractions(db,encoder);
    }
    @Test void duplicateMobileCannotCreateOrphanTenant() {
        when(db.find(anyString(),eq("9876543210"))).thenReturn(Optional.of(Map.of("id",UUID.randomUUID())));
        assertEquals(HttpStatus.CONFLICT,assertThrows(ApiError.class,()->service.register(details())).status());
        verify(db,never()).update(anyString(),any(),any(),any(),any(),any());
        verifyNoInteractions(encoder);
    }
    @Test void registrationStoresAddressAndHashesPin() {
        when(encoder.encode("482915")).thenReturn("hashed-pin");
        when(db.one(anyString(),any())).thenReturn(Map.of("role","ADMIN"));
        var result=service.register(details());
        verify(db).update(startsWith("insert into ap_tenant"),any(UUID.class),eq("Community"),eq("Hyderabad"),eq("Street 1"),eq("500001"));
        verify(db).update(startsWith("insert into ap_user"),any(UUID.class),any(UUID.class),eq("Owner"),eq("9876543210"),eq("hashed-pin"));
        assertNotNull(result.get("token"));
    }
    @Test void receiptReturnsOnlyBoundRequestStatusNotAuthentication() {
        when(db.one(anyString(),eq(Tokens.digest("receipt-secret")))).thenReturn(Map.of("status","ACTIVE","apartmentName","Community","flatLabel","A-101"));
        var result=service.joinStatus("receipt-secret");
        assertEquals("ACTIVE",result.get("status"));
        assertFalse(result.containsKey("token"));assertFalse(result.containsKey("mobile"));assertFalse(result.containsKey("id"));
        verify(db).one(contains("r.token_hash=? and r.expires_at>now()"),eq(Tokens.digest("receipt-secret")));
        verifyNoMoreInteractions(db);
    }
    @Test void invalidOrExpiredReceiptFailsClosed() {
        when(db.one(anyString(),any())).thenThrow(ApiError.missing());
        assertEquals(HttpStatus.NOT_FOUND,assertThrows(ApiError.class,()->service.joinStatus("wrong-receipt")).status());
    }
}
