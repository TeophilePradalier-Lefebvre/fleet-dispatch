package com.junia.fleetdispatch.model;

/** The result of assigning a parcel to a vehicle
 *  what is charged to the customer and what it costs us. */

public class Delivery {

    private final Parcel parcel;
    private final Vehicle vehicle;
    private final double distanceKm;
    private final double price;
    private final double cost;
    private boolean delivered;

    public Delivery(Parcel parcel, Vehicle vehicle, double distanceKm, double price, double cost) {
        this.parcel = parcel;
        this.vehicle = vehicle;
        this.distanceKm = distanceKm;
        this.price = price;
        this.cost = cost;
        this.delivered = false;
    }

    public void markDelivered() {
        if (delivered) {
            throw new IllegalStateException("Parcel " + parcel.getId() + " is already delivered.");
        }
        delivered = true;
    }

    public double getMargin() {
        return price - cost;
    }

    public Parcel getParcel() {
        return parcel;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getPrice() {
        return price;
    }

    public double getCost() {
        return cost;
    }

    public boolean isDelivered() {
        return delivered;
    }

    @Override
    public String toString() {
        return "Delivery[parcel=" + parcel.getId() + ", vehicle=" + vehicle.getId()
                + ", price=" + price + ", cost=" + cost + ", delivered=" + delivered + "]";
    }
}