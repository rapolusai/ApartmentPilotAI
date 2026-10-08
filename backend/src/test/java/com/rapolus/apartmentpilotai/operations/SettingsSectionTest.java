package com.rapolus.apartmentpilotai.operations;

import com.rapolus.apartmentpilotai.api.ApiError;
import com.rapolus.apartmentpilotai.security.Account;
import com.rapolus.apartmentpilotai.store.Db;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SettingsSectionTest {
    private final Db db=mock(Db.class);
    private final Commands commands=mock(Commands.class);
    private final AccountingService service=spy(new AccountingService(db,commands));
    private final Account admin=new Account(UUID.randomUUID(),UUID.randomUUID(),null,"QA","ADMIN");

    @Test void oneFormPreservesUnrelatedCurrentSettings() {
        when(commands.settings(admin)).thenReturn(Map.of("upi","old@bank","reminderDays","5,9","lateEnabled",false));
        doReturn(Map.of("ok",true)).when(service).saveSettings(eq(admin),anyMap());
        service.saveSettingsSection(admin,"payment",Map.of("upi","new@bank"));
        @SuppressWarnings("unchecked") ArgumentCaptor<Map<String,Object>> payload=ArgumentCaptor.forClass(Map.class);
        verify(service).saveSettings(eq(admin),payload.capture());
        assertEquals("new@bank",payload.getValue().get("upi"));
        assertEquals("5,9",payload.getValue().get("reminderDays"));
        assertEquals(false,payload.getValue().get("lateEnabled"));
        var order=inOrder(db,commands);
        order.verify(db).lockTenant(admin.tenantId());
        order.verify(commands).settings(admin);
    }
    @Test void sectionCannotChangeAnotherSectionsFieldsOrTenant() {
        assertThrows(IllegalArgumentException.class,()->service.saveSettingsSection(admin,"payment",Map.of("reminders",false)));
        assertThrows(IllegalArgumentException.class,()->service.saveSettingsSection(admin,"payment",Map.of("tenantId",UUID.randomUUID())));
        verifyNoInteractions(db,commands);
    }
    @Test void invalidOrEmptySectionsFailBeforeWrites() {
        assertThrows(IllegalArgumentException.class,()->service.saveSettingsSection(admin,"unknown",Map.of("upi","x")));
        assertThrows(IllegalArgumentException.class,()->service.saveSettingsSection(admin,"payment",Map.of()));
        verifyNoInteractions(db,commands);
    }
    @Test void residentCannotModifyApartmentSettings() {
        Account resident=new Account(UUID.randomUUID(),admin.tenantId(),UUID.randomUUID(),"QA","RESIDENT");
        assertThrows(ApiError.class,()->service.saveSettingsSection(resident,"payment",Map.of("upi","x")));
        verifyNoInteractions(db,commands);
    }
    @Test void commandRetryDoesNotRewriteNewerSettings() {
        Map<String,Object> change=Map.of("upi","qa@bank","requestKey",UUID.randomUUID().toString());
        when(commands.previous(admin,"SETTINGS_PAYMENT",change)).thenReturn(Optional.of(admin.tenantId()));
        assertEquals(Map.of("ok",true),service.saveSettingsSection(admin,"payment",change));
        verify(commands,never()).settings(any());
        verify(service,never()).saveSettings(any(),anyMap());
    }
}
