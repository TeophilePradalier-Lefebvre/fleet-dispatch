package com.junia.fleetdispatch.service;

import com.junia.fleetdispatch.exception.UnknownZoneException;

/**
 * Here you can find what the dispatch service needs to know about distances whatever the source.
 * The interface belongs to the service that uses it.
 */
public interface DistanceProvider {

    /** One-way distance in kilometres between the depot and a zone. */
    double distanceKm(String zone) throws UnknownZoneException;
}