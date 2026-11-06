package com.junia.fleetdispatch.service;

import com.junia.fleetdispatch.model.Delivery;
import com.junia.fleetdispatch.model.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * Calculates statistics from a list of deliveries without storing data or printing anything.
 * Keeping calculations separate from display makes each task easier to manage.
 * Only DELIVERED parcels count as revenue since an assigned parcel has not been earned yet.
 */

public class ReportService {

    public double totalRevenue(List<Delivery> deliveries) {
        double total = 0;
        for (Delivery delivery : deliveries) {
            if (delivery.isDelivered()) {
                total += delivery.getPrice();
            }
        }
        return total;
    }

    public double totalCost(List<Delivery> deliveries) {
        double total = 0;
        for (Delivery delivery : deliveries) {
            if (delivery.isDelivered()) {
                total += delivery.getCost();
            }
        }
        return total;
    }

    public double totalMargin(List<Delivery> deliveries) {
        return totalRevenue(deliveries) - totalCost(deliveries);
    }

    public long countDelivered(List<Delivery> deliveries) {
        return deliveries.stream()
                .filter(Delivery::isDelivered)
                .count();
    }

    public long countDeliveriesOf(Vehicle vehicle, List<Delivery> deliveries) {
        return deliveries.stream()
                .filter(delivery -> delivery.getVehicle().equals(vehicle))
                .count();
    }

    /** The delivered parcel with the best margin or an empty Optional when nothing was delivered. */
    public Optional<Delivery> mostProfitable(List<Delivery> deliveries) {
        Delivery best = null;
        for (Delivery delivery : deliveries) {
            if (delivery.isDelivered() && (best == null || delivery.getMargin() > best.getMargin())) {
                best = delivery;
            }
        }
        return Optional.ofNullable(best);
    }

    /** Share of the fleet that did at least one delivery between 0 and 1. */
    public double utilizationRate(List<Vehicle> fleet, List<Delivery> deliveries) {
        if (fleet.isEmpty()) {
            return 0;
        }
        int usedVehicles = 0;
        for (Vehicle vehicle : fleet) {
            if (countDeliveriesOf(vehicle, deliveries) > 0) {
                usedVehicles++;
            }
        }
        return (double) usedVehicles / fleet.size();
    }
}