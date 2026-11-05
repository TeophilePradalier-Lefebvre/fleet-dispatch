package com.junia.fleetdispatch.service;

import com.junia.fleetdispatch.assignment.CheapestVehicleStrategy;
import com.junia.fleetdispatch.assignment.SmallestFitStrategy;
import com.junia.fleetdispatch.exception.NoVehicleAvailableException;
import com.junia.fleetdispatch.exception.UnknownZoneException;
import com.junia.fleetdispatch.model.CargoBike;
import com.junia.fleetdispatch.model.Delivery;
import com.junia.fleetdispatch.model.Drone;
import com.junia.fleetdispatch.model.Parcel;
import com.junia.fleetdispatch.model.Van;
import com.junia.fleetdispatch.model.Vehicle;
import com.junia.fleetdispatch.repository.InMemoryVehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DispatchServiceTest {

    private Drone drone;
    private CargoBike bike;
    private Van van;
    private DispatchService service;

    @BeforeEach
    void setUp() {
        drone = new Drone("DR-1", 5, 40);
        bike = new CargoBike("CB-1", 30, 40);
        van = new Van("VN-1", 800, 400);

        FixedDistanceProvider distances = new FixedDistanceProvider();
        distances.addZone("NORTH", 5);   // round trip: 10 km
        distances.addZone("FAR", 150);   // round trip: 300 km

        service = new DispatchService(
                new InMemoryVehicleRepository(List.of(van, drone, bike)),
                distances,
                new CheapestVehicleStrategy());
    }

    private Parcel parcel(String id, double weightKg, String zone) {
        return new Parcel.Builder(id, weightKg, zone).build();
    }

    // --- normal situations ---

    @Test
    void shouldAssignTheCheapestVehicleThatCanDoTheJob() throws Exception {
        Delivery delivery = service.dispatch(parcel("P-1", 3, "NORTH"));

        assertEquals("CB-1", delivery.getVehicle().getId());
    }

    @Test
    void shouldComputePriceCostAndMargin() throws Exception {
        Delivery delivery = service.dispatch(parcel("P-1", 3, "NORTH"));

        // price: 3.0 + 5 * 0.8 + 3 * 0.1 = 7.3 ; cost: 10 km * 0.15 = 1.5
        assertEquals(7.3, delivery.getPrice(), 0.001);
        assertEquals(1.5, delivery.getCost(), 0.001);
        assertEquals(5.8, delivery.getMargin(), 0.001);
    }

    @Test
    void shouldApplyExpressPricing() throws Exception {
        Parcel express = new Parcel.Builder("P-2", 3, "NORTH").express().build();

        Delivery delivery = service.dispatch(express);

        assertEquals(10.95, delivery.getPrice(), 0.001);
    }

    @Test
    void shouldMarkTheVehicleBusyAndConsumeItsRange() throws Exception {
        service.dispatch(parcel("P-1", 3, "NORTH"));

        assertTrue(bike.isBusy());
        assertEquals(30, bike.getRemainingRangeKm(), 0.001);
    }

    @Test
    void shouldUseAnotherVehicleWhenTheBestOneIsBusy() throws Exception {
        service.dispatch(parcel("P-1", 3, "NORTH"));

        Delivery second = service.dispatch(parcel("P-2", 3, "NORTH"));

        assertEquals("DR-1", second.getVehicle().getId());
    }

    @Test
    void shouldSendHeavyParcelToTheVan() throws Exception {
        Delivery delivery = service.dispatch(parcel("P-3", 100, "NORTH"));

        assertEquals("VN-1", delivery.getVehicle().getId());
    }

    @Test
    void shouldSendFarParcelToTheOnlyVehicleWithEnoughRange() throws Exception {
        Delivery delivery = service.dispatch(parcel("P-4", 3, "FAR"));

        assertEquals("VN-1", delivery.getVehicle().getId());
    }

    @Test
    void shouldChangeTheChoiceWhenTheStrategyIsSwapped() throws Exception {
        service.setAssignmentStrategy(new SmallestFitStrategy());

        Delivery delivery = service.dispatch(parcel("P-5", 3, "NORTH"));

        assertEquals("DR-1", delivery.getVehicle().getId());
    }

    @Test
    void shouldReleaseAndRefillTheVehicleWhenDeliveryIsCompleted() throws Exception {
        Delivery delivery = service.dispatch(parcel("P-1", 3, "NORTH"));

        service.completeDelivery(delivery);

        assertTrue(delivery.isDelivered());
        assertFalse(bike.isBusy());
        assertEquals(40, bike.getRemainingRangeKm(), 0.001);
    }

    @Test
    void shouldRefuelAVanWhenItsDeliveryIsCompleted() throws Exception {
        Delivery delivery = service.dispatch(parcel("P-6", 100, "NORTH"));

        service.completeDelivery(delivery);

        assertEquals(400, van.getRemainingRangeKm(), 0.001);
    }

    @Test
    void shouldKeepTrackOfDeliveries() throws Exception {
        service.dispatch(parcel("P-1", 3, "NORTH"));
        service.dispatch(parcel("P-2", 3, "NORTH"));

        assertEquals(2, service.getDeliveries().size());
    }

    // --- edge cases ---

    @Test
    void shouldAcceptParcelExactlyAtTheMaxLoadOfTheBestVehicle() throws Exception {
        Delivery delivery = service.dispatch(parcel("P-7", 30, "NORTH"));

        assertEquals("CB-1", delivery.getVehicle().getId());
    }

    @Test
    void shouldGoBackToNormalWhenTheBusyVehicleIsReleased() throws Exception {
        Delivery first = service.dispatch(parcel("P-1", 3, "NORTH"));
        service.completeDelivery(first);

        Delivery second = service.dispatch(parcel("P-2", 3, "NORTH"));

        assertEquals("CB-1", second.getVehicle().getId());
    }

    // --- invalid situations ---

    @Test
    void shouldThrowWhenNoVehicleCanCarryTheParcel() {
        assertThrows(NoVehicleAvailableException.class, () -> service.dispatch(parcel("P-8", 2000, "NORTH")));
    }

    @Test
    void shouldThrowWhenAllVehiclesAreBusy() throws Exception {
        service.dispatch(parcel("P-1", 3, "NORTH"));
        service.dispatch(parcel("P-2", 3, "NORTH"));
        service.dispatch(parcel("P-3", 3, "NORTH"));

        assertThrows(NoVehicleAvailableException.class, () -> service.dispatch(parcel("P-4", 3, "NORTH")));
    }

    @Test
    void shouldThrowWhenOnlyADroneIsAvailableForAHeavyFragileParcel() {
        DispatchService droneOnly = new DispatchService(
                new InMemoryVehicleRepository(List.of((Vehicle) drone)),
                distancesWithNorth(),
                new CheapestVehicleStrategy());
        Parcel fragile = new Parcel.Builder("P-9", 3, "NORTH").fragile().build();

        assertThrows(NoVehicleAvailableException.class, () -> droneOnly.dispatch(fragile));
    }

    @Test
    void shouldThrowForAnUnknownZone() {
        assertThrows(UnknownZoneException.class, () -> service.dispatch(parcel("P-10", 3, "MOON")));
    }

    @Test
    void shouldNotChangeAnyVehicleWhenDispatchFails() {
        assertThrows(NoVehicleAvailableException.class, () -> service.dispatch(parcel("P-8", 2000, "NORTH")));

        assertFalse(bike.isBusy());
        assertFalse(drone.isBusy());
        assertFalse(van.isBusy());
    }

    @Test
    void shouldRefuseToCompleteTheSameDeliveryTwice() throws Exception {
        Delivery delivery = service.dispatch(parcel("P-1", 3, "NORTH"));
        service.completeDelivery(delivery);

        assertThrows(IllegalStateException.class, () -> service.completeDelivery(delivery));
    }

    // --- good practice: failures are isolated ---

    @Test
    void shouldContinueWithOtherParcelsWhenOneFails() {
        List<Parcel> parcels = List.of(
                parcel("P-1", 3, "NORTH"),
                parcel("P-2", 2000, "NORTH"),   // too heavy for the whole fleet
                parcel("P-3", 3, "MOON"),       // unknown zone
                parcel("P-4", 3, "NORTH"));

        DispatchSummary summary = service.dispatchAll(parcels);

        assertEquals(2, summary.getAssigned().size());
        assertEquals(2, summary.getFailures().size());
    }

    @Test
    void shouldExplainWhichParcelFailed() {
        DispatchSummary summary = service.dispatchAll(List.of(parcel("P-2", 2000, "NORTH")));

        assertTrue(summary.getFailures().get(0).contains("P-2"));
    }

    private FixedDistanceProvider distancesWithNorth() {
        FixedDistanceProvider distances = new FixedDistanceProvider();
        distances.addZone("NORTH", 5);
        return distances;
    }
}