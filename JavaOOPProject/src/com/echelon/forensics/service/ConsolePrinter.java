package com.echelon.forensics.service;

import com.echelon.forensics.model.AppRecord;
import com.echelon.forensics.model.ContactRecord;
import com.echelon.forensics.model.DeviceInfo;
import com.echelon.forensics.model.ForensicRecord;
import com.echelon.forensics.model.MediaRecord;
import com.echelon.forensics.model.TimelineEvent;

import java.util.List;

public class ConsolePrinter {
    private static final int PREVIEW_LIMIT = 20;

    public void printRecordSection(String title, List<? extends ForensicRecord> records) {
        System.out.println("\n=== " + title + " ===");
        if (records.isEmpty()) {
            System.out.println("No data found");
            return;
        }
        int limit = Math.min(records.size(), PREVIEW_LIMIT);
        System.out.println("Showing " + limit + " of " + records.size() + " records");
        for (int index = 0; index < limit; index++) {
            System.out.println((index + 1) + ". " + records.get(index).toDisplayString());
        }
        if (records.size() > limit) {
            System.out.println("... additional records saved in CSV export");
        }
    }

    public void printAppSection(String title, List<AppRecord> apps) {
        System.out.println("\n=== " + title + " ===");
        if (apps.isEmpty()) {
            System.out.println("No data found");
            return;
        }
        int limit = Math.min(apps.size(), PREVIEW_LIMIT);
        System.out.println("Showing " + limit + " of " + apps.size() + " records");
        for (int index = 0; index < limit; index++) {
            System.out.println((index + 1) + ". " + apps.get(index).toDisplayString());
        }
        if (apps.size() > limit) {
            System.out.println("... additional records saved in CSV export");
        }
    }

    public void printDeviceInfo(DeviceInfo deviceInfo) {
        System.out.println("\n=== DEVICE INFO ===");
        System.out.println(deviceInfo.toDisplayString());
    }

    public void printContacts(String title, List<ContactRecord> contacts) {
        System.out.println("\n=== " + title + " ===");
        if (contacts.isEmpty()) {
            System.out.println("No data found");
            return;
        }
        int limit = Math.min(contacts.size(), PREVIEW_LIMIT);
        System.out.println("Showing " + limit + " of " + contacts.size() + " records");
        for (int index = 0; index < limit; index++) {
            System.out.println((index + 1) + ". " + contacts.get(index).toDisplayString());
        }
        if (contacts.size() > limit) {
            System.out.println("... additional records saved in CSV export");
        }
    }

    public void printMedia(String title, List<MediaRecord> mediaFiles) {
        System.out.println("\n=== " + title + " ===");
        if (mediaFiles.isEmpty()) {
            System.out.println("No data found");
            return;
        }
        int limit = Math.min(mediaFiles.size(), PREVIEW_LIMIT);
        System.out.println("Showing " + limit + " of " + mediaFiles.size() + " records");
        for (int index = 0; index < limit; index++) {
            System.out.println((index + 1) + ". " + mediaFiles.get(index).toDisplayString());
        }
        if (mediaFiles.size() > limit) {
            System.out.println("... additional records saved in CSV export");
        }
    }

    public void printTimeline(List<TimelineEvent> timeline) {
        System.out.println("\n=== TIMELINE (Chronological) ===");
        if (timeline.isEmpty()) {
            System.out.println("No timeline events available");
            return;
        }
        int limit = Math.min(timeline.size(), PREVIEW_LIMIT);
        System.out.println("Showing " + limit + " of " + timeline.size() + " records");
        for (int index = 0; index < limit; index++) {
            System.out.println((index + 1) + ". " + timeline.get(index));
        }
        if (timeline.size() > limit) {
            System.out.println("... additional timeline events saved in report");
        }
    }
}
