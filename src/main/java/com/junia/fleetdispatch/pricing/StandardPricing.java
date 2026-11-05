package com.junia.fleetdispatch.pricing;

import com.junia.fleetdispatch.model.Parcel;

/** Base fee + distance + weight plus a surcharge for fragile parcels and 2% of the insured value. */
public class StandardPricing implements PricingPolicy {

    private static final double BASE_FEE = 3.0;
    private static final double RATE_PER_KM = 0.8;
    private static final double RATE_PER_KG = 0.1;
    private static final double FRAGILE_FEE = 5.0;
    private static final double INSURANCE_RATE = 0.02;

    @Override
    public double calculatePrice(Parcel parcel, double distanceKm) {
        if (distanceKm < 0) {
            throw new IllegalArgumentException("Distance cannot be negative.");
        }
        double price = BASE_FEE + distanceKm * RATE_PER_KM + parcel.getWeightKg() * RATE_PER_KG;
        if (parcel.isFragile()) {
            price += FRAGILE_FEE;
        }
        return price + parcel.getInsuredValue() * INSURANCE_RATE;
    }
}