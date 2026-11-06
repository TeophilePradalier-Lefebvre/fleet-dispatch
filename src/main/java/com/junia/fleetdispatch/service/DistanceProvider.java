package com.junia.fleetdispatch.service;

import com.junia.fleetdispatch.exception.UnknownZoneException;

/**
 * Gives the dispatch service access to distance data without depending on its source.
 * The interface is defined by the service that needs it.
 */

public interface DistanceProvider {

    /** One-way distance in kilometres between the depot and a zone. */
    double distanceKm(String zone) throws UnknownZoneException;
}