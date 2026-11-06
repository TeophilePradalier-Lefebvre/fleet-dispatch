package com.junia.fleetdispatch.adapter;

import com.junia.fleetdispatch.exception.UnknownZoneException;
import com.junia.fleetdispatch.service.DistanceProvider;

/**
 * Makes the legacy service look like the DistanceProvider our service expects.
 * The legacy class is never modified and DispatchService never knows it exists.
 */
public class LegacyDistanceAdapter implements DistanceProvider {

    private static final double KM_PER_MILE = 1.609;

    private final LegacyZoneService legacyService;

    public LegacyDistanceAdapter(LegacyZoneService legacyService) {
        this.legacyService = legacyService;
    }

    @Override
    public double distanceKm(String zone) throws UnknownZoneException {
        int miles = legacyService.getDistanceInMiles(zone);
        if (miles < 0) {
            throw new UnknownZoneException("Unknown zone: " + zone);
        }
        return miles * KM_PER_MILE;
    }
}