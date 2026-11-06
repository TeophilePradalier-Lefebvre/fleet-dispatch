package com.junia.fleetdispatch.service;

import com.junia.fleetdispatch.assignment.CheapestVehicleStrategy;
import com.junia.fleetdispatch.event.AuditLog;
import com.junia.fleetdispatch.exception.NoVehicleAvailableException;
import com.junia.fleetdispatch.model.CargoBike;
import com.junia.fleetdispatch.model.Delivery;
import com.junia.fleetdispatch.model.Drone;
import com.junia.fleetdispatch.model.Parcel;
import com.junia.fleetdispatch.model.Vehicle;
import com.junia.fleetdispatch.repository.InMemoryVehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DispatchServiceEventsTest {

    private DispatchService service;
    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        List<Vehicle> fleet = List.of(new CargoBike("CB-1", 30, 40), new Drone("DR-1", 5, 40));
        FixedDistanceProvider distances = new FixedDistanceProvider();
        distances.addZone("NORTH", 5);
        service = new DispatchService(new InMemoryVehicleRepository(fleet), distances, new CheapestVehicleStrategy());
        auditLog = new AuditLog();
        service.addListener(auditLog);
    }

    private Parcel parcel(String id, double weightKg) {
        return new Parcel.Builder(id, weightKg, "NORTH").build();
    }

    @Test
    void shouldNotifyListenersWhenAParcelIsAssigned() throws Exception {
        service.dispatch(parcel("P-1", 3));

        assertEquals("ASSIGNED P-1 -> CB-1", auditLog.getEntries().get(0));
    }

    @Test
    void shouldNotifyListenersWhenADeliveryIsCompleted() throws Exception {
        Delivery delivery = service.dispatch(parcel("P-1", 3));

        service.completeDelivery(delivery);

        assertEquals("DELIVERED P-1 by CB-1", auditLog.getEntries().get(1));
    }

    @Test
    void shouldNotifyEveryRegisteredListener() throws Exception {
        AuditLog secondLog = new AuditLog();
        service.addListener(secondLog);

        service.dispatch(parcel("P-1", 3));

        assertEquals(1, auditLog.getEntries().size());
        assertEquals(1, secondLog.getEntries().size());
    }

    @Test
    void shouldNotNotifyAnythingWhenDispatchFails() {
        assertThrows(NoVehicleAvailableException.class, () -> service.dispatch(parcel("P-2", 2000)));

        assertTrue(auditLog.getEntries().isEmpty());
    }

    @Test
    void shouldWorkWithoutAnyListener() throws Exception {
        FixedDistanceProvider distances = new FixedDistanceProvider();
        distances.addZone("NORTH", 5);
        DispatchService withoutListener = new DispatchService(
                new InMemoryVehicleRepository(List.of(new CargoBike("CB-9", 30, 40))),
                distances,
                new CheapestVehicleStrategy());

        Delivery delivery = withoutListener.dispatch(parcel("P-1", 3));

        assertEquals("CB-9", delivery.getVehicle().getId());
    }

    @Test
    void shouldCompleteEveryPendingDeliveryAtOnce() throws Exception {
        service.dispatch(parcel("P-1", 3));
        service.dispatch(parcel("P-2", 3));

        service.completeAllDeliveries();

        assertTrue(service.getDeliveries().get(0).isDelivered());
        assertTrue(service.getDeliveries().get(1).isDelivered());
        assertEquals(4, auditLog.getEntries().size());
    }

    @Test
    void shouldIgnoreDeliveriesThatAreAlreadyCompletedWhenCompletingAll() throws Exception {
        Delivery first = service.dispatch(parcel("P-1", 3));
        service.completeDelivery(first);

        service.completeAllDeliveries();

        assertEquals(2, auditLog.getEntries().size());
    }
}