package com.junia.fleetdispatch.exception;

/**
 * Thrown when no vehicle of the fleet can deliver a given parcel.
 * It is checked: it is a normal business outcome that the caller MUST handle
 * for example by reporting the parcel as undeliverable and continuing with the next one.
 */
public class NoVehicleAvailableException extends Exception {

    public NoVehicleAvailableException(String message) {
        super(message);
    }
}