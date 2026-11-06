package com.junia.fleetdispatch.service;

import com.junia.fleetdispatch.assignment.AssignmentStrategy;
import com.junia.fleetdispatch.event.DeliveryListener;
import com.junia.fleetdispatch.exception.NoVehicleAvailableException;
import com.junia.fleetdispatch.exception.UnknownZoneException;
import com.junia.fleetdispatch.model.Delivery;
import com.junia.fleetdispatch.model.Parcel;
import com.junia.fleetdispatch.model.Rechargeable;
import com.junia.fleetdispatch.model.Refuelable;
import com.junia.fleetdispatch.model.Vehicle;
import com.junia.fleetdispatch.pricing.PricingPolicy;
import com.junia.fleetdispatch.pricing.PricingPolicyFactory;
import com.junia.fleetdispatch.repository.VehicleRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates the dispatch of parcels
 * It depends only on abstractions so it knows neither CSV files nor concrete vehicles nor who reacts to events
 */
public class DispatchService {

    private final VehicleRepository vehicleRepository;
    private final DistanceProvider distanceProvider;
    private AssignmentStrategy assignmentStrategy;
    private final List<Delivery> deliveries = new ArrayList<>();
    private final List<DeliveryListener> listeners = new ArrayList<>();

    public DispatchService(VehicleRepository vehicleRepository,
                           DistanceProvider distanceProvider,
                           AssignmentStrategy assignmentStrategy) {
        this.vehicleRepository = vehicleRepository;
        this.distanceProvider = distanceProvider;
        this.assignmentStrategy = assignmentStrategy;
    }

    /** The strategy can be swapped at runtime without touching the rest of the service */
    public void setAssignmentStrategy(AssignmentStrategy assignmentStrategy) {
        this.assignmentStrategy = assignmentStrategy;
    }

    public void addListener(DeliveryListener listener) {
        listeners.add(listener);
    }

    public Delivery dispatch(Parcel parcel) throws NoVehicleAvailableException, UnknownZoneException {
        double distanceKm = distanceProvider.distanceKm(parcel.getZone());
        double roundTripKm = distanceKm * 2; // the vehicle must come back to the depot

        List<Vehicle> candidates = vehicleRepository.findAll().stream()
                .filter(vehicle -> vehicle.canDeliver(parcel, roundTripKm))
                .toList();

        Vehicle vehicle = assignmentStrategy.select(candidates)
                .orElseThrow(() -> new NoVehicleAvailableException(
                        "No vehicle available for parcel " + parcel.getId()));

        PricingPolicy pricingPolicy = PricingPolicyFactory.forParcel(parcel);
        double price = pricingPolicy.calculatePrice(parcel, distanceKm);
        double cost = roundTripKm * vehicle.costPerKm();

        vehicle.markBusy();
        vehicle.consumeRange(roundTripKm);

        Delivery delivery = new Delivery(parcel, vehicle, distanceKm, price, cost);
        deliveries.add(delivery);
        for (DeliveryListener listener : listeners) {
            listener.onDeliveryAssigned(delivery);
        }
        return delivery;
    }

    /** One impossible parcel must not stop the others making sure each failure is recorded and the loop continues. */
    public DispatchSummary dispatchAll(List<Parcel> parcels) {
        DispatchSummary summary = new DispatchSummary();
        for (Parcel parcel : parcels) {
            try {
                summary.addAssigned(dispatch(parcel));
            } catch (NoVehicleAvailableException e) {
                summary.addFailure(parcel, parcel.getId() + ": " + e.getMessage());
            } catch (UnknownZoneException e) {
                summary.addFailure(parcel, parcel.getId() + ": " + e.getMessage());
            }
        }
        return summary;
    }

    /** The parcel is delivered then the vehicle is released and goes back to the depot to be recharged or refuelled */
    public void completeDelivery(Delivery delivery) {
        delivery.markDelivered();
        Vehicle vehicle = delivery.getVehicle();
        vehicle.release();
        refill(vehicle);
        for (DeliveryListener listener : listeners) {
            listener.onDeliveryCompleted(delivery);
        }
    }

    public void completeAllDeliveries() {
        for (Delivery delivery : getDeliveries()) {
            if (!delivery.isDelivered()) {
                completeDelivery(delivery);
            }
        }
    }

    private void refill(Vehicle vehicle) {
        if (vehicle instanceof Rechargeable rechargeable) {
            rechargeable.recharge();
        } else if (vehicle instanceof Refuelable refuelable) {
            refuelable.refuel();
        }
    }

    public List<Delivery> getDeliveries() {
        return new ArrayList<>(deliveries);
    }
}