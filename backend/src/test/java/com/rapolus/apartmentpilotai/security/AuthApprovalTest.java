package com.rapolus.apartmentpilotai.security;
import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.store.Db;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

final class AuthApprovalTest {
    private final Db db=mock(Db.class);
    private final AuthService service=new AuthService(db,mock(PasswordEncoder.class),new InviteCipher(""),true,12);
    private final Account admin=new Account(UUID.randomUUID(),UUID.randomUUID(),null,"Admin","ADMIN");
    @Test void unverifiedApprovalCannotTouchDatabase() {
        assertEquals(HttpStatus.BAD_REQUEST,assertThrows(ApiError.class,()->service.approve(admin,UUID.randomUUID(),false)).status());
        verifyNoInteractions(db);
    }
    @Test void residentCannotGrantApprovalEvenWithVerificationFlag() {
        var resident=new Account(UUID.randomUUID(),admin.tenantId(),UUID.randomUUID(),"Resident","RESIDENT");
        assertEquals(HttpStatus.FORBIDDEN,assertThrows(ApiError.class,()->service.approve(resident,UUID.randomUUID(),true)).status());
        verifyNoInteractions(db);
    }
    @Test void verifiedApprovalIsTenantBoundAndAudited() {
        UUID member=UUID.randomUUID();
        when(db.update(anyString(),eq(member),eq(admin.tenantId()))).thenReturn(1);
        service.approve(admin,member,true);
        verify(db).lockTenant(admin.tenantId());
        verify(db).update(contains("tenant_id=? and status='PENDING'"),eq(member),eq(admin.tenantId()));
        verify(db).audit(admin.tenantId(),admin.id(),"RESIDENT_APPROVED",member,"Pending","Active");
    }
}
