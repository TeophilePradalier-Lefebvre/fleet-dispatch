package com.junia.fleetdispatch.assignment;

import com.junia.fleetdispatch.model.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * How to decide between vehicles that are all suitable for the task.
 * The service first selects the vehicles that meet the requirements
 * The strategy simply helps determine which one is preferred.
 */


public interface AssignmentStrategy {

    Optional<Vehicle> select(List<Vehicle> candidates);
}