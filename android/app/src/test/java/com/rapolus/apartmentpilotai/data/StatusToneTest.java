package com.rapolus.apartmentpilotai.data;

import org.junit.Test;
import static org.junit.Assert.*;

public class StatusToneTest {
    @Test public void existingBadgesKeepTheirColours() {
        assertEquals(StatusTone.WAITING,StatusTone.of("Pending"));
        assertEquals(StatusTone.REVIEW,StatusTone.of("In review"));
        assertEquals(StatusTone.UPCOMING,StatusTone.of("Upcoming"));
        assertEquals(StatusTone.UPCOMING,StatusTone.of("next"));
        assertEquals(StatusTone.DONE,StatusTone.of("Paid"));
        assertEquals(StatusTone.DONE,StatusTone.of("APPROVED"));
    }
    @Test public void problemsStandOut() {
        assertEquals(StatusTone.PROBLEM,StatusTone.of("Rejected"));
        assertEquals(StatusTone.PROBLEM,StatusTone.of("OVERDUE"));
        assertEquals(StatusTone.PROBLEM,StatusTone.of("failed"));
        assertEquals(StatusTone.WAITING,StatusTone.of("Reversed"));
    }
    @Test public void unknownIsNeutral() {
        assertEquals(StatusTone.NEUTRAL,StatusTone.of("Cancelled"));
        assertEquals(StatusTone.NEUTRAL,StatusTone.of(null));
        assertEquals(StatusTone.NEUTRAL,StatusTone.of(""));
    }
}
