package com.junia.fleetdispatch.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleTest {

    private CargoBike bike;
    private Drone drone;
    private Van van;

    @BeforeEach
    void setUp() {
        bike = new CargoBike("CB-1", 30, 50);
        drone = new Drone("DR-1", 5, 20);
        van = new Van("VN-1", 800, 400);
    }

    // --- normal situations ---

    @Test
    void shouldCarryWeightBelowMaxLoad() {
        assertTrue(bike.canCarry(10));
    }

    @Test
    void shouldReduceRemainingRangeWhenTravelling() {
        bike.consumeRange(12);

        assertEquals(38, bike.getRemainingRangeKm(), 0.001);
    }

    @Test
    void shouldRestoreFullRangeAfterRecharge() {
        drone.consumeRange(15);

        drone.recharge();

        assertEquals(20, drone.getRemainingRangeKm(), 0.001);
    }

    @Test
    void shouldRestoreFullRangeAfterRefuel() {
        van.consumeRange(300);

        van.refuel();

        assertEquals(400, van.getRemainingRangeKm(), 0.001);
    }

    @Test
    void shouldHaveDifferentCostPerKmDependingOnType() {
        assertTrue(bike.costPerKm() < drone.costPerKm());
        assertTrue(drone.costPerKm() < van.costPerKm());
    }

    @Test
    void shouldConsiderVehiclesWithSameIdAsEqual() {
        assertEquals(new CargoBike("CB-1", 99, 99), bike);
    }

    // --- edge cases ---

    @Test
    void shouldCarryWeightExactlyEqualToMaxLoad() {
        assertTrue(bike.canCarry(30));
    }

    @Test
    void shouldTravelExactlyTheRemainingRange() {
        assertTrue(bike.canTravel(50));
    }

    @Test
    void shouldNotCarryWeightAboveMaxLoad() {
        assertFalse(bike.canCarry(30.1));
    }

    // --- invalid situations ---

    @Test
    void shouldRejectNegativeMaxLoad() {
        assertThrows(IllegalArgumentException.class, () -> new CargoBike("CB-2", -5, 20));
    }

    @Test
    void shouldRejectEmptyId() {
        assertThrows(IllegalArgumentException.class, () -> new Van("", 100, 100));
    }

    @Test
    void shouldThrowWhenRangeIsInsufficientAndKeepRangeUnchanged() {
        assertThrows(IllegalStateException.class, () -> drone.consumeRange(25));

        assertEquals(20, drone.getRemainingRangeKm(), 0.001);
    }

    @Test
    void shouldThrowWhenMarkingABusyVehicleBusyAgain() {
        bike.markBusy();

        assertThrows(IllegalStateException.class, () -> bike.markBusy());
    }
}