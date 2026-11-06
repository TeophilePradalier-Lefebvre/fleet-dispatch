package com.junia.fleetdispatch.pricing;

import com.junia.fleetdispatch.model.Parcel;

/**
 * Defines how the price of a delivery is calculated.
 * A new pricing method can be added through another class without changing the existing code or the service using it.
 */
public interface PricingPolicy {

    double calculatePrice(Parcel parcel, double distanceKm);
}