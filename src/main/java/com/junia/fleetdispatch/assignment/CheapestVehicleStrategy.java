package com.junia.fleetdispatch.assignment;

import com.junia.fleetdispatch.model.Vehicle;

import java.util.List;
import java.util.Optional;

/** Keeps big vehicles free for big parcels
 * Picks the vehicle with the smallest max load.
 * On a tie the first one wins. */

public class CheapestVehicleStrategy implements AssignmentStrategy {

    @Override
    public Optional<Vehicle> select(List<Vehicle> candidates) {
        Vehicle best = null;
        for (Vehicle vehicle : candidates) {
            if (best == null || vehicle.costPerKm() < best.costPerKm()) {
                best = vehicle;
            }
        }
        return Optional.ofNullable(best);
    }
}