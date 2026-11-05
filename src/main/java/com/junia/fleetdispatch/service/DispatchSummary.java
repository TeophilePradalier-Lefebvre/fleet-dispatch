package com.junia.fleetdispatch.service;

import com.junia.fleetdispatch.model.Delivery;

import java.util.ArrayList;
import java.util.List;

/** Outcome of dispatching several parcels
 *  what was assigned and what failed with the reason. */
public class DispatchSummary {

    private final List<Delivery> assigned = new ArrayList<>();
    private final List<String> failures = new ArrayList<>();

    public void addAssigned(Delivery delivery) {
        assigned.add(delivery);
    }

    public void addFailure(String message) {
        failures.add(message);
    }

    public List<Delivery> getAssigned() {
        return new ArrayList<>(assigned);
    }

    public List<String> getFailures() {
        return new ArrayList<>(failures);
    }
}