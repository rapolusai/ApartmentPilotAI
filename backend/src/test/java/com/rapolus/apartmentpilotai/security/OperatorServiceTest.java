package com.rapolus.apartmentpilotai.security;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
class OperatorServiceTest {
    private static final LocalDate DEC=LocalDate.of(2026,12,1),DEC_END=LocalDate.of(2026,12,31);
    @Test void trialMonthIsNotExpectedRevenue() {
        // Registered 20 November: the first cycle (the free trial) ends 20 December, so December's cycle is the first paid one.
        assertEquals(new BigDecimal("149"),OperatorService.expectedNextMonth(LocalDate.of(2026,11,20),DEC,DEC_END,20,null));
        // Registered in December itself would be the trial; a registration date after next month expects nothing.
        assertEquals(BigDecimal.ZERO,OperatorService.expectedNextMonth(LocalDate.of(2026,12,5),DEC,DEC_END,20,null));
    }
    @Test void planPriceFollowsFlatCount() {
        assertEquals(new BigDecimal("99"),OperatorService.expectedNextMonth(LocalDate.of(2026,1,31),DEC,DEC_END,8,null));
        assertEquals(new BigDecimal("249"),OperatorService.expectedNextMonth(LocalDate.of(2026,1,31),DEC,DEC_END,40,null));
    }
    @Test void existingInvoiceWinsAndFlatsOutsidePlansExpectNothing() {
        assertEquals(new BigDecimal("49"),OperatorService.expectedNextMonth(LocalDate.of(2026,3,3),DEC,DEC_END,20,new BigDecimal("49")));
        assertEquals(BigDecimal.ZERO,OperatorService.expectedNextMonth(LocalDate.of(2026,3,3),DEC,DEC_END,20,new BigDecimal("-1")).max(BigDecimal.ZERO));
        assertEquals(BigDecimal.ZERO,OperatorService.expectedNextMonth(LocalDate.of(2026,3,3),DEC,DEC_END,3,null));
    }
}
