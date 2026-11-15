package com.junia.fleetdispatch.model;

/** Capability of vehicles powered by fuel.
 * Kept separate from Rechargeable to ensure that
 * no class implements a method it does not need. */

public interface Refuelable {
    void refuel();
}