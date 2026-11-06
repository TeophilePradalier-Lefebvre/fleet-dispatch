package com.junia.fleetdispatch.service;

import com.junia.fleetdispatch.model.Parcel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DispatchSummaryTest {

    @Test
    void shouldStartEmpty() {
        DispatchSummary summary = new DispatchSummary();

        assertTrue(summary.getAssigned().isEmpty());
        assertTrue(summary.getFailures().isEmpty());
        assertTrue(summary.getFailedParcels().isEmpty());
    }

    @Test
    void shouldKeepTheFailedParcelAndItsReason() {
        DispatchSummary summary = new DispatchSummary();
        Parcel parcel = new Parcel.Builder("P-1", 5, "NORTH").build();

        summary.addFailure(parcel, "P-1: no vehicle");

        assertEquals(parcel, summary.getFailedParcels().get(0));
        assertEquals("P-1: no vehicle", summary.getFailures().get(0));
    }

    @Test
    void shouldReturnCopiesOfItsLists() {
        DispatchSummary summary = new DispatchSummary();
        summary.addFailure(new Parcel.Builder("P-1", 5, "NORTH").build(), "reason");

        summary.getFailures().clear();
        summary.getFailedParcels().clear();

        assertEquals(1, summary.getFailures().size());
        assertEquals(1, summary.getFailedParcels().size());
    }
}