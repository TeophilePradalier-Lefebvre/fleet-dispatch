package com.junia.fleetdispatch.pricing;

import com.junia.fleetdispatch.model.Parcel;

/** Chooses the pricing policy that matches a parcel
 * to ensure that the shipping service does not require an if/else structure regarding package options. */

public class PricingPolicyFactory {

    public static PricingPolicy forParcel(Parcel parcel) {
        if (parcel.isExpress()) {
            return new ExpressPricing(new StandardPricing());
        }
        return new StandardPricing();
    }
}