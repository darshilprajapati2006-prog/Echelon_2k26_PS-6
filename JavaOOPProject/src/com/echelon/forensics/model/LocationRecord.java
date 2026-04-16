package com.echelon.forensics.model;

public class LocationRecord extends ForensicRecord {
    private final double latitude;
    private final double longitude;

    public LocationRecord(double latitude, double longitude, String timestamp) {
        super(timestamp);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String getRecordType() {
        return "LOCATION";
    }

    @Override
    public String toDisplayString() {
        return String.format("Latitude: %.4f, Longitude: %.4f, Time: %s", latitude, longitude, getFormattedTimestamp());
    }
}
