package com.echelon.forensics.service;

import com.echelon.forensics.model.AppRecord;
import com.echelon.forensics.model.CallRecord;
import com.echelon.forensics.model.LocationRecord;
import com.echelon.forensics.model.MessageRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvDataLoader {
    private final Path dataDirectory;

    public CsvDataLoader(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public List<CallRecord> loadCalls() throws IOException {
        List<CallRecord> calls = new ArrayList<>();
        for (String[] fields : readCsv("calls.csv")) {
            calls.add(new CallRecord(fields[0], fields[1], Integer.parseInt(fields[2])));
        }
        return calls;
    }

    public List<MessageRecord> loadMessages() throws IOException {
        List<MessageRecord> messages = new ArrayList<>();
        for (String[] fields : readCsv("messages.csv")) {
            messages.add(new MessageRecord(fields[0], fields[1], fields[2]));
        }
        return messages;
    }

    public List<AppRecord> loadApps() throws IOException {
        List<AppRecord> apps = new ArrayList<>();
        for (String[] fields : readCsv("apps.csv")) {
            apps.add(new AppRecord(fields[0], fields[1]));
        }
        return apps;
    }

    public List<LocationRecord> loadLocations() throws IOException {
        List<LocationRecord> locations = new ArrayList<>();
        for (String[] fields : readCsv("location.csv")) {
            locations.add(new LocationRecord(Double.parseDouble(fields[0]), Double.parseDouble(fields[1]), fields[2]));
        }
        return locations;
    }

    private List<String[]> readCsv(String fileName) throws IOException {
        Path filePath = dataDirectory.resolve(fileName);
        List<String[]> rows = new ArrayList<>();
        List<String> lines = Files.readAllLines(filePath);
        for (int index = 1; index < lines.size(); index++) {
            String line = lines.get(index).trim();
            if (!line.isEmpty()) {
                rows.add(line.split(",", -1));
            }
        }
        return rows;
    }
}
