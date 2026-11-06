package com.junia.fleetdispatch.service;

import com.junia.fleetdispatch.model.Delivery;
import com.junia.fleetdispatch.model.Parcel;

import java.util.ArrayList;
import java.util.List;

/** Keeps track of which parcels were assigned and which ones failed so they can be tried again later. */

public class DispatchSummary {

    private final List<Delivery> assigned = new ArrayList<>();
    private final List<String> failures = new ArrayList<>();
    private final List<Parcel> failedParcels = new ArrayList<>();

    public void addAssigned(Delivery delivery) {
        assigned.add(delivery);
    }

    public void addFailure(Parcel parcel, String message) {
        failedParcels.add(parcel);
        failures.add(message);
    }

    public List<Delivery> getAssigned() {
        return new ArrayList<>(assigned);
    }

    public List<String> getFailures() {
        return new ArrayList<>(failures);
    }

    public List<Parcel> getFailedParcels() {
        return new ArrayList<>(failedParcels);
    }
}