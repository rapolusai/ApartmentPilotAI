package com.rapolus.apartmentpilotai.finance;

import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.security.Account;
import com.rapolus.apartmentpilotai.store.Db;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PaymentDetailTest {
    private final Db db=mock(Db.class);
    private final FinanceService finance=new FinanceService(db);
    private final UUID tenant=UUID.randomUUID(),flat=UUID.randomUUID(),payment=UUID.randomUUID(),user=UUID.randomUUID();
    private final Account resident=new Account(user,tenant,flat,"QA","RESIDENT");
    private void record(UUID parent,String status,UUID submitter) {
        when(db.one(anyString(),eq(tenant),eq(payment))).thenReturn(new HashMap<>(Map.of("flatId",parent,"status",status,"submittedBy",submitter)));
    }
    @Test void ownPendingPaymentCanAttachWithoutExposingSubmitterId() {
        record(flat,"PENDING",user);
        var result=finance.payment(resident,payment);
        assertEquals(true,result.get("canAttach"));assertFalse(result.containsKey("submittedBy"));
        verify(db).one(anyString(),eq(tenant),eq(payment));
    }
    @Test void anotherFlatIsForbiddenEvenWithinSameTenant() {
        record(UUID.randomUUID(),"PENDING",user);
        assertThrows(ApiError.class,()->finance.payment(resident,payment));
    }
    @Test void approvedPaymentCannotReceiveNewProof() {
        record(flat,"APPROVED",user);
        assertEquals(false,finance.payment(resident,payment).get("canAttach"));
    }
    @Test void staffCanViewOtherFlatAndAttachOnlyWhilePending() {
        record(UUID.randomUUID(),"PENDING",UUID.randomUUID());
        Account staff=new Account(UUID.randomUUID(),tenant,null,"QA","TREASURER");
        assertEquals(true,finance.payment(staff,payment).get("canAttach"));
    }
    @Test void residentCannotAttachToAnotherSubmittersDeclaration() {
        record(flat,"PENDING",UUID.randomUUID());
        assertEquals(false,finance.payment(resident,payment).get("canAttach"));
    }
}
