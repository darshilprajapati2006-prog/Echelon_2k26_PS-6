package com.echelon.forensics.model;

import java.util.List;

public class InvestigationResult {
    private final DeviceInfo deviceInfo;
    private final List<CallRecord> calls;
    private final List<MessageRecord> messages;
    private final List<ContactRecord> contacts;
    private final List<AppRecord> apps;
    private final List<MediaRecord> mediaFiles;
    private final List<LocationRecord> locations;
    private final List<CallRecord> suspiciousCalls;
    private final List<MessageRecord> suspiciousMessages;
    private final List<AppRecord> suspiciousApps;
    private final List<LocationRecord> suspiciousLocations;
    private final List<ForensicRecord> timestampAnomalies;
    private final List<TimelineEvent> timeline;

    public InvestigationResult(
            DeviceInfo deviceInfo,
            List<CallRecord> calls,
            List<MessageRecord> messages,
            List<ContactRecord> contacts,
            List<AppRecord> apps,
            List<MediaRecord> mediaFiles,
            List<LocationRecord> locations,
            List<CallRecord> suspiciousCalls,
            List<MessageRecord> suspiciousMessages,
            List<AppRecord> suspiciousApps,
            List<LocationRecord> suspiciousLocations,
            List<ForensicRecord> timestampAnomalies,
            List<TimelineEvent> timeline) {
        this.deviceInfo = deviceInfo;
        this.calls = calls;
        this.messages = messages;
        this.contacts = contacts;
        this.apps = apps;
        this.mediaFiles = mediaFiles;
        this.locations = locations;
        this.suspiciousCalls = suspiciousCalls;
        this.suspiciousMessages = suspiciousMessages;
        this.suspiciousApps = suspiciousApps;
        this.suspiciousLocations = suspiciousLocations;
        this.timestampAnomalies = timestampAnomalies;
        this.timeline = timeline;
    }

    public DeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public List<CallRecord> getCalls() {
        return calls;
    }

    public List<MessageRecord> getMessages() {
        return messages;
    }

    public List<ContactRecord> getContacts() {
        return contacts;
    }

    public List<AppRecord> getApps() {
        return apps;
    }

    public List<MediaRecord> getMediaFiles() {
        return mediaFiles;
    }

    public List<LocationRecord> getLocations() {
        return locations;
    }

    public List<CallRecord> getSuspiciousCalls() {
        return suspiciousCalls;
    }

    public List<MessageRecord> getSuspiciousMessages() {
        return suspiciousMessages;
    }

    public List<AppRecord> getSuspiciousApps() {
        return suspiciousApps;
    }

    public List<LocationRecord> getSuspiciousLocations() {
        return suspiciousLocations;
    }

    public List<ForensicRecord> getTimestampAnomalies() {
        return timestampAnomalies;
    }

    public List<TimelineEvent> getTimeline() {
        return timeline;
    }
}
