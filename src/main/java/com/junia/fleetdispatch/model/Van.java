package com.junia.fleetdispatch.model;

/** Heavy loads and long range but the most expensive per kilometre. */
public class Van extends Vehicle implements Refuelable {

    private static final double COST_PER_KM = 0.90;

    public Van(String id, double maxLoadKg, double maxRangeKm) {
        super(id, maxLoadKg, maxRangeKm);
    }

    @Override
    public double costPerKm() {
        return COST_PER_KM;
    }

    @Override
    public void refuel() {
        restoreFullRange();
    }
}