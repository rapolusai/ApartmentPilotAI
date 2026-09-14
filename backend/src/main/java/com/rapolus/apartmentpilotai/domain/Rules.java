package com.rapolus.apartmentpilotai.domain;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
/** Pure, dependency-free financial rules shared by commands and executable checks. */ public final class Rules {
    private Rules() {
    }
    public static BigDecimal money(BigDecimal value) {
        if(value==null || value.signum()<=0 || value.compareTo(new BigDecimal("99999999.99"))>0) throw new IllegalArgumentException("Enter an amount between 0.01 and 99999999.99.");
        try {
            return value.setScale(2,RoundingMode.UNNECESSARY);
        } catch(ArithmeticException e) {
            throw new IllegalArgumentException("Use at most two decimal places.");
        }
    }
    public static BigDecimal outstanding(BigDecimal billed,List<BigDecimal> approved) {
        BigDecimal paid=approved.stream().reduce(BigDecimal.ZERO,BigDecimal::add);
        if(paid.signum()<0 || paid.compareTo(billed)>0) throw new IllegalArgumentException("Invalid approved total.");
        return billed.subtract(paid).setScale(2,RoundingMode.UNNECESSARY);
    }
    public static void verifyPayment(BigDecimal amount,BigDecimal balance,LocalDate paidOn,LocalDate today) {
        money(amount);
        if(amount.compareTo(balance)>0) throw new IllegalArgumentException("Payment exceeds outstanding amount.");
        if(paidOn==null || paidOn.isAfter(today)) throw new IllegalArgumentException("Choose a valid payment date.");
    }
    public static LocalDate dueDate(YearMonth month,int billingDay,int dueDay) {
        if(month==null || billingDay<1 || billingDay>28 || dueDay<billingDay || dueDay>28) throw new IllegalArgumentException("Choose billing/due days 1–28; due day cannot precede billing day.");
        return month.atDay(dueDay);
    }
    public static void effectiveMonth(YearMonth effective,YearMonth now,YearMonth lastBilled) {
        if(effective==null || effective.isBefore(now)) throw new IllegalArgumentException("Use the current or a future month.");
        if(lastBilled!=null && !effective.isAfter(lastBilled)) throw new IllegalArgumentException("Existing bills are locked. Choose a month after "+lastBilled+".");
    }
}
