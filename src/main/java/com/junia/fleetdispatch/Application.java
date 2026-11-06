package com.junia.fleetdispatch;

import com.junia.fleetdispatch.adapter.LegacyDistanceAdapter;
import com.junia.fleetdispatch.adapter.LegacyZoneService;
import com.junia.fleetdispatch.assignment.CheapestVehicleStrategy;
import com.junia.fleetdispatch.assignment.SmallestFitStrategy;
import com.junia.fleetdispatch.event.AuditLog;
import com.junia.fleetdispatch.event.CustomerNotifier;
import com.junia.fleetdispatch.exception.DataLoadException;
import com.junia.fleetdispatch.repository.CsvParcelRepository;
import com.junia.fleetdispatch.repository.CsvVehicleRepository;
import com.junia.fleetdispatch.repository.ParcelRepository;
import com.junia.fleetdispatch.repository.VehicleRepository;
import com.junia.fleetdispatch.service.DispatchService;
import com.junia.fleetdispatch.service.DispatchSummary;
import com.junia.fleetdispatch.service.DistanceProvider;
import com.junia.fleetdispatch.service.ReportService;
import com.junia.fleetdispatch.utils.CsvFileReader;
import com.junia.fleetdispatch.view.ConsoleView;

import java.nio.file.Path;

/**
 * Build the objects and connect them then run the flow.
 * Every rule lives in the services and every display in ConsoleView.
 * Run it from the project root so the relative file paths below are found.
 */
public class Application {

    private static final Path VEHICLES_FILE = Path.of("src/main/resources/vehicles.csv");
    private static final Path PARCELS_FILE = Path.of("src/main/resources/parcels.csv");
    private static final Path ZONES_FILE = Path.of("src/main/resources/zones.csv");

    public static void main(String[] args) {
        try {
            run();
        } catch (DataLoadException e) {
            // Without its data the application cannot work: we stop with a clear message.
            System.out.println("The application cannot start: " + e.getMessage());
        }
    }

    private static void run() throws DataLoadException {
        // 1. Load the data from files
        VehicleRepository vehicles = new CsvVehicleRepository(CsvFileReader.readLines(VEHICLES_FILE));
        ParcelRepository parcels = new CsvParcelRepository(CsvFileReader.readLines(PARCELS_FILE));
        DistanceProvider distances = new LegacyDistanceAdapter(
                new LegacyZoneService(CsvFileReader.readLines(ZONES_FILE)));

        // 2. Build and connect the services
        DispatchService dispatchService = new DispatchService(vehicles, distances, new CheapestVehicleStrategy());
        CustomerNotifier notifier = new CustomerNotifier();
        AuditLog auditLog = new AuditLog();
        dispatchService.addListener(notifier);
        dispatchService.addListener(auditLog);
        ReportService reportService = new ReportService();
        ConsoleView view = new ConsoleView();

        // 3. Round 1: dispatch every parcel, choosing the cheapest vehicle
        view.printTitle("Round 1 - cheapest vehicle strategy");
        DispatchSummary firstRound = dispatchService.dispatchAll(parcels.findAll());
        view.printSummary(firstRound);
        dispatchService.completeAllDeliveries();

        // 4. Round 2: vehicles are back at the depot, retry the failed parcels with another strategy
        view.printTitle("Round 2 - retry failed parcels, smallest fit strategy");
        dispatchService.setAssignmentStrategy(new SmallestFitStrategy());
        DispatchSummary secondRound = dispatchService.dispatchAll(firstRound.getFailedParcels());
        view.printSummary(secondRound);
        dispatchService.completeAllDeliveries();

        // 5. Display results
        view.printReport(reportService, dispatchService.getDeliveries(), vehicles.findAll());
        view.printLines("Customer notifications", notifier.getMessages());
        view.printLines("Audit log", auditLog.getEntries());
    }
}