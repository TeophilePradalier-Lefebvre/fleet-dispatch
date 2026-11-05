package com.junia.fleetdispatch.pricing;

import com.junia.fleetdispatch.model.Parcel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PricingPolicyTest {

    // base price of a 5 kg parcel over 10 km: 3.0 + 10 * 0.8 + 5 * 0.1 = 11.5
    private final Parcel plain = new Parcel.Builder("P-1", 5, "NORTH").build();

    // --- StandardPricing ---

    @Test
    void shouldComputeStandardPrice() {
        assertEquals(11.5, new StandardPricing().calculatePrice(plain, 10), 0.001);
    }

    @Test
    void shouldAddFragileSurcharge() {
        Parcel fragile = new Parcel.Builder("P-2", 5, "NORTH").fragile().build();

        assertEquals(16.5, new StandardPricing().calculatePrice(fragile, 10), 0.001);
    }

    @Test
    void shouldAddTwoPercentOfInsuredValue() {
        Parcel insured = new Parcel.Builder("P-3", 5, "NORTH").insuredValue(500).build();

        assertEquals(21.5, new StandardPricing().calculatePrice(insured, 10), 0.001);
    }

    @Test
    void shouldChargeOnlyTheBaseFeeAndWeightForAZeroDistance() {
        assertEquals(3.5, new StandardPricing().calculatePrice(plain, 0), 0.001);
    }

    @Test
    void shouldRejectNegativeDistance() {
        assertThrows(IllegalArgumentException.class, () -> new StandardPricing().calculatePrice(plain, -1));
    }

    // --- ExpressPricing ---

    @Test
    void shouldMultiplyThePriceByOnePointFiveForExpress() {
        PricingPolicy express = new ExpressPricing(new StandardPricing());

        assertEquals(17.25, express.calculatePrice(plain, 10), 0.001);
    }

    @Test
    void shouldApplyExpressMultiplierOnTheSurchargesToo() {
        Parcel fragileExpress = new Parcel.Builder("P-4", 5, "NORTH").fragile().express().build();
        PricingPolicy express = new ExpressPricing(new StandardPricing());

        assertEquals(24.75, express.calculatePrice(fragileExpress, 10), 0.001);
    }

    // --- PricingPolicyFactory ---

    @Test
    void factoryShouldChooseStandardPricingForANormalParcel() {
        assertTrue(PricingPolicyFactory.forParcel(plain) instanceof StandardPricing);
    }

    @Test
    void factoryShouldChooseExpressPricingForAnExpressParcel() {
        Parcel express = new Parcel.Builder("P-5", 5, "NORTH").express().build();

        assertTrue(PricingPolicyFactory.forParcel(express) instanceof ExpressPricing);
    }
}