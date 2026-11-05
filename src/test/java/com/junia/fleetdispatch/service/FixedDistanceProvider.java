package com.junia.fleetdispatch.service;

import com.junia.fleetdispatch.exception.UnknownZoneException;

import java.util.HashMap;
import java.util.Map;

/** Test double here distances are set by hand so tests do not depend on any data file. */
public class FixedDistanceProvider implements DistanceProvider {

    private final Map<String, Double> distances = new HashMap<>();

    public void addZone(String zone, double distanceKm) {
        distances.put(zone, distanceKm);
    }

    @Override
    public double distanceKm(String zone) throws UnknownZoneException {
        if (!distances.containsKey(zone)) {
            throw new UnknownZoneException("Unknown zone: " + zone);
        }
        return distances.get(zone);
    }
}