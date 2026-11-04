package com.junia.fleetdispatch.model;

import com.junia.fleetdispatch.exception.InvalidParcelException;

/**
 * A parcel to deliver. It is immutable so once built it can never change.
 *
 * It has 3 mandatory fields and 3 optional ones so a constructor with 6 parameters would be
 * hard to read. The Builder names each value and validates
 * everything in one place before the object exists.
 */

public class Parcel {

    private final String id;
    private final double weightKg;
    private final String zone;
    private final boolean fragile;
    private final boolean express;
    private final double insuredValue;

    // Private: The only way to create a Parcel is through the Builder which guarantees validation.
    private Parcel(Builder builder) {
        this.id = builder.id;
        this.weightKg = builder.weightKg;
        this.zone = builder.zone;
        this.fragile = builder.fragile;
        this.express = builder.express;
        this.insuredValue = builder.insuredValue;
    }

    public String getId() {
        return id;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public String getZone() {
        return zone;
    }

    public boolean isFragile() {
        return fragile;
    }

    public boolean isExpress() {
        return express;
    }

    public double getInsuredValue() {
        return insuredValue;
    }

    public boolean isInsured() {
        return insuredValue > 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Parcel other)) {
            return false;
        }
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Parcel[id=" + id + ", weightKg=" + weightKg + ", zone=" + zone
                + ", fragile=" + fragile + ", express=" + express + ", insuredValue=" + insuredValue + "]";
    }

    public static class Builder {

        private final String id;
        private final double weightKg;
        private final String zone;
        private boolean fragile = false;
        private boolean express = false;
        private double insuredValue = 0;

        public Builder(String id, double weightKg, String zone) {
            this.id = id;
            this.weightKg = weightKg;
            this.zone = zone;
        }

        public Builder fragile() {
            this.fragile = true;
            return this;
        }

        public Builder express() {
            this.express = true;
            return this;
        }

        public Builder insuredValue(double insuredValue) {
            this.insuredValue = insuredValue;
            return this;
        }

        public Parcel build() {
            if (id == null || id.isEmpty()) {
                throw new InvalidParcelException("Parcel id cannot be empty.");
            }
            if (weightKg <= 0) {
                throw new InvalidParcelException("Parcel " + id + ": weight must be strictly positive.");
            }
            if (zone == null || zone.isEmpty()) {
                throw new InvalidParcelException("Parcel " + id + ": zone cannot be empty.");
            }
            if (insuredValue < 0) {
                throw new InvalidParcelException("Parcel " + id + ": insured value cannot be negative.");
            }
            return new Parcel(this);
        }
    }
}