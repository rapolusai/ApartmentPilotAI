package com.rapolus.apartmentpilotai.domain;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.*;
import java.util.*;
class OperationsRulesTest {
    @Test void touchingBookingsDoNotOverlap() {
        Instant a=Instant.parse("2026-09-14T10:00:00Z"),b=a.plusSeconds(3600);
        assertFalse(OperationsRules.overlaps(a,b,b,b.plusSeconds(60)));
    }
    @Test void approvalCapacityUsesConcurrentPeakNotAllDayTotal() {
        Instant a=Instant.EPOCH;
        var rows=List.of(new OperationsRules.Reservation(a,a.plusSeconds(60),3),new OperationsRules.Reservation(a.plusSeconds(60),a.plusSeconds(120),2));
        assertEquals(3,OperationsRules.peak(a,a.plusSeconds(120),rows));
        assertDoesNotThrow(()->OperationsRules.capacity(5,2,3));
        assertThrows(IllegalArgumentException.class,()->OperationsRules.capacity(5,3,3));
    }
    @Test void rewardStopsAtFirstMonthBoundary() {
        LocalDate a=LocalDate.of(2026,1,31),end=a.plusMonths(1);
        assertTrue(OperationsRules.withinFirstMonth(a,end,end.minusDays(1)));
        assertFalse(OperationsRules.withinFirstMonth(a,end,end));
    }
    @Test void flatPlanBounds() {
        assertEquals(99,OperationsRules.planPrice(10));
        assertEquals(149,OperationsRules.planPrice(30));
        assertEquals(249,OperationsRules.planPrice(50));
        assertThrows(IllegalArgumentException.class,()->OperationsRules.planPrice(51));
    }
    @Test void residentsCannotResolveTicketThemselves() {
        assertThrows(IllegalArgumentException.class,()->OperationsRules.ticketTransition("OPEN","RESOLVED",false));
        assertDoesNotThrow(()->OperationsRules.ticketTransition("RESOLVED","CLOSED",false));
    }
    @Test void remindersSkipQuietHours() {
        assertTrue(OperationsRules.quiet(LocalTime.of(23,0),LocalTime.of(21,0),LocalTime.of(8,0)));
        assertFalse(OperationsRules.quiet(LocalTime.of(9,0),LocalTime.of(21,0),LocalTime.of(8,0)));
    }
}
