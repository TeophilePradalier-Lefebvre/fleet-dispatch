package com.junia.fleetdispatch.exception;

/** Thrown when a parcel must go to a zone for which we have no distance.
 * Checked because the caller must decide what to do with that parcel. */

public class UnknownZoneException extends Exception {

    public UnknownZoneException(String message) {
        super(message);
    }
}