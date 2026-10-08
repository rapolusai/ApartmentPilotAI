package com.rapolus.apartmentpilotai.security;

import java.time.Instant;
import java.util.Objects;

/** Central access rule shared by every authenticated account in an apartment. */
public final class SubscriptionAccessPolicy {
    public enum State { TRIAL, ACTIVE, PAYMENT_DUE }

    private SubscriptionAccessPolicy() { }

    public static State state(Instant now, Instant trialEndsAt, boolean currentInvoiceCovered) {
        Objects.requireNonNull(now, "now");
        Objects.requireNonNull(trialEndsAt, "trialEndsAt");
        if (currentInvoiceCovered) return State.ACTIVE;
        return now.isBefore(trialEndsAt) ? State.TRIAL : State.PAYMENT_DUE;
    }

    public static boolean allowed(State state) {
        return state != State.PAYMENT_DUE;
    }
}
