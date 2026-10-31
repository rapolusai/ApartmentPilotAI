package com.rapolus.apartmentpilotai.data;

import org.junit.Test;
import java.math.BigDecimal;
import java.util.Arrays;
import static org.junit.Assert.*;

public class ReportTextTest {
    @Test public void moneyUsesIndianGrouping() {
        assertEquals("₹0.00",ReportText.money(BigDecimal.ZERO));
        assertEquals("₹999.00",ReportText.money(new BigDecimal("999")));
        assertEquals("₹3,000.00",ReportText.money(new BigDecimal("3000")));
        assertEquals("₹1,24,500.50",ReportText.money(new BigDecimal("124500.5")));
        assertEquals("₹12,34,56,789.00",ReportText.money(new BigDecimal("123456789")));
        assertEquals("−₹2,500.00",ReportText.money(new BigDecimal("-2500")));
        assertEquals("98,500.00",ReportText.plain(new BigDecimal("98500")));
    }
    @Test public void unreadableAmountsAreZero() {
        assertEquals(0,ReportText.amount(null).signum());
        assertEquals(0,ReportText.amount("n/a").signum());
        assertEquals(new BigDecimal("1500.00"),ReportText.amount("1500.00"));
    }
    @Test public void amountInWords() {
        assertEquals("Three thousand rupees only",ReportText.words(new BigDecimal("3000")));
        assertEquals("One lakh twenty-four thousand five hundred rupees only",ReportText.words(new BigDecimal("124500")));
        assertEquals("One thousand two hundred fifty rupees and fifty paise only",ReportText.words(new BigDecimal("1250.50")));
        assertEquals("One rupee only",ReportText.words(BigDecimal.ONE));
        assertEquals("Zero rupees only",ReportText.words(BigDecimal.ZERO));
        assertEquals("Two crore rupees only",ReportText.words(new BigDecimal("20000000")));
    }
    @Test public void monthsAndCodesReadNaturally() {
        assertEquals("October 2026",ReportText.month("2026-10"));
        assertEquals("October 2026",ReportText.month("2026-10-01"));
        assertEquals("soon",ReportText.month("soon"));
        assertEquals("Payment reversal",ReportText.label("PAYMENT_REVERSAL"));
        assertEquals("UPI",ReportText.label("UPI"));
        assertEquals("Other income",ReportText.label("otherIncome"));
    }
    @Test public void wrapBreaksAtSpacesAndNeverOverflows() {
        // One unit per character keeps the test independent of fonts.
        assertEquals(Arrays.asList("Security","agency","October"),ReportText.wrap("Security agency October",10,String::length));
        assertEquals(Arrays.asList("Lift AMC visit"),ReportText.wrap("Lift AMC visit",20,String::length));
        assertEquals(Arrays.asList("ABCDE","FGHIJ","K"),ReportText.wrap("ABCDEFGHIJK",5,String::length));
        assertEquals(Arrays.asList("a","b"),ReportText.wrap("a\nb",5,String::length));
        for(String line:ReportText.wrap("Maintenance · flat B-204 tower two north side",12,String::length))assertTrue(line.length()<=12);
    }
}
