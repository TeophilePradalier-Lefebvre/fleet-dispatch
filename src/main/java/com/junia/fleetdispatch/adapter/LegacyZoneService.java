package com.junia.fleetdispatch.adapter;

import com.junia.fleetdispatch.exception.DataLoadException;
import com.junia.fleetdispatch.utils.CsvParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simulates an existing system we cannot change
 * it speaks in miles and signals an unknown zone by returning -1 instead of throwing an exception.
 * Our code needs kilometres and exceptions hence the adapter below.
 * Expected columns: zone,distanceMiles
 */
public class LegacyZoneService {

    private final Map<String, Integer> milesByZone = new HashMap<>();

    public LegacyZoneService(List<String> lines) throws DataLoadException {
        List<String[]> rows = CsvParser.toRows(lines);
        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            if (row.length != 2) {
                throw new DataLoadException("Zone row " + (i + 1) + ": expected 2 columns but found " + row.length);
            }
            if (milesByZone.containsKey(row[0])) {
                throw new DataLoadException("Zone row " + (i + 1) + ": duplicate zone " + row[0]);
            }
            try {
                milesByZone.put(row[0], Integer.parseInt(row[1]));
            } catch (NumberFormatException e) {
                throw new DataLoadException("Zone row " + (i + 1) + ": invalid distance " + row[1], e);
            }
        }
    }

    /** Legacy contract: -1 when the zone is unknown. */
    public int getDistanceInMiles(String zone) {
        return milesByZone.containsKey(zone) ? milesByZone.get(zone) : -1;
    }
}