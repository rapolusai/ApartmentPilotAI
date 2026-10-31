package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.security.Account;
import com.rapolus.apartmentpilotai.store.Db;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class SetupServiceTest {
    private final Db db=mock(Db.class);
    private final SetupService service=new SetupService(db);
    private final UUID tenant=UUID.randomUUID();
    @Test void eachRoleGetsItsOwnSteps() {
        assertEquals(9,SetupService.steps("ADMIN").size());
        assertEquals(5,SetupService.steps("TREASURER").size());
        assertEquals(5,SetupService.steps("RESIDENT").size());
        assertEquals("invite",SetupService.steps("ADMIN").get(8).key());
    }
    @Test void lockAllowsOnlyWizardEndpointsForTheRole() {
        assertTrue(SetupService.allowedDuringSetup("ADMIN","POST","/api/v1/ops/flats/bulk"));
        assertTrue(SetupService.allowedDuringSetup("ADMIN","POST","/api/v1/invites"));
        assertTrue(SetupService.allowedDuringSetup("RESIDENT","GET","/api/v1/setup"));
        assertTrue(SetupService.allowedDuringSetup("RESIDENT","POST","/api/v1/ops/vehicles"));
        assertFalse(SetupService.allowedDuringSetup("ADMIN","GET","/api/v1/reports/monthly"));
        assertFalse(SetupService.allowedDuringSetup("ADMIN","POST","/api/v1/members/x/approve"));
        assertFalse(SetupService.allowedDuringSetup("RESIDENT","POST","/api/v1/payments"));
        assertFalse(SetupService.allowedDuringSetup("RESIDENT","POST","/api/v1/ops/flats/bulk"));
        assertFalse(SetupService.allowedDuringSetup("TREASURER","POST","/api/v1/payments/approve"));
    }
    @Test void paymentNeedsPayeeAndAWayToPay() {
        assertFalse(SetupService.paymentReady(Map.of("payee","","upi","a@upi")));
        assertTrue(SetupService.paymentReady(Map.of("payee","Assoc","upi","a@upi")));
        assertTrue(SetupService.paymentReady(Map.of("payee","Assoc","upi","","account","123","ifsc","HDFC0001234")));
        assertFalse(SetupService.paymentReady(Map.of("payee","Assoc","upi","","account","123","ifsc","")));
    }
    @Test void stepsCannotBeSkipped() {
        var admin=new Account(UUID.randomUUID(),tenant,null,"QA","ADMIN");
        when(db.rows(anyString(),any(),any())).thenReturn(List.of());
        assertThrows(ApiError.class,()->service.complete(admin,"payment"));
        verify(db,never()).update(contains("insert into ap_setup_step"),any(),any(),any());
    }
    @Test void unknownStepIsRefused() {
        var resident=new Account(UUID.randomUUID(),tenant,UUID.randomUUID(),"QA","RESIDENT");
        assertThrows(IllegalArgumentException.class,()->service.complete(resident,"invite"));
    }
}
