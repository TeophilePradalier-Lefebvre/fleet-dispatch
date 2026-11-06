package com.junia.fleetdispatch.event;

import com.junia.fleetdispatch.model.Delivery;
import com.junia.fleetdispatch.model.Drone;
import com.junia.fleetdispatch.model.Parcel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeliveryListenersTest {

    private Delivery delivery;

    @BeforeEach
    void setUp() {
        Parcel parcel = new Parcel.Builder("P-1", 2, "NORTH").build();
        delivery = new Delivery(parcel, new Drone("DR-1", 5, 20), 5, 7.3, 4);
    }

    // --- CustomerNotifier ---

    @Test
    void notifierShouldMentionParcelVehicleAndPriceWhenAssigned() {
        CustomerNotifier notifier = new CustomerNotifier();

        notifier.onDeliveryAssigned(delivery);

        String message = notifier.getMessages().get(0);
        assertTrue(message.contains("P-1"));
        assertTrue(message.contains("DR-1"));
        assertTrue(message.contains("7.30 EUR"));
    }

    @Test
    void notifierShouldSayTheParcelIsDeliveredWhenCompleted() {
        CustomerNotifier notifier = new CustomerNotifier();

        notifier.onDeliveryCompleted(delivery);

        assertTrue(notifier.getMessages().get(0).contains("delivered"));
    }

    @Test
    void notifierShouldReturnACopyOfItsMessages() {
        CustomerNotifier notifier = new CustomerNotifier();
        notifier.onDeliveryAssigned(delivery);

        notifier.getMessages().clear();

        assertEquals(1, notifier.getMessages().size());
    }

    // --- AuditLog ---

    @Test
    void auditLogShouldKeepEventsInOrder() {
        AuditLog auditLog = new AuditLog();

        auditLog.onDeliveryAssigned(delivery);
        auditLog.onDeliveryCompleted(delivery);

        assertEquals("ASSIGNED P-1 -> DR-1", auditLog.getEntries().get(0));
        assertEquals("DELIVERED P-1 by DR-1", auditLog.getEntries().get(1));
    }

    @Test
    void auditLogShouldStartEmpty() {
        assertTrue(new AuditLog().getEntries().isEmpty());
    }
}