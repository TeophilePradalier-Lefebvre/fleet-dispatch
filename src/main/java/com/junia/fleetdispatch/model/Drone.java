package com.junia.fleetdispatch.model;

/** Light and cheap but with a small load and a short range. */
public class Drone extends Vehicle implements Rechargeable {

    private static final double COST_PER_KM = 0.40;

    public Drone(String id, double maxLoadKg, double maxRangeKm) {
        super(id, maxLoadKg, maxRangeKm);
    }

    @Override
    public double costPerKm() {
        return COST_PER_KM;
    }

    @Override
    public void recharge() {
        restoreFullRange();
    }
}