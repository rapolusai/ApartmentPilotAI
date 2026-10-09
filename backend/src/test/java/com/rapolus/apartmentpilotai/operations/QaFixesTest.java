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
        assertEquals("PIN",ErrorHandler.label("pin"));
    }
    @Test void missingValuesReadAsRequired() {
        assertEquals("is required",ErrorHandler.wording("must not be null"));
        assertEquals("is required",ErrorHandler.wording("must not be blank"));
        assertEquals("must be 6 digits",ErrorHandler.wording("must be 6 digits"));
    }
    @Test void referralCodesAreReadableAndUnambiguous() {
        java.util.Set<String> seen=new java.util.HashSet<>();
        for(int i=0; i<500; i++) {
            String code=SubscriptionService.referralCode();
            assertTrue(code.matches("AP-[2-9A-HJKMNP-Z]{4}-[2-9A-HJKMNP-Z]{4}"),code);
            seen.add(code);
        }
        assertTrue(seen.size()>490,"codes should not repeat in practice");
    }
    @Test void freeTrialCoversTheWholeFirstMonth() {
        java.time.LocalDate firstEnd=java.time.LocalDate.of(2026,11,9);
        java.time.Instant monthEnd=java.time.Instant.parse("2026-11-08T18:30:00Z"); // 9 Nov 00:00 IST
        assertEquals(monthEnd,SubscriptionService.trialEnd(java.time.Instant.parse("2026-10-11T19:37:05Z"),firstEnd),"short stored trial extends to month end");
        java.time.Instant later=java.time.Instant.parse("2026-12-01T00:00:00Z");
        assertEquals(later,SubscriptionService.trialEnd(later,firstEnd),"a longer stored trial is kept");
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
