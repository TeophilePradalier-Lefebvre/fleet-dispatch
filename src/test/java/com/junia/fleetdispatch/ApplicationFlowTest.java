package com.junia.fleetdispatch;

import com.junia.fleetdispatch.adapter.LegacyDistanceAdapter;
import com.junia.fleetdispatch.adapter.LegacyZoneService;
import com.junia.fleetdispatch.assignment.CheapestVehicleStrategy;
import com.junia.fleetdispatch.assignment.SmallestFitStrategy;
import com.junia.fleetdispatch.event.AuditLog;
import com.junia.fleetdispatch.exception.DataLoadException;
import com.junia.fleetdispatch.model.Delivery;
import com.junia.fleetdispatch.repository.CsvParcelRepository;
import com.junia.fleetdispatch.repository.CsvVehicleRepository;
import com.junia.fleetdispatch.repository.ParcelRepository;
import com.junia.fleetdispatch.repository.VehicleRepository;
import com.junia.fleetdispatch.service.DispatchService;
import com.junia.fleetdispatch.service.DispatchSummary;
import com.junia.fleetdispatch.service.ReportService;
import com.junia.fleetdispatch.utils.CsvFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Same wiring as Application with the real data files.
 * It checks that data so services and rules work together (as announced in the README).
 */
class ApplicationFlowTest {

    private VehicleRepository vehicles;
    private ParcelRepository parcels;
    private DispatchService service;
    private AuditLog auditLog;

    @BeforeEach
    void setUp() throws DataLoadException {
        vehicles = new CsvVehicleRepository(CsvFileReader.readLines(Path.of("src/main/resources/vehicles.csv")));
        parcels = new CsvParcelRepository(CsvFileReader.readLines(Path.of("src/main/resources/parcels.csv")));
        LegacyDistanceAdapter distances = new LegacyDistanceAdapter(
                new LegacyZoneService(CsvFileReader.readLines(Path.of("src/main/resources/zones.csv"))));
        service = new DispatchService(vehicles, distances, new CheapestVehicleStrategy());
        auditLog = new AuditLog();
        service.addListener(auditLog);
    }

    @Test
    void firstRoundShouldAssignSixParcelsAndFailTwo() {
        DispatchSummary summary = service.dispatchAll(parcels.findAll());

        assertEquals(6, summary.getAssigned().size());
        assertEquals(2, summary.getFailures().size());
    }

    @Test
    void firstRoundShouldFailBecauseOfWeightAndBecauseEveryVehicleIsBusy() {
        DispatchSummary summary = service.dispatchAll(parcels.findAll());

        assertEquals("P-006", summary.getFailedParcels().get(0).getId()); // 1500 kg: too heavy for the fleet
        assertEquals("P-008", summary.getFailedParcels().get(1).getId()); // fleet fully busy
    }

    @Test
    void cheapestStrategyShouldPreferBikesThenDronesThenVans() {
        DispatchSummary summary = service.dispatchAll(parcels.findAll());

        assertEquals("CB-01", summary.getAssigned().get(0).getVehicle().getId());
        assertEquals("CB-02", summary.getAssigned().get(1).getVehicle().getId());
        assertEquals("DR-01", summary.getAssigned().get(2).getVehicle().getId());
    }

    @Test
    void fragileParcelAboveTwoKilosShouldNotGoToADrone() {
        DispatchSummary summary = service.dispatchAll(parcels.findAll());

        // P-005 weighs 4 kg and is fragile: DR-02 is free but refuses it, so a van takes it
        assertEquals("VN-02", summary.getAssigned().get(4).getVehicle().getId());
    }

    @Test
    void secondRoundShouldDeliverTheParcelThatWaitedForAFreeVehicle() {
        DispatchSummary first = service.dispatchAll(parcels.findAll());
        service.completeAllDeliveries();

        service.setAssignmentStrategy(new SmallestFitStrategy());
        DispatchSummary second = service.dispatchAll(first.getFailedParcels());

        assertEquals(1, second.getAssigned().size());
        assertEquals("DR-01", second.getAssigned().get(0).getVehicle().getId());
        assertEquals(1, second.getFailures().size()); // P-006 can never be delivered
    }

    @Test
    void reportShouldShowAPositiveMarginOnceEverythingIsDelivered() {
        DispatchSummary first = service.dispatchAll(parcels.findAll());
        service.completeAllDeliveries();
        service.dispatchAll(first.getFailedParcels());
        service.completeAllDeliveries();

        ReportService report = new ReportService();
        assertEquals(7, report.countDelivered(service.getDeliveries()));
        assertTrue(report.totalMargin(service.getDeliveries()) > 0);
    }

    @Test
    void everyDeliveryShouldBeLoggedWhenAssignedAndWhenCompleted() {
        service.dispatchAll(parcels.findAll());
        service.completeAllDeliveries();

        assertEquals(12, auditLog.getEntries().size()); // 6 assigned + 6 delivered
    }

    @Test
    void vehiclesShouldBeAvailableAgainAfterTheirDeliveriesAreCompleted() {
        service.dispatchAll(parcels.findAll());
        service.completeAllDeliveries();

        for (Delivery delivery : service.getDeliveries()) {
            assertEquals(delivery.getVehicle().getMaxRangeKm(), delivery.getVehicle().getRemainingRangeKm(), 0.001);
        }
    }
}