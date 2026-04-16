package com.echelon.forensics.model;

import java.time.LocalDateTime;

public class TimelineEvent implements Comparable<TimelineEvent> {
    private final LocalDateTime timestamp;
    private final String type;
    private final Severity severity;
    private final String description;

    public TimelineEvent(LocalDateTime timestamp, String type, Severity severity, String description) {
        this.timestamp = timestamp;
        this.type = type;
        this.severity = severity;
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int compareTo(TimelineEvent other) {
        return this.timestamp.compareTo(other.timestamp);
    }

    @Override
    public String toString() {
        return String.format("[%s] [%s] %s -> %s",
                type,
                severity,
                timestamp.format(ForensicRecord.FORMATTER),
                description);
    }
}
