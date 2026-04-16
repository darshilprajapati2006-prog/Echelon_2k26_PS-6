package com.echelon.forensics.model;

public class CallRecord extends ForensicRecord {
    private final String number;
    private final int durationSeconds;

    public CallRecord(String number, String timestamp, int durationSeconds) {
        super(timestamp);
        this.number = number;
        this.durationSeconds = durationSeconds;
    }

    public String getNumber() {
        return number;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    @Override
    public String getRecordType() {
        return "CALL";
    }

    @Override
    public String toDisplayString() {
        return String.format("Number: %s, Time: %s, Duration: %d sec", number, getFormattedTimestamp(), durationSeconds);
    }
}
