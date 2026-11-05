package com.junia.fleetdispatch.repository;

import com.junia.fleetdispatch.exception.DataLoadException;
import com.junia.fleetdispatch.factory.VehicleFactory;
import com.junia.fleetdispatch.model.Vehicle;
import com.junia.fleetdispatch.utils.CsvParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Expected columns: type,id,maxLoadKg,rangeKm. */
public class CsvVehicleRepository implements VehicleRepository {

    private static final int EXPECTED_COLUMNS = 4;

    private final List<Vehicle> vehicles = new ArrayList<>();

    public CsvVehicleRepository(List<String> lines) throws DataLoadException {
        List<String[]> rows = CsvParser.toRows(lines);
        for (int i = 0; i < rows.size(); i++) {
            Vehicle vehicle = parseVehicle(rows.get(i), i + 1);
            if (findById(vehicle.getId()).isPresent()) {
                throw new DataLoadException("Vehicle row " + (i + 1) + ": duplicate id " + vehicle.getId());
            }
            vehicles.add(vehicle);
        }
    }

    private Vehicle parseVehicle(String[] row, int rowNumber) throws DataLoadException {
        if (row.length != EXPECTED_COLUMNS) {
            throw new DataLoadException("Vehicle row " + rowNumber + ": expected " + EXPECTED_COLUMNS
                    + " columns but found " + row.length);
        }
        try {
            int maxLoadKg = Integer.parseInt(row[2]);
            int rangeKm = Integer.parseInt(row[3]);
            return VehicleFactory.create(row[0], row[1], maxLoadKg, rangeKm);
        } catch (IllegalArgumentException e) {
            // NumberFormatException is a subclass of IllegalArgumentException so one catch covers
            // both a non-numeric value and a value refused by the Vehicle constructor or the factory.
            throw new DataLoadException("Vehicle row " + rowNumber + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<Vehicle> findAll() {
        return new ArrayList<>(vehicles); // copy so callers cannot modify our internal list
    }

    @Override
    public Optional<Vehicle> findById(String id) {
        return vehicles.stream()
                .filter(vehicle -> vehicle.getId().equals(id))
                .findFirst();
    }
}