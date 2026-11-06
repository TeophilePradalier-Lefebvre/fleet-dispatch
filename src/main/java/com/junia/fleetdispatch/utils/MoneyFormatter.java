package com.junia.fleetdispatch.utils;

/** Displays an amount with two decimals instead of raw double output
 * like 7.300000000000001. */
public class MoneyFormatter {

    public static String format(double amount) {
        if (amount < 0) {
            return "-" + format(-amount);
        }
        long cents = (long) (amount * 100 + 0.5); // +0.5 rounds to the nearest cent
        long units = cents / 100;
        long rest = cents % 100;
        return units + "." + (rest < 10 ? "0" : "") + rest + " EUR";
    }
}