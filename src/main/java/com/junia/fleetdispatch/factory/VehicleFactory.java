package com.junia.fleetdispatch.factory;

import com.junia.fleetdispatch.model.CargoBike;
import com.junia.fleetdispatch.model.Drone;
import com.junia.fleetdispatch.model.Van;
import com.junia.fleetdispatch.model.Vehicle;

/**
 * The only place that knows how to turn a type name read from a file into a class.
 * The rest of the application only manipulates Vehicle so it never depends on Drone, Van or CargoBike.
 */
public class VehicleFactory {

    public static Vehicle create(String type, String id, double maxLoadKg, double maxRangeKm) {
        return switch (type.toUpperCase()) {
            case "DRONE" -> new Drone(id, maxLoadKg, maxRangeKm);
            case "VAN" -> new Van(id, maxLoadKg, maxRangeKm);
            case "BIKE" -> new CargoBike(id, maxLoadKg, maxRangeKm);
            default -> throw new IllegalArgumentException("Unknown vehicle type: " + type);
        };
    }
}