package com.junia.fleetdispatch.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyFormatterTest {

    @Test
    void shouldAlwaysShowTwoDecimals() {
        assertEquals("7.30 EUR", MoneyFormatter.format(7.3));
    }

    @Test
    void shouldHideFloatingPointNoise() {
        assertEquals("7.30 EUR", MoneyFormatter.format(7.300000000000001));
    }

    @Test
    void shouldFormatAWholeAmount() {
        assertEquals("12.00 EUR", MoneyFormatter.format(12));
    }

    @Test
    void shouldFormatZero() {
        assertEquals("0.00 EUR", MoneyFormatter.format(0));
    }

    @Test
    void shouldKeepTheLeadingZeroOfSmallCents() {
        assertEquals("0.05 EUR", MoneyFormatter.format(0.05));
    }

    @Test
    void shouldFormatANegativeAmount() {
        assertEquals("-3.50 EUR", MoneyFormatter.format(-3.5));
    }

    @Test
    void shouldRoundToTheNearestCent() {
        assertEquals("1.24 EUR", MoneyFormatter.format(1.2449));
    }
}