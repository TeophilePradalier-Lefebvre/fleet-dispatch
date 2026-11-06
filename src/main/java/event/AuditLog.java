package com.junia.fleetdispatch.event;

import com.junia.fleetdispatch.model.Delivery;

import java.util.ArrayList;
import java.util.List;

/** Keeps a technical trace of every event in order.
 * Independent from CustomerNotifier so that each listener has one job. */

public class AuditLog implements DeliveryListener {

    private final List<String> entries = new ArrayList<>();

    @Override
    public void onDeliveryAssigned(Delivery delivery) {
        entries.add("ASSIGNED " + delivery.getParcel().getId() + " -> " + delivery.getVehicle().getId());
    }

    @Override
    public void onDeliveryCompleted(Delivery delivery) {
        entries.add("DELIVERED " + delivery.getParcel().getId() + " by " + delivery.getVehicle().getId());
    }

    public List<String> getEntries() {
        return new ArrayList<>(entries);
    }
}