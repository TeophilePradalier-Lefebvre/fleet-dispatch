package com.junia.fleetdispatch.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleDeliveryRulesTest {

    private Drone drone;
    private Van van;
    private Parcel lightParcel;

    @BeforeEach
    void setUp() {
        drone = new Drone("DR-1", 5, 40);
        van = new Van("VN-1", 800, 400);
        lightParcel = new Parcel.Builder("P-1", 1, "NORTH").build();
    }

    // --- normal situations ---

    @Test
    void shouldDeliverWhenEveryRuleIsMet() {
        assertTrue(drone.canDeliver(lightParcel, 10));
    }

    @Test
    void shouldNotDeliverWhenBusy() {
        drone.markBusy();

        assertFalse(drone.canDeliver(lightParcel, 10));
    }

    @Test
    void shouldNotDeliverWhenParcelIsTooHeavy() {
        Parcel heavy = new Parcel.Builder("P-2", 50, "NORTH").build();

        assertFalse(drone.canDeliver(heavy, 10));
    }

    @Test
    void shouldNotDeliverWhenTripIsTooLong() {
        assertFalse(drone.canDeliver(lightParcel, 41));
    }

    // --- The drone has its own handling restriction ---

    @Test
    void droneShouldRefuseHeavyFragileParcel() {
        Parcel fragile = new Parcel.Builder("P-3", 3, "NORTH").fragile().build();

        assertFalse(drone.canDeliver(fragile, 10));
    }

    @Test
    void vanShouldAcceptTheSameHeavyFragileParcel() {
        Parcel fragile = new Parcel.Builder("P-3", 3, "NORTH").fragile().build();

        assertTrue(van.canDeliver(fragile, 10));
    }

    // --- edge cases ---

    @Test
    void droneShouldAcceptFragileParcelExactlyAtTheLimit() {
        Parcel fragile = new Parcel.Builder("P-4", 2, "NORTH").fragile().build();

        assertTrue(drone.canDeliver(fragile, 10));
    }

    @Test
    void shouldDeliverWhenTripIsExactlyTheRemainingRange() {
        assertTrue(drone.canDeliver(lightParcel, 40));
    }
}