package com.echelon.forensics.service;

import com.echelon.forensics.model.AppRecord;
import com.echelon.forensics.model.CallRecord;
import com.echelon.forensics.model.ContactRecord;
import com.echelon.forensics.model.DeviceInfo;
import com.echelon.forensics.model.ExtractionBundle;
import com.echelon.forensics.model.MediaRecord;
import com.echelon.forensics.model.MessageRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvExporter {
    public void exportAll(ExtractionBundle bundle, Path outputDirectory) throws IOException {
        Files.createDirectories(outputDirectory);

        writeDeviceInfo(bundle.getDeviceInfo(), outputDirectory.resolve("device_info.csv"));
        writeCalls(bundle.getCalls(), outputDirectory.resolve("calls.csv"));
        writeMessages(bundle.getMessages(), outputDirectory.resolve("messages.csv"));
        writeContacts(bundle.getContacts(), outputDirectory.resolve("contacts.csv"));
        writeApps(bundle.getApps(), outputDirectory.resolve("apps.csv"));
        writeMedia(bundle.getMediaFiles(), outputDirectory.resolve("media.csv"));
    }

    private void writeDeviceInfo(DeviceInfo deviceInfo, Path outputFile) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("serial_number,manufacturer,model,android_version,sdk_version");
        lines.add(csv(deviceInfo.getSerialNumber()) + "," +
                csv(deviceInfo.getManufacturer()) + "," +
                csv(deviceInfo.getModel()) + "," +
                csv(deviceInfo.getAndroidVersion()) + "," +
                csv(deviceInfo.getSdkVersion()));
        Files.write(outputFile, lines);
    }

    private void writeCalls(List<CallRecord> records, Path outputFile) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("number,time,duration");
        for (CallRecord record : records) {
            lines.add(csv(record.getNumber()) + "," + csv(record.getFormattedTimestamp()) + "," + record.getDurationSeconds());
        }
        Files.write(outputFile, lines);
    }

    private void writeMessages(List<MessageRecord> records, Path outputFile) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("sender,message,time");
        for (MessageRecord record : records) {
            lines.add(csv(record.getSender()) + "," + csv(record.getMessage()) + "," + csv(record.getFormattedTimestamp()));
        }
        Files.write(outputFile, lines);
    }

    private void writeContacts(List<ContactRecord> records, Path outputFile) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("name,phone_number");
        for (ContactRecord record : records) {
            lines.add(csv(record.getName()) + "," + csv(record.getPhoneNumber()));
        }
        Files.write(outputFile, lines);
    }

    private void writeApps(List<AppRecord> records, Path outputFile) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("app_name,permission");
        for (AppRecord record : records) {
            lines.add(csv(record.getAppName()) + "," + csv(record.getPermission()));
        }
        Files.write(outputFile, lines);
    }

    private void writeMedia(List<MediaRecord> records, Path outputFile) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("category,path");
        for (MediaRecord record : records) {
            lines.add(csv(record.getCategory()) + "," + csv(record.getPath()));
        }
        Files.write(outputFile, lines);
    }

    private String csv(String value) {
        String safeValue = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + safeValue + "\"";
    }
}
