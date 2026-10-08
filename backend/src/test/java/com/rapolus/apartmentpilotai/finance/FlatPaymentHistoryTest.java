package com.rapolus.apartmentpilotai.finance;
import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.security.Account;
import com.rapolus.apartmentpilotai.store.Db;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class FlatPaymentHistoryTest {
    private final Db db=mock(Db.class);
    private final FinanceService service=new FinanceService(db);
    private final UUID tenant=UUID.randomUUID(),flat=UUID.randomUUID();
    private final Account admin=new Account(UUID.randomUUID(),tenant,null,"QA","ADMIN");
    @Test void staffFilterParentChecksFlatAndRestrictsQuery() {
        service.payments(admin,flat);
        var order=inOrder(db);
        order.verify(db).one(contains("tenant_id=? and id=?"),eq(tenant),eq(flat));
        order.verify(db).rows(contains("and b.flat_id=?"),eq(tenant),eq(flat));
    }
    @Test void residentCannotSelectAnotherFlat() {
        var resident=new Account(UUID.randomUUID(),tenant,flat,"QA","RESIDENT");
        assertThrows(ApiError.class,()->service.payments(resident,UUID.randomUUID()));verifyNoInteractions(db);
    }
    @Test void existingUnfilteredStaffAndResidentScopesRemain() {
        service.payments(admin);
        verify(db).rows(anyString(),eq(tenant));
        service.payments(new Account(UUID.randomUUID(),tenant,flat,"QA","RESIDENT"));
        verify(db).rows(contains("and b.flat_id=?"),eq(tenant),eq(flat));
    }
    @Test void missingOrForeignFlatDoesNotReturnPaymentRows() {
        when(db.one(anyString(),eq(tenant),eq(flat))).thenThrow(ApiError.missing());
        assertThrows(ApiError.class,()->service.payments(admin,flat));verify(db,never()).rows(anyString(),any());
    }
}
