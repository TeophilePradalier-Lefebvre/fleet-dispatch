package com.junia.fleetdispatch.view;

import com.junia.fleetdispatch.model.Delivery;
import com.junia.fleetdispatch.model.Vehicle;
import com.junia.fleetdispatch.service.DispatchSummary;
import com.junia.fleetdispatch.service.ReportService;
import com.junia.fleetdispatch.utils.MoneyFormatter;

import java.util.List;

/** Display things in the console.
 * It only formats what the services computed. */
public class ConsoleView {

    public void printTitle(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }

    public void printSummary(DispatchSummary summary) {
        for (Delivery delivery : summary.getAssigned()) {
            System.out.println("  OK    " + delivery.getParcel().getId()
                    + " -> " + delivery.getVehicle().getId()
                    + " | price " + MoneyFormatter.format(delivery.getPrice())
                    + " | cost " + MoneyFormatter.format(delivery.getCost()));
        }
        for (String failure : summary.getFailures()) {
            System.out.println("  FAIL  " + failure);
        }
    }

    public void printReport(ReportService report, List<Delivery> deliveries, List<Vehicle> fleet) {
        printTitle("Report");
        System.out.println("  Delivered parcels : " + report.countDelivered(deliveries));
        System.out.println("  Revenue           : " + MoneyFormatter.format(report.totalRevenue(deliveries)));
        System.out.println("  Cost              : " + MoneyFormatter.format(report.totalCost(deliveries)));
        System.out.println("  Margin            : " + MoneyFormatter.format(report.totalMargin(deliveries)));
        System.out.println("  Fleet utilisation : " + (int) (report.utilizationRate(fleet, deliveries) * 100) + " %");
        report.mostProfitable(deliveries).ifPresent(best ->
                System.out.println("  Most profitable   : " + best.getParcel().getId()
                        + " (margin " + MoneyFormatter.format(best.getMargin()) + ")"));
        for (Vehicle vehicle : fleet) {
            System.out.println("  " + vehicle.getId() + " : " + report.countDeliveriesOf(vehicle, deliveries) + " delivery(ies)");
        }
    }

    public void printLines(String title, List<String> lines) {
        printTitle(title);
        for (String line : lines) {
            System.out.println("  " + line);
        }
    }
}