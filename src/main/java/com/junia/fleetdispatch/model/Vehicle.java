package com.junia.fleetdispatch.model;

/**
 * Base of every vehicle of the fleet.
 *
 * It is abstract because a "generic vehicle" does not exist in the real world
 * only drones, vans and bikes do. It holds the state and the rules shared by all
 * of them (load, range, availability) while each subclass defines its own cost.
 */
public abstract class Vehicle {

    private final String id;
    private final double maxLoadKg;
    private final double maxRangeKm;
    private double remainingRangeKm;
    private boolean busy;

    public Vehicle(String id, double maxLoadKg, double maxRangeKm) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Vehicle id cannot be empty.");
        }
        if (maxLoadKg <= 0) {
            throw new IllegalArgumentException("Max load must be strictly positive.");
        }
        if (maxRangeKm <= 0) {
            throw new IllegalArgumentException("Max range must be strictly positive.");
        }
        this.id = id;
        this.maxLoadKg = maxLoadKg;
        this.maxRangeKm = maxRangeKm;
        this.remainingRangeKm = maxRangeKm; // a new vehicle starts fully charged
        this.busy = false;
    }

    /** Cost to travel one kilometre. Each vehicle type has its own value. */
    public abstract double costPerKm();

    public boolean canCarry(double weightKg) {
        return weightKg > 0 && weightKg <= maxLoadKg;
    }

    public boolean canTravel(double distanceKm) {
        return distanceKm >= 0 && distanceKm <= remainingRangeKm;
    }

    public void consumeRange(double distanceKm) {
        if (distanceKm < 0) {
            throw new IllegalArgumentException("Distance cannot be negative.");
        }
        if (!canTravel(distanceKm)) {
            throw new IllegalStateException(
                    "Vehicle " + id + " cannot travel " + distanceKm + " km, remaining range: " + remainingRangeKm + " km.");
        }
        remainingRangeKm -= distanceKm;
    }

    public void markBusy() {
        if (busy) {
            throw new IllegalStateException("Vehicle " + id + " is already busy.");
        }
        busy = true;
    }

    public void release() {
        busy = false;
    }

    /**
     * Subclasses call this from recharge() / refuel(). The field stays private
     * (encapsulation), only the Vehicle class itself decides how its range changes.
     */
    protected void restoreFullRange() {
        remainingRangeKm = maxRangeKm;
    }

    public String getId() {
        return id;
    }

    public double getMaxLoadKg() {
        return maxLoadKg;
    }

    public double getMaxRangeKm() {
        return maxRangeKm;
    }

    public double getRemainingRangeKm() {
        return remainingRangeKm;
    }

    public boolean isBusy() {
        return busy;
    }

    // Two vehicles are the same if they have the same id: the id identifies a vehicle in the fleet.
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vehicle other)) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[id=" + id + ", maxLoadKg=" + maxLoadKg
                + ", remainingRangeKm=" + remainingRangeKm + ", busy=" + busy + "]";
    }
}