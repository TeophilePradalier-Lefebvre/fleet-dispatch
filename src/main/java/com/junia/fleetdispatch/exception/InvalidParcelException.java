package com.junia.fleetdispatch.exception;

/**
 * Thrown when a parcel is built with invalid data (negative weight, empty zone...).
 * It is unchecked therefore it signals bad input data or a bug on the caller side but
 * not something the caller can really recover from at each call site.
 */
public class InvalidParcelException extends RuntimeException {

    public InvalidParcelException(String message) {
        super(message);
    }
}