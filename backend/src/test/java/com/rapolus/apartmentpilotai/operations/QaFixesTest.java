package com.rapolus.apartmentpilotai.operations;

import com.rapolus.apartmentpilotai.api.ErrorHandler;
import com.rapolus.apartmentpilotai.security.Account;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Rules added for the 9 Oct 2026 tablet QA findings. */
class QaFixesTest {
    @Test void contactPhonesNeedRealNumbersOrEmergencyCodes() {
        for(String ok:new String[]{"9810099001","+91 98100 99001","040-12345678","04012345678","1800-123-4567","100","108","112"})assertTrue(CommunityService.validPhone(ok),ok);
        for(String bad:new String[]{"123","1234567","99999","abc1234567","12345678901234567"})assertFalse(CommunityService.validPhone(bad),bad);
    }
    @Test void fieldNamesReadAsWords() {
        assertEquals("Due day",ErrorHandler.label("dueDay"));
        assertEquals("Title",ErrorHandler.label("title"));
        assertEquals("Pin",ErrorHandler.label("pin"));
    }
    @Test void scheduledJobsAreAuditedAsSystemNotAsTheAdmin() {
        UUID admin=UUID.randomUUID(),tenant=UUID.randomUUID();
        Account system=Account.system(admin,tenant);
        assertTrue(system.system());
        assertNull(system.auditActor());
        assertEquals("ADMIN",system.role());
        // A person who happens to have the same display name is still a person.
        Account person=new Account(admin,tenant,null,new String("System automation"),"ADMIN");
        assertFalse(person.system());
        assertEquals(admin,person.auditActor());
    }
}
