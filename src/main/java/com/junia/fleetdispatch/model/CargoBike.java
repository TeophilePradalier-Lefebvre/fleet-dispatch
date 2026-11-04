package com.junia.fleetdispatch.model;

/** Cheapest vehicle: it needs neither a battery nor fuel so it implements no energy interface. */
public class CargoBike extends Vehicle {

    private static final double COST_PER_KM = 0.15;

    public CargoBike(String id, double maxLoadKg, double maxRangeKm) {
        super(id, maxLoadKg, maxRangeKm);
    }

    @Override
    public double costPerKm() {
        return COST_PER_KM;
    }
}