package com.junia.fleetdispatch.assignment;

import com.junia.fleetdispatch.model.CargoBike;
import com.junia.fleetdispatch.model.Drone;
import com.junia.fleetdispatch.model.Van;
import com.junia.fleetdispatch.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssignmentStrategyTest {

    private Vehicle drone;
    private Vehicle bike;
    private Vehicle van;
    private List<Vehicle> candidates;

    @BeforeEach
    void setUp() {
        drone = new Drone("DR-1", 5, 40);
        bike = new CargoBike("CB-1", 30, 40);
        van = new Van("VN-1", 800, 400);
        candidates = List.of(van, drone, bike);
    }

    private String idOf(AssignmentStrategy strategy, List<Vehicle> vehicles) {
        return strategy.select(vehicles).map(Vehicle::getId).orElse("none");
    }

    // --- CheapestVehicleStrategy ---

    @Test
    void cheapestShouldPickTheLowestCostPerKm() {
        assertEquals("CB-1", idOf(new CheapestVehicleStrategy(), candidates));
    }

    @Test
    void cheapestShouldKeepTheFirstOnATie() {
        Vehicle otherBike = new CargoBike("CB-2", 30, 40);

        assertEquals("CB-1", idOf(new CheapestVehicleStrategy(), List.of(bike, otherBike)));
    }

    @Test
    void cheapestShouldReturnNothingWhenThereIsNoCandidate() {
        assertEquals("none", idOf(new CheapestVehicleStrategy(), List.of()));
    }

    // --- SmallestFitStrategy ---

    @Test
    void smallestFitShouldPickTheSmallestMaxLoad() {
        assertEquals("DR-1", idOf(new SmallestFitStrategy(), candidates));
    }

    @Test
    void smallestFitShouldPickTheOnlyCandidate() {
        assertEquals("VN-1", idOf(new SmallestFitStrategy(), List.of(van)));
    }

    @Test
    void smallestFitShouldReturnNothingWhenThereIsNoCandidate() {
        assertEquals("none", idOf(new SmallestFitStrategy(), List.of()));
    }
}