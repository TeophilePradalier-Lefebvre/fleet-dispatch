package com.junia.fleetdispatch.model;

/** Cheap and quick but with a small load a short range and a limit on fragile parcels. */
public class Drone extends Vehicle implements Rechargeable {

    private static final double COST_PER_KM = 0.40;

    // A drone shakes its cargo
    //  above this weight a fragile parcel would be too risky.
    private static final double MAX_FRAGILE_WEIGHT_KG = 2;

    public Drone(String id, double maxLoadKg, double maxRangeKm) {
        super(id, maxLoadKg, maxRangeKm);
    }

    @Override
    public double costPerKm() {
        return COST_PER_KM;
    }

    @Override
    protected boolean acceptsHandling(Parcel parcel) {
        return !parcel.isFragile() || parcel.getWeightKg() <= MAX_FRAGILE_WEIGHT_KG;
    }

    @Override
    public void recharge() {
        restoreFullRange();
    }
}