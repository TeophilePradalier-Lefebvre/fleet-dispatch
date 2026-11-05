package com.junia.fleetdispatch.pricing;

import com.junia.fleetdispatch.model.Parcel;

/**
 * Express delivery = the price of another policy multiplied by 1.5.
 * The base policy is injected so this class does not duplicate the pricing formula.
 */

public class ExpressPricing implements PricingPolicy {

    private static final double EXPRESS_MULTIPLIER = 1.5;

    private final PricingPolicy basePolicy;

    public ExpressPricing(PricingPolicy basePolicy) {
        this.basePolicy = basePolicy;
    }

    @Override
    public double calculatePrice(Parcel parcel, double distanceKm) {
        return basePolicy.calculatePrice(parcel, distanceKm) * EXPRESS_MULTIPLIER;
    }
}