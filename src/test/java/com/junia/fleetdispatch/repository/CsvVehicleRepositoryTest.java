package com.junia.fleetdispatch.repository;

import com.junia.fleetdispatch.exception.DataLoadException;
import com.junia.fleetdispatch.model.Vehicle;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvVehicleRepositoryTest {

    private static final String HEADER = "type,id,maxLoadKg,rangeKm";

    // --- normal situations ---

    @Test
    void shouldLoadAllVehiclesFromLines() throws DataLoadException {
        VehicleRepository repository = new CsvVehicleRepository(
                List.of(HEADER, "DRONE,DR-1,5,20", "VAN,VN-1,800,400", "BIKE,CB-1,30,40"));

        assertEquals(3, repository.findAll().size());
    }

    @Test
    void shouldFindVehicleById() throws DataLoadException {
        VehicleRepository repository = new CsvVehicleRepository(List.of(HEADER, "VAN,VN-1,800,400"));

        Optional<Vehicle> found = repository.findById("VN-1");

        assertTrue(found.isPresent());
        assertEquals(800, found.get().getMaxLoadKg(), 0.001);
    }

    @Test
    void shouldReturnEmptyOptionalForUnknownId() throws DataLoadException {
        VehicleRepository repository = new CsvVehicleRepository(List.of(HEADER, "VAN,VN-1,800,400"));

        assertFalse(repository.findById("NOPE").isPresent());
    }

    @Test
    void shouldReturnACopyOfTheInternalList() throws DataLoadException {
        VehicleRepository repository = new CsvVehicleRepository(List.of(HEADER, "VAN,VN-1,800,400"));

        repository.findAll().clear();

        assertEquals(1, repository.findAll().size());
    }

    // --- edge cases ---

    @Test
    void shouldIgnoreEmptyLines() throws DataLoadException {
        VehicleRepository repository = new CsvVehicleRepository(
                List.of(HEADER, "", "DRONE,DR-1,5,20", ""));

        assertEquals(1, repository.findAll().size());
    }

    @Test
    void shouldLoadNothingWhenFileHasOnlyAHeader() throws DataLoadException {
        VehicleRepository repository = new CsvVehicleRepository(List.of(HEADER));

        assertTrue(repository.findAll().isEmpty());
    }

    // --- invalid situations ---

    @Test
    void shouldFailOnNonNumericValue() {
        assertThrows(DataLoadException.class,
                () -> new CsvVehicleRepository(List.of(HEADER, "DRONE,DR-1,heavy,20")));
    }

    @Test
    void shouldFailOnUnknownVehicleType() {
        assertThrows(DataLoadException.class,
                () -> new CsvVehicleRepository(List.of(HEADER, "TRUCK,T-1,5000,800")));
    }

    @Test
    void shouldFailOnDuplicateId() {
        assertThrows(DataLoadException.class,
                () -> new CsvVehicleRepository(List.of(HEADER, "DRONE,DR-1,5,20", "VAN,DR-1,800,400")));
    }

    @Test
    void shouldFailOnWrongNumberOfColumns() {
        assertThrows(DataLoadException.class,
                () -> new CsvVehicleRepository(List.of(HEADER, "DRONE,DR-1,5")));
    }

    @Test
    void shouldFailOnValueRefusedByTheModel() {
        assertThrows(DataLoadException.class,
                () -> new CsvVehicleRepository(List.of(HEADER, "DRONE,DR-1,-5,20")));
    }

    @Test
    void shouldKeepTheOriginalCauseOfTheError() {
        DataLoadException e = assertThrows(DataLoadException.class,
                () -> new CsvVehicleRepository(List.of(HEADER, "DRONE,DR-1,heavy,20")));

        assertTrue(e.getCause() instanceof NumberFormatException);
    }
}