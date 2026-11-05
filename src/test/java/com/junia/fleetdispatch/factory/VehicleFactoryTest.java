package com.junia.fleetdispatch.factory;

import com.junia.fleetdispatch.model.CargoBike;
import com.junia.fleetdispatch.model.Drone;
import com.junia.fleetdispatch.model.Van;
import com.junia.fleetdispatch.model.Vehicle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleFactoryTest {

    @Test
    void shouldCreateDrone() {
        Vehicle vehicle = VehicleFactory.create("DRONE", "DR-1", 5, 20);

        assertTrue(vehicle instanceof Drone);
    }

    @Test
    void shouldCreateVan() {
        Vehicle vehicle = VehicleFactory.create("VAN", "VN-1", 800, 400);

        assertTrue(vehicle instanceof Van);
    }

    @Test
    void shouldCreateCargoBike() {
        Vehicle vehicle = VehicleFactory.create("BIKE", "CB-1", 30, 40);

        assertTrue(vehicle instanceof CargoBike);
    }

    @Test
    void shouldAcceptLowerCaseType() {
        Vehicle vehicle = VehicleFactory.create("drone", "DR-2", 5, 20);

        assertTrue(vehicle instanceof Drone);
    }

    @Test
    void shouldRejectUnknownType() {
        assertThrows(IllegalArgumentException.class, () -> VehicleFactory.create("TRUCK", "T-1", 5000, 800));
    }
}