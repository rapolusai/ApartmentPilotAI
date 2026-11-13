package com.rapolus.apartmentpilotai.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ShareMessagesTest {
    @Test public void playStoreLinkUsesReleasePackage() {
        assertEquals("https://play.google.com/store/apps/details?id=com.rapolus.apartmentpilotai",ShareMessages.PLAY_STORE_URL);
    }

    @Test public void residentInviteHasCodeLinkStepsAndAttribution() {
        String text=ShareMessages.residentInvite("Green Meadows","ABCD1234EF","30 Nov 2026");
        assertTrue(text.contains("join Green Meadows on ApartmentCare"));
        assertTrue(text.contains("Invite code: ABCD1234EF"));
        assertTrue(text.contains("Valid until: 30 Nov 2026"));
        assertTrue(text.contains(ShareMessages.PLAY_STORE_URL));
        assertTrue(text.contains("\"Join with invite\""));
        assertTrue(text.contains("\"Request to join\""));
        assertTrue(text.contains("Admin approval"));
        assertTrue(text.endsWith("Powered By @Rapolu`s"));
    }

    @Test public void residentInviteToleratesMissingNameAndExpiry() {
        String text=ShareMessages.residentInvite("  ",  "ABCD1234EF","—");
        assertTrue(text.contains("join your apartment on ApartmentCare"));
        assertFalse(text.contains("Valid until"));
    }

    @Test public void referralKeepsTermsAndAddsLinkAndClaimStep() {
        String text=ShareMessages.apartmentReferral("REF123");
        assertTrue(text.contains("referral code: REF123"));
        assertTrue(text.contains(ShareMessages.PLAY_STORE_URL));
        assertTrue(text.contains("\"New apartment\""));
        assertTrue(text.contains("\"I was referred\""));
        assertTrue(text.contains("first month only. Terms apply."));
    }
}
