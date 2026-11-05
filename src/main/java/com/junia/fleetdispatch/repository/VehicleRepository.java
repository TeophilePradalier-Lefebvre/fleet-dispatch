package com.junia.fleetdispatch.repository;

import com.junia.fleetdispatch.model.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * Abstraction used by the services => they ask for vehicles without knowing
 * whether they come from a CSV file, a database or a test list.
 */
public interface VehicleRepository {

    List<Vehicle> findAll();

    Optional<Vehicle> findById(String id);
}