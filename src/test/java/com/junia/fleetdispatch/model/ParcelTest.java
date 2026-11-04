package com.junia.fleetdispatch.model;

import com.junia.fleetdispatch.exception.InvalidParcelException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParcelTest {

    // --- normal situations ---

    @Test
    void shouldBuildParcelWithDefaultOptions() {
        Parcel parcel = new Parcel.Builder("P-1", 2.5, "NORTH").build();

        assertEquals("P-1", parcel.getId());
        assertEquals(2.5, parcel.getWeightKg(), 0.001);
        assertEquals("NORTH", parcel.getZone());
        assertFalse(parcel.isFragile());
        assertFalse(parcel.isExpress());
        assertFalse(parcel.isInsured());
    }

    @Test
    void shouldBuildParcelWithAllOptions() {
        Parcel parcel = new Parcel.Builder("P-2", 4, "SOUTH")
                .fragile()
                .express()
                .insuredValue(150)
                .build();

        assertTrue(parcel.isFragile());
        assertTrue(parcel.isExpress());
        assertTrue(parcel.isInsured());
        assertEquals(150, parcel.getInsuredValue(), 0.001);
    }

    @Test
    void shouldConsiderParcelsWithSameIdAsEqual() {
        Parcel first = new Parcel.Builder("P-3", 1, "EAST").build();
        Parcel second = new Parcel.Builder("P-3", 9, "WEST").build();

        assertEquals(first, second);
    }

    // --- edge cases ---

    @Test
    void shouldAcceptVeryLightParcel() {
        Parcel parcel = new Parcel.Builder("P-4", 0.001, "NORTH").build();

        assertEquals(0.001, parcel.getWeightKg(), 0.0001);
    }

    @Test
    void shouldAcceptZeroInsuredValueAsNotInsured() {
        Parcel parcel = new Parcel.Builder("P-5", 1, "NORTH").insuredValue(0).build();

        assertFalse(parcel.isInsured());
    }

    // --- invalid situations ---

    @Test
    void shouldRejectZeroWeight() {
        assertThrows(InvalidParcelException.class, () -> new Parcel.Builder("P-6", 0, "NORTH").build());
    }

    @Test
    void shouldRejectNegativeWeight() {
        assertThrows(InvalidParcelException.class, () -> new Parcel.Builder("P-7", -3, "NORTH").build());
    }

    @Test
    void shouldRejectEmptyId() {
        assertThrows(InvalidParcelException.class, () -> new Parcel.Builder("", 1, "NORTH").build());
    }

    @Test
    void shouldRejectEmptyZone() {
        assertThrows(InvalidParcelException.class, () -> new Parcel.Builder("P-8", 1, "").build());
    }

    @Test
    void shouldRejectNegativeInsuredValue() {
        assertThrows(InvalidParcelException.class,
                () -> new Parcel.Builder("P-9", 1, "NORTH").insuredValue(-10).build());
    }

    @Test
    void shouldExplainWhichRuleWasBroken() {
        InvalidParcelException e = assertThrows(InvalidParcelException.class,
                () -> new Parcel.Builder("P-10", -1, "NORTH").build());

        assertTrue(e.getMessage().contains("weight"));
    }
}