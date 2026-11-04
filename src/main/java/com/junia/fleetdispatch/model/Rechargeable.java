package com.junia.fleetdispatch.model;

/**
 * Capability of vehicles powered by a battery.
 * It is an interface because only some vehicles have it
 * a diesel van or a pedal bike cannot be recharged.
 */
public interface Rechargeable {
    void recharge();
}