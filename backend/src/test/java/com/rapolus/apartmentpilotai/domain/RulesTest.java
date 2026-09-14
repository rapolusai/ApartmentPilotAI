package com.rapolus.apartmentpilotai.domain;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
class RulesTest {
    @Test void exactMoney() {
        assertEquals(new BigDecimal("2000.00"),Rules.money(new BigDecimal("2000")));
    }
    @Test void noRounding() {
        assertThrows(IllegalArgumentException.class,()->Rules.money(new BigDecimal("0.001")));
    }
    @Test void partialPayment() {
        assertEquals(new BigDecimal("1500.00"),Rules.outstanding(new BigDecimal("2000.00"),List.of(new BigDecimal("500.00"))));
    }
    @Test void noNegativePayment() {
        assertThrows(IllegalArgumentException.class,()->Rules.money(new BigDecimal("-1")));
    }
    @Test void oldBillsProtected() {
        assertThrows(IllegalArgumentException.class,()->Rules.effectiveMonth(YearMonth.of(2026,9),YearMonth.of(2026,9),YearMonth.of(2026,9)));
    }
    @Test void excessPaymentRejected() {
        assertThrows(IllegalArgumentException.class,()->Rules.verifyPayment(new BigDecimal("2001"),new BigDecimal("2000"),LocalDate.of(2026,9,13),LocalDate.of(2026,9,13)));
    }
    @Test void dueDateIsValid() {
        assertEquals(LocalDate.of(2027,2,28),Rules.dueDate(YearMonth.of(2027,2),1,28));
    }
    @Test void futurePaymentRejected() {
        assertThrows(IllegalArgumentException.class,()->Rules.verifyPayment(BigDecimal.ONE,BigDecimal.TEN,LocalDate.of(2026,10,1),LocalDate.of(2026,9,13)));
    }
}
