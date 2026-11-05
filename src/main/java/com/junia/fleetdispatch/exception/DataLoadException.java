package com.junia.fleetdispatch.exception;

/**
 * Thrown when initial data cannot be loaded.
 * It is checked: the application cannot start correctly without its data so every caller
 * is forced to decide what to do, here it stop with a message.
 */
public class DataLoadException extends Exception {

    public DataLoadException(String message) {
        super(message);
    }

    // Keeps the original exception so the root cause is not lost.
    public DataLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}