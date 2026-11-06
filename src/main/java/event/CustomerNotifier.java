package com.junia.fleetdispatch.event;

import com.junia.fleetdispatch.model.Delivery;
import com.junia.fleetdispatch.utils.MoneyFormatter;

import java.util.ArrayList;
import java.util.List;

/** Builds the messages that would be sent to customers. It keeps them in a list so they can be displayed or tested. */
public class CustomerNotifier implements DeliveryListener {

    private final List<String> messages = new ArrayList<>();

    @Override
    public void onDeliveryAssigned(Delivery delivery) {
        messages.add("Parcel " + delivery.getParcel().getId() + " is on its way with vehicle "
                + delivery.getVehicle().getId() + " (price: " + MoneyFormatter.format(delivery.getPrice()) + ").");
    }

    @Override
    public void onDeliveryCompleted(Delivery delivery) {
        messages.add("Parcel " + delivery.getParcel().getId() + " has been delivered.");
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }
}