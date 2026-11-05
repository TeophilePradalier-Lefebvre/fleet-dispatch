package com.junia.fleetdispatch.repository;

import com.junia.fleetdispatch.exception.DataLoadException;
import com.junia.fleetdispatch.model.Parcel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvParcelRepositoryTest {

    private static final String HEADER = "id,weightKg,zone,fragile,express,insuredValue";

    // --- normal situations ---

    @Test
    void shouldLoadParcelWithItsOptions() throws DataLoadException {
        ParcelRepository repository = new CsvParcelRepository(
                List.of(HEADER, "P-1,4,WEST,true,true,250"));

        Parcel parcel = repository.findAll().get(0);

        assertEquals("P-1", parcel.getId());
        assertEquals(4, parcel.getWeightKg(), 0.001);
        assertEquals("WEST", parcel.getZone());
        assertTrue(parcel.isFragile());
        assertTrue(parcel.isExpress());
        assertEquals(250, parcel.getInsuredValue(), 0.001);
    }

    @Test
    void shouldLoadParcelWithoutOptions() throws DataLoadException {
        ParcelRepository repository = new CsvParcelRepository(
                List.of(HEADER, "P-2,2,NORTH,false,false,0"));

        Parcel parcel = repository.findAll().get(0);

        assertFalse(parcel.isFragile());
        assertFalse(parcel.isExpress());
        assertFalse(parcel.isInsured());
    }

    // --- edge cases ---

    @Test
    void shouldLoadNothingWhenFileHasOnlyAHeader() throws DataLoadException {
        ParcelRepository repository = new CsvParcelRepository(List.of(HEADER));

        assertTrue(repository.findAll().isEmpty());
    }

    // --- invalid situations ---

    @Test
    void shouldFailOnNegativeWeight() {
        assertThrows(DataLoadException.class,
                () -> new CsvParcelRepository(List.of(HEADER, "P-3,-2,NORTH,false,false,0")));
    }

    @Test
    void shouldFailOnNonNumericWeight() {
        assertThrows(DataLoadException.class,
                () -> new CsvParcelRepository(List.of(HEADER, "P-4,abc,NORTH,false,false,0")));
    }

    @Test
    void shouldFailOnWrongNumberOfColumns() {
        assertThrows(DataLoadException.class,
                () -> new CsvParcelRepository(List.of(HEADER, "P-5,2,NORTH")));
    }

    @Test
    void shouldFailOnDuplicateId() {
        assertThrows(DataLoadException.class,
                () -> new CsvParcelRepository(List.of(HEADER,
                        "P-6,2,NORTH,false,false,0", "P-6,3,SOUTH,false,false,0")));
    }
}