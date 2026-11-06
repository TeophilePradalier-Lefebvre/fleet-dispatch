package com.junia.fleetdispatch.adapter;

import com.junia.fleetdispatch.exception.DataLoadException;
import com.junia.fleetdispatch.exception.UnknownZoneException;
import com.junia.fleetdispatch.service.DistanceProvider;
import com.junia.fleetdispatch.utils.CsvFileReader;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LegacyDistanceAdapterTest {

    private static final String HEADER = "zone,distanceMiles";

    private DistanceProvider adapterFor(List<String> lines) throws DataLoadException {
        return new LegacyDistanceAdapter(new LegacyZoneService(lines));
    }

    // --- normal situations ---

    @Test
    void shouldConvertMilesToKilometres() throws Exception {
        DistanceProvider adapter = adapterFor(List.of(HEADER, "NORTH,10"));

        assertEquals(16.09, adapter.distanceKm("NORTH"), 0.001);
    }

    @Test
    void shouldFindEachZone() throws Exception {
        DistanceProvider adapter = adapterFor(List.of(HEADER, "NORTH,10", "SOUTH,20"));

        assertEquals(32.18, adapter.distanceKm("SOUTH"), 0.001);
    }

    @Test
    void shouldLoadTheShippedZonesFile() throws Exception {
        List<String> lines = CsvFileReader.readLines(Path.of("src/main/resources/zones.csv"));
        DistanceProvider adapter = adapterFor(lines);

        assertEquals(4.827, adapter.distanceKm("NORTH"), 0.001);
        assertEquals(1.609, adapter.distanceKm("CENTER"), 0.001);
    }

    // --- edge cases ---

    @Test
    void shouldAcceptAZeroDistanceZone() throws Exception {
        DistanceProvider adapter = adapterFor(List.of(HEADER, "DEPOT,0"));

        assertEquals(0, adapter.distanceKm("DEPOT"), 0.001);
    }

    // --- invalid situations ---

    @Test
    void shouldTurnTheLegacyMinusOneIntoAnException() throws Exception {
        DistanceProvider adapter = adapterFor(List.of(HEADER, "NORTH,10"));

        assertThrows(UnknownZoneException.class, () -> adapter.distanceKm("MOON"));
    }

    @Test
    void shouldFailOnNonNumericDistance() {
        assertThrows(DataLoadException.class, () -> adapterFor(List.of(HEADER, "NORTH,far")));
    }

    @Test
    void shouldFailOnWrongNumberOfColumns() {
        assertThrows(DataLoadException.class, () -> adapterFor(List.of(HEADER, "NORTH")));
    }

    @Test
    void shouldFailOnDuplicateZone() {
        assertThrows(DataLoadException.class, () -> adapterFor(List.of(HEADER, "NORTH,10", "NORTH,12")));
    }
}