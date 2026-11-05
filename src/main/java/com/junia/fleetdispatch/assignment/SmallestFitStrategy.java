package com.junia.fleetdispatch.assignment;

import com.junia.fleetdispatch.model.Vehicle;

import java.util.List;
import java.util.Optional;

/** Leaves larger vehicles available for bigger parcels by choosing the one with the lowest maximum load capacity.
 * If two vehicles have the same capacity the first one is selected.*/

public class SmallestFitStrategy implements AssignmentStrategy {

    @Override
    public Optional<Vehicle> select(List<Vehicle> candidates) {
        Vehicle best = null;
        for (Vehicle vehicle : candidates) {
            if (best == null || vehicle.getMaxLoadKg() < best.getMaxLoadKg()) {
                best = vehicle;
            }
        }
        return Optional.ofNullable(best);
    }
}