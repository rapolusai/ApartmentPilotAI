package com.rapolus.apartmentpilotai.data;

import static org.junit.Assert.*;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.Test;

public class TeamDashboardTest {
    @Test public void statusLabelsAndAttentionOrder() {
        assertEquals("Active",TeamDashboard.statusLabel("ACTIVE"));
        assertEquals("Trial",TeamDashboard.statusLabel("TRIAL"));
        assertEquals("Payment due",TeamDashboard.statusLabel("PAYMENT_DUE"));
        assertTrue(TeamDashboard.compare(TeamDashboard.Sort.STATUS,"B","A",BigDecimal.ZERO,BigDecimal.ZERO,"","","PAYMENT_DUE","ACTIVE")<0);
    }
    @Test public void searchMatchesAnyFieldIgnoringCase() {
        assertTrue(TeamDashboard.matches("",(String)null));
        assertTrue(TeamDashboard.matches("green","Green Meadows","Hyderabad"));
        assertTrue(TeamDashboard.matches("48012","x","9848012345"));
        assertFalse(TeamDashboard.matches("pune","Green Meadows","Hyderabad",null));
    }
    @Test public void revenueAndSignInSortDescendingWithNameTieBreak() {
        assertTrue(TeamDashboard.compare(TeamDashboard.Sort.REVENUE,"A","B",new BigDecimal("99"),new BigDecimal("249"),"","","","")>0);
        assertTrue(TeamDashboard.compare(TeamDashboard.Sort.REVENUE,"A","B",BigDecimal.TEN,BigDecimal.TEN,"","","","")<0);
        assertTrue(TeamDashboard.compare(TeamDashboard.Sort.LAST_SIGN_IN,"A","B",BigDecimal.ZERO,BigDecimal.ZERO,"2026-10-01T00:00:00Z","2026-10-20T00:00:00Z","","")>0);
        assertTrue(TeamDashboard.compare(TeamDashboard.Sort.LAST_SIGN_IN,"A","B",BigDecimal.ZERO,BigDecimal.ZERO,"","2026-10-20T00:00:00Z","","")>0);
    }
    @Test public void lastSeenAndMobileFormatting() {
        Instant now=Instant.parse("2026-11-01T10:00:00Z");
        assertEquals("Today",TeamDashboard.lastSeen("2026-11-01T08:00:00Z",now));
        assertEquals("Yesterday",TeamDashboard.lastSeen("2026-10-31T08:00:00Z",now));
        assertEquals("5 days ago",TeamDashboard.lastSeen("2026-10-27T08:00:00Z",now));
        assertEquals("Not recorded",TeamDashboard.lastSeen(null,now));
        assertEquals("98480 12345",TeamDashboard.spacedMobile("9848012345"));
        assertEquals("No active Admin",TeamDashboard.spacedMobile(null));
    }
}
