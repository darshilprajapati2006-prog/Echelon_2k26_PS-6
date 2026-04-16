package com.echelon.forensics.model;

import java.util.List;

public class ExtractionBundle {
    private final DeviceInfo deviceInfo;
    private final List<CallRecord> calls;
    private final List<MessageRecord> messages;
    private final List<ContactRecord> contacts;
    private final List<AppRecord> apps;
    private final List<MediaRecord> mediaFiles;

    public ExtractionBundle(
            DeviceInfo deviceInfo,
            List<CallRecord> calls,
            List<MessageRecord> messages,
            List<ContactRecord> contacts,
            List<AppRecord> apps,
            List<MediaRecord> mediaFiles) {
        this.deviceInfo = deviceInfo;
        this.calls = calls;
        this.messages = messages;
        this.contacts = contacts;
        this.apps = apps;
        this.mediaFiles = mediaFiles;
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
}
