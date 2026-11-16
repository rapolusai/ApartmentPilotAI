package com.rapolus.apartmentpilotai.security;

import com.rapolus.apartmentpilotai.api.*;
import com.rapolus.apartmentpilotai.store.Db;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Launch without SMS: open registration, Admin resets a member's PIN, Forgot PIN by SMS stays off. */
final class PinRecoveryTest {
    private final Db db=mock(Db.class);
    private final PasswordEncoder encoder=mock(PasswordEncoder.class);
    private final AuthService auth=new AuthService(db,encoder,new InviteCipher(""),false,12,PhoneVerifier.disabled());
    private final UUID tenant=UUID.randomUUID(),adminId=UUID.randomUUID(),member=UUID.randomUUID();
    private final Account admin=new Account(adminId,tenant,null,"Admin","ADMIN",false);
    private final Account resident=new Account(UUID.randomUUID(),tenant,UUID.randomUUID(),"Resident","RESIDENT",false);

    private HttpStatus status(Runnable call) {
        return assertThrows(ApiError.class,call::run).status();
    }
    @Test void productionWithoutPhoneOrOpenRegistrationRefusesSignUp() {
        var register=new Requests.Register("Owner","9876543210","482915","Community","Hyderabad",5,"","",null);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE,status(()->auth.register(register)));
        verifyNoInteractions(db,encoder);
    }
    @Test void openRegistrationSignsUpWithoutPhoneCheck() {
        auth.setOpenRegistration(true);
        assertTrue(auth.openRegistrationEnabled());
        when(encoder.encode("482915")).thenReturn("hashed");
        when(db.one(anyString(),any())).thenReturn(Map.of("role","ADMIN"));
        var ok=auth.register(new Requests.Register("Owner","9876543210","482915","Community","Hyderabad",5,"","",null));
        assertNotNull(ok.get("token"));
    }
    @Test void forgotPinBySmsIsOffEvenWithOpenRegistration() {
        auth.setOpenRegistration(true);
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE,status(()->auth.resetPin(new Requests.ResetPin("9876543210","482915","token"))));
        verify(db,never()).update(anyString(),any(),any());
    }
    @Test void adminResetsActiveResidentPinAndEndsSessions() {
        when(db.find(contains("from ap_user where id=? and tenant_id=?"),eq(member),eq(tenant))).thenReturn(Optional.of(Map.of("role","RESIDENT","status","ACTIVE")));
        when(encoder.encode("482915")).thenReturn("new-hash");
        auth.adminResetPin(admin,member,"482915");
        verify(db).update(startsWith("update ap_user set pin_hash=?"),eq("new-hash"),eq(member),eq(tenant));
        verify(db).update(startsWith("delete from ap_session"),eq(member));
        verify(db).audit(eq(tenant),eq(adminId),eq("PIN_RESET_BY_ADMIN"),eq(member),isNull(),anyString());
    }
    @Test void adminResetRefusesNonAdminsSelfOtherAdminsPendingAndOtherApartments() {
        assertThrows(ApiError.class,()->auth.adminResetPin(resident,member,"482915"));
        assertEquals(HttpStatus.BAD_REQUEST,status(()->auth.adminResetPin(admin,adminId,"482915")));
        when(db.find(contains("from ap_user where id=? and tenant_id=?"),eq(member),eq(tenant))).thenReturn(Optional.of(Map.of("role","ADMIN","status","ACTIVE")));
        assertEquals(HttpStatus.CONFLICT,status(()->auth.adminResetPin(admin,member,"482915")));
        when(db.find(contains("from ap_user where id=? and tenant_id=?"),eq(member),eq(tenant))).thenReturn(Optional.of(Map.of("role","RESIDENT","status","PENDING")));
        assertEquals(HttpStatus.CONFLICT,status(()->auth.adminResetPin(admin,member,"482915")));
        when(db.find(contains("from ap_user where id=? and tenant_id=?"),eq(member),eq(tenant))).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND,status(()->auth.adminResetPin(admin,member,"482915")));
        verify(db,never()).update(anyString(),any(),any(),any());
    }
    @Test void supportResetFindsActiveAccountByMobile() {
        UUID user=UUID.randomUUID();
        when(db.find(contains("from ap_user where mobile=?"),eq("9876543210"))).thenReturn(Optional.of(Map.of("id",user,"tenantId",tenant)));
        when(encoder.encode("482915")).thenReturn("h");
        auth.supportResetPin("9876543210","482915");
        verify(db).update(startsWith("update ap_user set pin_hash=?"),eq("h"),eq(user));
        verify(db).update(startsWith("delete from ap_session"),eq(user));
        when(db.find(contains("from ap_user where mobile=?"),eq("9000000000"))).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND,status(()->auth.supportResetPin("9000000000","482915")));
    }
}
