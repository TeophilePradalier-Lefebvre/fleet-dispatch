package com.junia.fleetdispatch.service;

import com.junia.fleetdispatch.model.CargoBike;
import com.junia.fleetdispatch.model.Delivery;
import com.junia.fleetdispatch.model.Drone;
import com.junia.fleetdispatch.model.Parcel;
import com.junia.fleetdispatch.model.Van;
import com.junia.fleetdispatch.model.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReportServiceTest {

    private ReportService report;
    private Vehicle bike;
    private Vehicle drone;
    private Vehicle van;
    private Delivery cheapDelivered;     // price 10, cost 4, margin 6
    private Delivery goodDelivered;      // price 20, cost 5, margin 15
    private Delivery notYetDelivered;    // price 100, cost 1 so must not count
    private List<Delivery> deliveries;

    @BeforeEach
    void setUp() {
        report = new ReportService();
        bike = new CargoBike("CB-1", 30, 40);
        drone = new Drone("DR-1", 5, 20);
        van = new Van("VN-1", 800, 400);

        cheapDelivered = delivery("P-1", bike, 10, 4);
        goodDelivered = delivery("P-2", drone, 20, 5);
        notYetDelivered = delivery("P-3", bike, 100, 1);
        cheapDelivered.markDelivered();
        goodDelivered.markDelivered();

        deliveries = List.of(cheapDelivered, goodDelivered, notYetDelivered);
    }

    private Delivery delivery(String parcelId, Vehicle vehicle, double price, double cost) {
        Parcel parcel = new Parcel.Builder(parcelId, 1, "NORTH").build();
        return new Delivery(parcel, vehicle, 5, price, cost);
    }

    // --- normal situations ---

    @Test
    void shouldSumTheRevenueOfDeliveredParcelsOnly() {
        assertEquals(30, report.totalRevenue(deliveries), 0.001);
    }

    @Test
    void shouldSumTheCostOfDeliveredParcelsOnly() {
        assertEquals(9, report.totalCost(deliveries), 0.001);
    }

    @Test
    void shouldComputeTheMargin() {
        assertEquals(21, report.totalMargin(deliveries), 0.001);
    }

    @Test
    void shouldCountDeliveredParcels() {
        assertEquals(2, report.countDelivered(deliveries));
    }

    @Test
    void shouldCountDeliveriesOfAVehicle() {
        assertEquals(2, report.countDeliveriesOf(bike, deliveries));
        assertEquals(0, report.countDeliveriesOf(van, deliveries));
    }

    @Test
    void shouldFindTheMostProfitableDeliveredParcel() {
        String id = report.mostProfitable(deliveries).map(d -> d.getParcel().getId()).orElse("none");

        assertEquals("P-2", id);
    }

    @Test
    void shouldComputeTheFleetUtilizationRate() {
        // The bike and the drone have deliveries to make but the van has none
        // that makes 2 out of 3 vehicles
        assertEquals(2.0 / 3, report.utilizationRate(List.of(bike, drone, van), deliveries), 0.001);
    }

    // --- edge cases ---

    @Test
    void shouldReturnZeroTotalsWhenThereIsNoDelivery() {
        assertEquals(0, report.totalRevenue(List.of()), 0.001);
        assertEquals(0, report.totalMargin(List.of()), 0.001);
        assertEquals(0, report.countDelivered(List.of()));
    }

    @Test
    void shouldReturnNothingAsMostProfitableWhenNothingIsDelivered() {
        String id = report.mostProfitable(List.of(notYetDelivered)).map(d -> d.getParcel().getId()).orElse("none");

        assertEquals("none", id);
    }

    @Test
    void shouldIgnoreAnUndeliveredParcelEvenIfItIsTheMostProfitable() {
        String id = report.mostProfitable(deliveries).map(d -> d.getParcel().getId()).orElse("none");

        assertEquals("P-2", id); // P-3 has a margin of 99 but is not delivered
    }

    @Test
    void shouldReturnZeroUtilizationForAnEmptyFleet() {
        assertEquals(0, report.utilizationRate(List.of(), deliveries), 0.001);
    }

    @Test
    void shouldReturnZeroUtilizationWhenNoVehicleWasUsed() {
        assertEquals(0, report.utilizationRate(List.of(bike, drone), List.of()), 0.001);
    }
}