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

class ApartmentDetailsTest {
    private final Db db=mock(Db.class);
    private final Commands commands=mock(Commands.class);
    private final CommunityService service=new CommunityService(db,commands,mock(PasswordEncoder.class));
    private final Account admin=new Account(UUID.randomUUID(),UUID.randomUUID(),null,"QA","ADMIN");
    private Map<String,Object> valid() {return new HashMap<>(Map.of("name"," Community ","city"," Pune ","address"," Street 1 ","pincode"," 500001 "));}
    @Test void requiredAndMalformedFieldsCannotMutateDatabase() {
        for(String key:List.of("name","city","address","pincode")) {
            var p=valid();p.put(key," ");assertThrows(IllegalArgumentException.class,()->service.saveApartment(admin,p));
        }
        for(String code:List.of("12345","abcdef","1234567")) {
            var p=valid();p.put("pincode",code);assertThrows(IllegalArgumentException.class,()->service.saveApartment(admin,p));
        }
        for(String key:List.of("name","city","address")) {
            var p=valid();p.put(key,"x".repeat(key.equals("address")?301:81));assertThrows(IllegalArgumentException.class,()->service.saveApartment(admin,p));
        }
        verifyNoInteractions(db,commands);
    }
    @Test void nonAdminsCannotUpdateApartment() {
        for(String role:List.of("RESIDENT","TREASURER")) {
            var a=new Account(UUID.randomUUID(),admin.tenantId(),null,"QA",role);
            assertThrows(ApiError.class,()->service.saveApartment(a,valid()));
        }
        verifyNoInteractions(db,commands);
    }
    @Test void callerTenantOnlyIsUpdatedAndAudited() {
        var p=valid();p.put("tenantId",UUID.randomUUID().toString());
        when(db.one(anyString(),eq(admin.tenantId()))).thenReturn(Map.of("name","Community"));
        assertEquals("Community",((Map<?,?>)service.saveApartment(admin,p)).get("name"));
        var order=inOrder(db,commands);
        order.verify(db).lockTenant(admin.tenantId());
        order.verify(db).update(contains("where id=?"),eq("Community"),eq("Pune"),eq("Street 1"),eq("500001"),eq(admin.tenantId()));
        order.verify(commands).audit(admin,"APARTMENT_UPDATED",admin.tenantId(),null,"Community");
    }
}
