package com.echelon.forensics.service;

import com.echelon.forensics.model.AppRecord;
import com.echelon.forensics.model.CallRecord;
import com.echelon.forensics.model.ContactRecord;
import com.echelon.forensics.model.InvestigationResult;
import com.echelon.forensics.model.LocationRecord;
import com.echelon.forensics.model.MediaRecord;
import com.echelon.forensics.model.MessageRecord;
import com.echelon.forensics.model.TimelineEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportGenerator {
    public Path generateReport(InvestigationResult result, Path outputFile) throws IOException {
        Files.createDirectories(outputFile.getParent());

        List<String> lines = new ArrayList<>();
        lines.add("MOBILE FORENSICS INVESTIGATION REPORT");
        lines.add("============================================================");
        lines.add("Generated On : " + LocalDateTime.now());
        lines.add("");
        lines.add("0. DEVICE INFORMATION");
        lines.add("------------------------------------------------------------");
        if (result.getDeviceInfo() != null) {
            lines.add("Serial Number         : " + result.getDeviceInfo().getSerialNumber());
            lines.add("Manufacturer          : " + result.getDeviceInfo().getManufacturer());
            lines.add("Model                 : " + result.getDeviceInfo().getModel());
            lines.add("Android Version       : " + result.getDeviceInfo().getAndroidVersion());
            lines.add("SDK Version           : " + result.getDeviceInfo().getSdkVersion());
        } else {
            lines.add("Device information unavailable.");
        }
        lines.add("");
        lines.add("1. CASE SUMMARY");
        lines.add("------------------------------------------------------------");
        lines.add("This report documents the results of a mobile forensic investigation.");
        lines.add("The objective was to safely extract data, identify suspicious activity,");
        lines.add("and reconstruct a timeline using Java OOP design principles.");
        lines.add("");
        lines.add("2. EXTRACTED DATA OVERVIEW");
        lines.add("------------------------------------------------------------");
        lines.add("Total Call Records     : " + result.getCalls().size());
        lines.add("Total Messages         : " + result.getMessages().size());
        lines.add("Total Contacts         : " + result.getContacts().size());
        lines.add("Installed Applications : " + result.getApps().size());
        lines.add("Media Files Listed     : " + result.getMediaFiles().size());
        lines.add("Location Records       : " + result.getLocations().size());
        lines.add("");
        lines.add("3. SUSPICIOUS FINDINGS SUMMARY");
        lines.add("------------------------------------------------------------");
        lines.add("Suspicious Calls       : " + result.getSuspiciousCalls().size());
        lines.add("Suspicious Messages    : " + result.getSuspiciousMessages().size());
        lines.add("Suspicious Apps        : " + result.getSuspiciousApps().size());
        lines.add("Suspicious Locations   : " + result.getSuspiciousLocations().size());
        lines.add("Timestamp Anomalies    : " + result.getTimestampAnomalies().size());
        lines.add("");

        appendCallDetails(lines, result.getSuspiciousCalls());
        appendMessageDetails(lines, result.getSuspiciousMessages());
        appendAppDetails(lines, result.getSuspiciousApps());
        appendLocationDetails(lines, result.getSuspiciousLocations());
        appendContactSummary(lines, result.getContacts());
        appendMediaSummary(lines, result.getMediaFiles());

        lines.add("4. TIMELINE RECONSTRUCTION");
        lines.add("------------------------------------------------------------");
        for (TimelineEvent event : result.getTimeline()) {
            lines.add(event.toString());
        }
        lines.add("");
        lines.add("5. EVIDENCE INTEGRITY NOTE");
        lines.add("------------------------------------------------------------");
        lines.add("All analysis was performed on exported CSV evidence.");
        lines.add("The original sample data was not modified during investigation.");
        lines.add("This project is suitable for academic OOP demonstration.");

        Files.write(outputFile, lines);
        return outputFile;
    }

    private void appendCallDetails(List<String> lines, List<CallRecord> records) {
        if (records.isEmpty()) {
            return;
        }
        lines.add("3.1 Suspicious Call Details");
        for (CallRecord record : records) {
            lines.add("- " + record.toDisplayString());
        }
        lines.add("");
    }

    private void appendMessageDetails(List<String> lines, List<MessageRecord> records) {
        if (records.isEmpty()) {
            return;
        }
        lines.add("3.2 Suspicious Message Details");
        for (MessageRecord record : records) {
            lines.add("- " + record.toDisplayString());
        }
        lines.add("");
    }

    private void appendAppDetails(List<String> lines, List<AppRecord> records) {
        if (records.isEmpty()) {
            return;
        }
        lines.add("3.3 Suspicious Application Details");
        for (AppRecord record : records) {
            lines.add("- " + record.toDisplayString());
        }
        lines.add("");
    }

    private void appendLocationDetails(List<String> lines, List<LocationRecord> records) {
        if (records.isEmpty()) {
            return;
        }
        lines.add("3.4 Suspicious Location Details");
        for (LocationRecord record : records) {
            lines.add("- " + record.toDisplayString());
        }
        lines.add("");
    }

    private void appendContactSummary(List<String> lines, List<ContactRecord> records) {
        if (records.isEmpty()) {
            return;
        }
        lines.add("3.5 Contact Records");
        for (ContactRecord record : records) {
            lines.add("- " + record.toDisplayString());
        }
        lines.add("");
    }

    private void appendMediaSummary(List<String> lines, List<MediaRecord> records) {
        if (records.isEmpty()) {
            return;
        }
        lines.add("3.6 Media File Listing");
        int limit = Math.min(records.size(), 25);
        for (int index = 0; index < limit; index++) {
            lines.add("- " + records.get(index).toDisplayString());
        }
        if (records.size() > limit) {
            lines.add("- Additional media files omitted from report for brevity.");
        }
        lines.add("");
    }
}
