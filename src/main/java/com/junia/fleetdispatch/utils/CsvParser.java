package com.junia.fleetdispatch.utils;

import java.util.ArrayList;
import java.util.List;

/** Splits CSV lines into columns. Shared by every repository to avoid duplicating this logic. */
public class CsvParser {

    /** Skips the header and the empty lines then splits each remaining line on commas. */
    public static List<String[]> toRows(List<String> lines) {
        List<String[]> rows = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) { // index 0 is the header
            String line = lines.get(i);
            if (!line.isEmpty()) {
                rows.add(line.split(","));
            }
        }
        return rows;
    }
}