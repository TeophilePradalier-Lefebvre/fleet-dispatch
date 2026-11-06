package com.junia.fleetdispatch.event;

import com.junia.fleetdispatch.model.Delivery;

/**
 * Anyone interested in what happens to deliveries implements this interface and registers
 * on the DispatchService.
 * The service notifies them without knowing who they are or what they do
 * so adding a new reaction never requires modifying the service.
 */
public interface DeliveryListener {

    void onDeliveryAssigned(Delivery delivery);

    void onDeliveryCompleted(Delivery delivery);
}