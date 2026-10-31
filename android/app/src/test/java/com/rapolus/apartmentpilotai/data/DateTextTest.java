package com.rapolus.apartmentpilotai.data;

import org.junit.Test;
import static org.junit.Assert.*;

public class DateTextTest {
    @Test public void utcInstantIsShownInIst() {
        assertEquals("9 Oct 2026, 1:37 AM IST",DateText.humanize("2026-10-08T20:07:05.380387Z"));
        assertEquals("Offer accepted on 9 Oct 2026, 1:30 AM IST.",DateText.humanize("Offer accepted on 2026-10-08T20:00:34.674541Z."));
        assertEquals("9 Oct 2026, 6:00 PM IST",DateText.humanize("2026-10-09T12:30:00Z"));
    }
    @Test public void otherTextIsUnchanged() {
        assertEquals("2026-10-11T17:00 IST",DateText.humanize("2026-10-11T17:00 IST"));
        assertEquals("Paid on 2026-10-09",DateText.humanize("Paid on 2026-10-09"));
        assertNull(DateText.humanize(null));
    }
    @Test public void dayUsesIstCalendarDate() {
        assertEquals("9 Oct 2026",DateText.day("2026-10-08T20:27:30.061206Z"));
        assertEquals("8 Oct 2026",DateText.day("2026-10-08T10:00:00Z"));
        assertEquals("10 Oct 2026",DateText.day("2026-10-10"));
        assertEquals("",DateText.day(""));
        assertEquals("soon",DateText.day("soon"));
    }
}
