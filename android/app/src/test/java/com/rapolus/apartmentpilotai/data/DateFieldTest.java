package com.rapolus.apartmentpilotai.data;

import org.junit.Test;
import static org.junit.Assert.*;

public class DateFieldTest {
    @Test public void labelsDropTypingHints() {
        assertEquals("Start · IST",DateField.label("Start · IST YYYY-MM-DDTHH:mm"));
        assertEquals("Schedule · IST · optional",DateField.label("Schedule · IST YYYY-MM-DDTHH:mm · optional"));
        assertEquals("AMC expiry · optional",DateField.label("AMC expiry · optional YYYY-MM-DD"));
        assertEquals("Due date",DateField.label("Due date · YYYY-MM-DD"));
        assertEquals("As of · first day of month",DateField.label("As of YYYY-MM-DD · first day of month"));
        assertEquals("Valid until",DateField.label("Valid until"));
    }
    @Test public void timeFieldsAreDetectedFromTheLabel() {
        assertTrue(DateField.hasTime("End · IST YYYY-MM-DDTHH:mm"));
        assertFalse(DateField.hasTime("Due date · YYYY-MM-DD"));
    }
    @Test public void storedValuesAreShownReadably() {
        assertEquals("25 Oct 2026, 5:00 PM",DateField.display("2026-10-25T17:00",true));
        assertEquals("25 Oct 2026, 12:05 AM",DateField.display("2026-10-25T00:05",true));
        assertEquals("9 Oct 2026",DateField.display("2026-10-09",false));
        assertEquals("",DateField.display("",true));
        assertEquals("",DateField.display(null,false));
        assertEquals("soon",DateField.display("soon",false));
    }
    @Test public void pickedValuesKeepTheServerFormat() {
        assertEquals("2026-10-25T17:00",DateField.value(2026,10,25,17,0,true));
        assertEquals("2026-01-05",DateField.value(2026,1,5,0,0,false));
    }
}
