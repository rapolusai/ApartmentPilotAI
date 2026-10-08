package com.rapolus.apartmentpilotai.data;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static com.rapolus.apartmentpilotai.data.NavigationPolicy.Group.*;

public class NavigationPolicyTest {
    @Test public void paymentsAndReportsSelectMoney() {
        for(String route:new String[]{"dues","payment-status","payment-reverse","expense-reverse","proof","history","receipt","payments","collection-report","cashbook"})
            assertEquals(route,MONEY,NavigationPolicy.group(route,""));
    }
    @Test public void communityAndAliasesSelectCommunity() {
        for(String route:new String[]{"invite-settings","join-review","member","directory","booking","event","parking-share"})
            assertEquals(route,COMMUNITY,NavigationPolicy.group(route,""));
    }
    @Test public void servicesSelectIssues() {
        for(String route:new String[]{"issue","issue-photo","complaint","service","ticket","ticket-update"})
            assertEquals(route,ISSUES,NavigationPolicy.group(route,""));
    }
    @Test public void attachmentTabFollowsParent() {
        assertEquals(MONEY,NavigationPolicy.group("attachments","PAYMENT:123"));
        assertEquals(MONEY,NavigationPolicy.group("attachments","EXPENSE:123"));
        assertEquals(ISSUES,NavigationPolicy.group("attachments","TICKET:123"));
        assertEquals(COMMUNITY,NavigationPolicy.group("attachments","DOCUMENT:123"));
    }
    @Test public void settingsAndUnknownRoutesSelectMore() {
        assertEquals(HOME,NavigationPolicy.group("home",""));
        assertEquals(MORE,NavigationPolicy.group("payment-settings",""));
        assertEquals(MORE,NavigationPolicy.group("not-found",""));
        assertEquals(MORE,NavigationPolicy.group(null,null));
    }
}
