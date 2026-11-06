package com.junia.fleetdispatch.utils;

import com.junia.fleetdispatch.exception.DataLoadException;
import com.junia.fleetdispatch.repository.CsvParcelRepository;
import com.junia.fleetdispatch.repository.CsvVehicleRepository;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvFileReaderTest {

    @Test
    void shouldFailWhenFileDoesNotExist() {
        assertThrows(DataLoadException.class, () -> CsvFileReader.readLines(Path.of("does-not-exist.csv")));
    }

    @Test
    void shouldKeepTheIOExceptionAsCause() {
        DataLoadException e = assertThrows(DataLoadException.class,
                () -> CsvFileReader.readLines(Path.of("does-not-exist.csv")));

        assertTrue(e.getCause() instanceof java.io.IOException);
    }

    // The two tests below check that the data files really shipped with the application are valid

    @Test
    void shouldLoadTheShippedVehiclesFile() throws DataLoadException {
        List<String> lines = CsvFileReader.readLines(Path.of("src/main/resources/vehicles.csv"));

        assertEquals(6, new CsvVehicleRepository(lines).findAll().size());
    }

    @Test
    void shouldLoadTheShippedParcelsFile() throws DataLoadException {
        List<String> lines = CsvFileReader.readLines(Path.of("src/main/resources/parcels.csv"));

        assertEquals(8, new CsvParcelRepository(lines).findAll().size());
    }
}