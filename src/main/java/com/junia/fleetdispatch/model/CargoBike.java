package com.junia.fleetdispatch.model;

/** Cheapest vehicle per kilometre. It is an electric cargo bike so its battery can be recharged. */
public class CargoBike extends Vehicle implements Rechargeable {

    private static final double COST_PER_KM = 0.15;

    public CargoBike(String id, double maxLoadKg, double maxRangeKm) {
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