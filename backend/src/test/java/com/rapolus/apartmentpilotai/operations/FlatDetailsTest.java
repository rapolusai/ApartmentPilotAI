package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.security.Account;
import com.rapolus.apartmentpilotai.store.Db;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class FlatDetailsTest {
    private final Db db=mock(Db.class);
    private final CommunityService service=new CommunityService(db,mock(Commands.class),mock(PasswordEncoder.class));
    private final UUID tenant=UUID.randomUUID(),flat=UUID.randomUUID();
    @Test void detailUsesAuthenticatedTenantAndRequestedParent() {
        var admin=new Account(UUID.randomUUID(),tenant,null,"QA","ADMIN");
        when(db.one(anyString(),eq(tenant),eq(flat))).thenReturn(Map.of("label","A-101"));
        assertEquals("A-101",service.flat(admin,flat).get("label"));
        verify(db).one(contains("where f.tenant_id=? and f.id=?"),eq(tenant),eq(flat));
    }
    @Test void nonAdminsCannotEnumeratePrivateFlatDetails() {
        for(String role:List.of("RESIDENT","TREASURER"))assertThrows(ApiError.class,()->service.flat(new Account(UUID.randomUUID(),tenant,flat,"QA",role),flat));
        verifyNoInteractions(db);
    }
}
