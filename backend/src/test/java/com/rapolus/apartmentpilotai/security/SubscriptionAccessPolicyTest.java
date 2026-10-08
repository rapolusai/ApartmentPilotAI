package com.rapolus.apartmentpilotai.security;

import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;

final class SubscriptionAccessPolicyTest {
    private static final Instant END=Instant.parse("2026-10-03T12:00:00Z");

    @Test void trialAllowsAccessBeforeCutoff() {
        var state=SubscriptionAccessPolicy.state(END.minusNanos(1),END,false);
        assertEquals(SubscriptionAccessPolicy.State.TRIAL,state);
        assertTrue(SubscriptionAccessPolicy.allowed(state));
    }

    @Test void trialRequiresSubscriptionAtExactCutoff() {
        var state=SubscriptionAccessPolicy.state(END,END,false);
        assertEquals(SubscriptionAccessPolicy.State.PAYMENT_DUE,state);
        assertFalse(SubscriptionAccessPolicy.allowed(state));
    }

    @Test void coveredInvoiceAllowsAccessAfterTrial() {
        var state=SubscriptionAccessPolicy.state(END.plusSeconds(1),END,true);
        assertEquals(SubscriptionAccessPolicy.State.ACTIVE,state);
        assertTrue(SubscriptionAccessPolicy.allowed(state));
    }
}
