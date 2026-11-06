package com.junia.fleetdispatch.utils;

import com.junia.fleetdispatch.exception.DataLoadException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Read the lines of a file. It knows nothing about vehicles or parcels
 * and the repositories know nothing about files.
 */
public class CsvFileReader {

    public static List<String> readLines(Path path) throws DataLoadException {
        try {
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // We translate a technical error into a domain error and keep the cause.
            throw new DataLoadException("Unable to read file: " + path, e);
        }
    }
}