package com.echelon.forensics.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class ForensicRecord {
    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final LocalDateTime timestamp;

    protected ForensicRecord(String timestamp) {
        this.timestamp = LocalDateTime.parse(timestamp, FORMATTER);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(FORMATTER);
    }

    public abstract String getRecordType();

    public abstract String toDisplayString();
}
