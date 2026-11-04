package com.junia.fleetdispatch.exception;

/**
 * Thrown when a parcel is built with invalid data (negative weight, empty zone...).
 * It is unchecked : it signals bad input data, a bug on the caller side,
 * not something the caller can really recover from at each call site.
 */
public class InvalidParcelException extends RuntimeException {

    public InvalidParcelException(String message) {
        super(message);
    }
}