package com.junia.fleetdispatch.repository;

import com.junia.fleetdispatch.exception.DataLoadException;
import com.junia.fleetdispatch.exception.InvalidParcelException;
import com.junia.fleetdispatch.model.Parcel;
import com.junia.fleetdispatch.utils.CsvParser;

import java.util.ArrayList;
import java.util.List;

/** Expected columns: id,weightKg,zone,fragile,express,insuredValue. */
public class CsvParcelRepository implements ParcelRepository {

    private static final int EXPECTED_COLUMNS = 6;

    private final List<Parcel> parcels = new ArrayList<>();

    public CsvParcelRepository(List<String> lines) throws DataLoadException {
        List<String[]> rows = CsvParser.toRows(lines);
        for (int i = 0; i < rows.size(); i++) {
            Parcel parcel = parseParcel(rows.get(i), i + 1);
            if (parcels.contains(parcel)) { // relies on Parcel.equals
                throw new DataLoadException("Parcel row " + (i + 1) + ": duplicate id " + parcel.getId());
            }
            parcels.add(parcel);
        }
    }

    private Parcel parseParcel(String[] row, int rowNumber) throws DataLoadException {
        if (row.length != EXPECTED_COLUMNS) {
            throw new DataLoadException("Parcel row " + rowNumber + ": expected " + EXPECTED_COLUMNS
                    + " columns but found " + row.length);
        }
        try {
            Parcel.Builder builder = new Parcel.Builder(row[0], Integer.parseInt(row[1]), row[2]);
            if (row[3].equals("true")) {
                builder.fragile();
            }
            if (row[4].equals("true")) {
                builder.express();
            }
            builder.insuredValue(Integer.parseInt(row[5]));
            return builder.build();
        } catch (NumberFormatException e) {
            throw new DataLoadException("Parcel row " + rowNumber + ": invalid number (" + e.getMessage() + ")", e);
        } catch (InvalidParcelException e) {
            throw new DataLoadException("Parcel row " + rowNumber + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<Parcel> findAll() {
        return new ArrayList<>(parcels);
    }
}