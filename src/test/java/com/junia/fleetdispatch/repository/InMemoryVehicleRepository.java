package com.junia.fleetdispatch.repository;

import com.junia.fleetdispatch.model.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Test double => a repository backed by a plain list.
 * Inversion Principle: the services can be tested without any file because they only know the VehicleRepository interface.
 */

public class InMemoryVehicleRepository implements VehicleRepository {

    private final List<Vehicle> vehicles;

    public InMemoryVehicleRepository(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public List<Vehicle> findAll() {
        return new ArrayList<>(vehicles);
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicles.stream()
                .filter(vehicle -> vehicle.getId().equals(id))
                .findFirst();
    }
}