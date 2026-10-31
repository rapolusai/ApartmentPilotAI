package com.rapolus.apartmentpilotai.security;

import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.api.Requests;
import com.rapolus.apartmentpilotai.store.Db;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

final class AuthLoginTest {
    private final Db db=mock(Db.class);
    private final PasswordEncoder encoder=mock(PasswordEncoder.class);
    private final AuthService service=new AuthService(db,encoder,new InviteCipher(""),true,12);
    private final UUID user=UUID.randomUUID();
    private static final String MOBILE="9876543210";
    private void account(String status,String role) {
        when(db.find(anyString(),eq(MOBILE))).thenReturn(Optional.of(Map.of("id",user,"pinHash","encoded","status",status,"role",role)));
        when(encoder.matches("123456","encoded")).thenReturn(true);
    }
    private void denied(String role,HttpStatus status) {
        ApiError error=assertThrows(ApiError.class,()->service.login(new Requests.Login(MOBILE,"123456",role)));
        assertEquals(status,error.status());
        verify(db,never()).update(anyString(),any(),any(),any());
    }
    @Test void UnknownAccountDoesNotIssueSession() {
        when(db.find(anyString(),eq(MOBILE))).thenReturn(Optional.empty());
        denied("ADMIN",HttpStatus.UNAUTHORIZED);
    }
    @Test void IncorrectPinDoesNotIssueSession() {
        account("ACTIVE","ADMIN");
        when(encoder.matches("123456","encoded")).thenReturn(false);
        denied("ADMIN",HttpStatus.UNAUTHORIZED);
    }
    @Test void ClientRoleCannotEscalatePrivileges() {
        account("ACTIVE","RESIDENT");
        denied("ADMIN",HttpStatus.UNAUTHORIZED);
    }
    @Test void PendingAndRejectedAccountsCannotSignIn() {
        for(String status:new String[]{"PENDING","REJECTED","DISABLED"}) {
            account(status,"RESIDENT");
            denied("RESIDENT",HttpStatus.FORBIDDEN);
        }
    }
    @Test void SuccessfulLoginReturnsServerAccountAndStoresOnlyTokenHash() {
        account("ACTIVE","ADMIN");
        Map<String,Object> identity=Map.of("id",user,"tenantId",UUID.randomUUID(),"role","ADMIN");
        when(db.one(anyString(),eq(user))).thenReturn(identity);
        Map<String,Object> session=service.login(new Requests.Login(MOBILE,"123456","ADMIN"));
        String token=session.get("token").toString();
        assertSame(identity,session.get("account"));
        assertEquals(43,token.length());
        assertTrue(Instant.parse(session.get("expiresAt").toString()).isAfter(Instant.now()));
        verify(db).update(startsWith("insert into ap_session"),eq(Tokens.digest(token)),eq(user),any(java.sql.Timestamp.class));
    }
    @Test void UnknownMobileStillRunsAPinComparison() {
        when(db.find(anyString(),eq(MOBILE))).thenReturn(Optional.empty());
        denied("ADMIN",HttpStatus.UNAUTHORIZED);
        verify(encoder).matches(eq("123456"),startsWith("$2a$12$"));
    }
    @Test void PinChangeRequiredFlagsLegacyAndWeakPinsOnly() {
        account("ACTIVE","ADMIN");
        when(encoder.matches("4826","encoded")).thenReturn(true);
        when(encoder.matches("482915","encoded")).thenReturn(true);
        when(db.one(anyString(),eq(user))).thenReturn(Map.of("id",user));
        assertEquals(true,service.login(new Requests.Login(MOBILE,"4826","ADMIN")).get("pinChangeRequired"));
        assertEquals(true,service.login(new Requests.Login(MOBILE,"123456","ADMIN")).get("pinChangeRequired"));
        assertEquals(false,service.login(new Requests.Login(MOBILE,"482915","ADMIN")).get("pinChangeRequired"));
    }
    @Test void LogoutRevokesOnlyThePresentedHashedToken() {
        service.logout("Bearer test-session-token");
        verify(db).update(startsWith("delete from ap_session"),eq(Tokens.digest("test-session-token")));
        service.logout(null);
        service.logout("malformed");
        verifyNoMoreInteractions(db);
    }
}
