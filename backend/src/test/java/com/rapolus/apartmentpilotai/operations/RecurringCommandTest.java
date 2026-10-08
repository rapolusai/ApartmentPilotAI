package com.rapolus.apartmentpilotai.operations;
import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.security.Account;
import com.rapolus.apartmentpilotai.store.Db;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecurringCommandTest {
    private final Db db=mock(Db.class);
    private final Commands commands=mock(Commands.class);
    private final AccountingService service=new AccountingService(db,commands);
    private final Account admin=new Account(UUID.randomUUID(),UUID.randomUUID(),null,"QA","ADMIN");
    private final UUID id=UUID.randomUUID(),expense=UUID.randomUUID();
    private final String cycle=LocalDate.now().plusDays(10).toString();
    private Map<String,Object> request(){return Map.of("cycle",cycle,"requestKey",UUID.randomUUID().toString());}
    private Map<String,Object> template(boolean active){return Map.of("id",id,"title","QA Security","category","Security","amount",new java.math.BigDecimal("100.00"),"nextOn",cycle,"active",active);}
    private void load(boolean active){when(db.one(anyString(),eq(admin.tenantId()),eq(id))).thenReturn(template(active));}
    @Test void residentCommandsFailBeforeDatabaseAccess() {
        Account resident=new Account(UUID.randomUUID(),admin.tenantId(),UUID.randomUUID(),"QA","RESIDENT");
        assertThrows(ApiError.class,()->service.prepareRecurring(resident,id,request()));
        assertThrows(ApiError.class,()->service.recurringActive(resident,id,Map.of("active",true,"expectedActive",false)));
        verifyNoInteractions(db,commands);
    }
    @Test void pausedTemplateCannotPrepare() {
        load(false);
        assertThrows(ApiError.class,()->service.prepareRecurring(admin,id,request()));
        verify(db,never()).update(anyString(),any(Object[].class));
    }
    @Test void staleCycleCannotPrepare() {
        load(true);
        assertThrows(ApiError.class,()->service.prepareRecurring(admin,id,Map.of("cycle",LocalDate.now().toString(),"requestKey",UUID.randomUUID().toString())));
        verify(db,never()).update(anyString(),any(Object[].class));
    }
    @Test void secondKeyFindsSameCycleDraftWithoutCreatingExpense() {
        load(true);
        when(db.find(anyString(),eq(admin.tenantId()),eq(id),any())).thenReturn(Optional.of(Map.of("id",expense)));
        assertEquals(Map.of("id",expense),service.prepareRecurring(admin,id,request()));
        verify(db,never()).update(anyString(),any(Object[].class));
        verify(commands).remember(eq(admin),eq("RECURRING_PREPARE"),argThat(p->id.toString().equals(p.get("recurringId"))),eq(expense));
    }
    @Test void retryReturnsOriginalDraftEvenAfterPause() {
        load(false);
        when(commands.previous(eq(admin),eq("RECURRING_PREPARE"),anyMap())).thenReturn(Optional.of(expense));
        assertEquals(Map.of("id",expense),service.prepareRecurring(admin,id,request()));
        verify(db,never()).find(anyString(),eq(admin.tenantId()),eq(id),any());
        verify(commands,never()).remember(any(),anyString(),anyMap(),any());
    }
    @Test void staleActiveDecisionCannotOverwriteCurrentState() {
        load(true);
        assertThrows(ApiError.class,()->service.recurringActive(admin,id,Map.of("active",true,"expectedActive",false,"requestKey",UUID.randomUUID().toString())));
        verify(db,never()).update(anyString(),any(Object[].class));
    }
    @Test void suppliedTenantAndInvalidStateAreRejected() {
        assertThrows(IllegalArgumentException.class,()->service.prepareRecurring(admin,id,Map.of("cycle",cycle,"tenantId",UUID.randomUUID())));
        assertThrows(IllegalArgumentException.class,()->service.recurringActive(admin,id,Map.of("active","true","expectedActive",false)));
        verifyNoInteractions(db,commands);
    }
}
