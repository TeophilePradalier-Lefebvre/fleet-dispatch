package com.junia.fleetdispatch.pricing;

import com.junia.fleetdispatch.model.Parcel;

/**
 * How a delivery is priced. Here Adding a new way of pricing means adding a new class
 * without modifying the existing ones or the service that uses them.
 */
public interface PricingPolicy {

    double calculatePrice(Parcel parcel, double distanceKm);
}