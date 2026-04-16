package com.echelon.forensics.service;

import com.echelon.forensics.model.AppRecord;
import com.echelon.forensics.model.CallRecord;
import com.echelon.forensics.model.ContactRecord;
import com.echelon.forensics.model.DeviceInfo;
import com.echelon.forensics.model.ExtractionBundle;
import com.echelon.forensics.model.MediaRecord;
import com.echelon.forensics.model.MessageRecord;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveDataExtractor {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final AdbClient adbClient;

    public LiveDataExtractor(AdbClient adbClient) {
        this.adbClient = adbClient;
    }

    public ExtractionBundle extractAll() throws IOException, InterruptedException {
        DeviceInfo deviceInfo = fetchDeviceInfo();
        List<CallRecord> calls = fetchCallLogs();
        List<MessageRecord> messages = fetchMessages();
        List<ContactRecord> contacts = fetchContacts();
        List<AppRecord> apps = fetchApps();
        List<MediaRecord> mediaFiles = fetchMediaFiles();

        return new ExtractionBundle(deviceInfo, calls, messages, contacts, apps, mediaFiles);
    }

    private DeviceInfo fetchDeviceInfo() throws IOException, InterruptedException {
        String serial = adbClient.getConnectedSerial();
        String manufacturer = singleLine(adbClient.shell("getprop", "ro.product.manufacturer"));
        String model = singleLine(adbClient.shell("getprop", "ro.product.model"));
        String androidVersion = singleLine(adbClient.shell("getprop", "ro.build.version.release"));
        String sdkVersion = singleLine(adbClient.shell("getprop", "ro.build.version.sdk"));
        return new DeviceInfo(serial, manufacturer, model, androidVersion, sdkVersion);
    }

    private List<CallRecord> fetchCallLogs() throws IOException, InterruptedException {
        List<CallRecord> records = new ArrayList<>();
        List<String> lines = adbClient.shell(
                "content",
                "query",
                "--uri",
                "content://call_log/calls",
                "--projection",
                "number:date:duration"
        );
        for (String line : lines) {
            Map<String, String> row = parseContentRow(line);
            String number = row.get("number");
            String date = row.get("date");
            String duration = row.get("duration");
            if (number != null && date != null && duration != null) {
                records.add(new CallRecord(number, formatEpochMillis(date), parseInteger(duration)));
            }
        }
        return records;
    }

    private List<MessageRecord> fetchMessages() throws IOException, InterruptedException {
        List<MessageRecord> records = new ArrayList<>();
        List<String> lines = adbClient.shell(
                "content",
                "query",
                "--uri",
                "content://sms",
                "--projection",
                "address:body:date"
        );
        for (String line : lines) {
            Map<String, String> row = parseContentRow(line);
            String address = row.get("address");
            String body = row.get("body");
            String date = row.get("date");
            if (address != null && body != null && date != null) {
                records.add(new MessageRecord(address, body, formatEpochMillis(date)));
            }
        }
        return records;
    }

    private List<ContactRecord> fetchContacts() throws IOException, InterruptedException {
        List<ContactRecord> records = new ArrayList<>();
        List<String> lines = adbClient.shell(
                "content",
                "query",
                "--uri",
                "content://com.android.contacts/data/phones",
                "--projection",
                "display_name:data1"
        );
        for (String line : lines) {
            Map<String, String> row = parseContentRow(line);
            String name = row.get("display_name");
            String phone = row.get("data1");
            if (name != null && phone != null) {
                records.add(new ContactRecord(name, phone));
            }
        }
        return records;
    }

    private List<AppRecord> fetchApps() throws IOException, InterruptedException {
        List<AppRecord> records = new ArrayList<>();
        List<String> lines = adbClient.shell("pm", "list", "packages", "-3");
        for (String line : lines) {
            if (line.startsWith("package:")) {
                records.add(new AppRecord(line.substring("package:".length()).trim(), "Third-Party"));
            }
        }
        return records;
    }

    private List<MediaRecord> fetchMediaFiles() throws IOException, InterruptedException {
        List<MediaRecord> records = new ArrayList<>();
        collectMediaFromDirectory(records, "/sdcard/DCIM");
        collectMediaFromDirectory(records, "/sdcard/Pictures");
        collectMediaFromDirectory(records, "/sdcard/Download");
        return records;
    }

    private void collectMediaFromDirectory(List<MediaRecord> records, String directory) throws IOException, InterruptedException {
        List<String> lines;
        try {
            lines = adbClient.shell("ls", "-1", directory);
        } catch (IOException exception) {
            return;
        }

        int limit = Math.min(lines.size(), 40);
        for (int index = 0; index < limit; index++) {
            String name = lines.get(index).trim();
            if (!name.isEmpty()) {
                records.add(new MediaRecord(directory + "/" + name, detectCategory(name)));
            }
        }
    }

    private Map<String, String> parseContentRow(String line) {
        Map<String, String> values = new HashMap<>();
        if (line == null || !line.contains("Row:")) {
            return values;
        }

        String[] tokens = line.split(", ");
        for (String token : tokens) {
            int equalsIndex = token.indexOf('=');
            if (equalsIndex > 0) {
                String key = token.substring(0, equalsIndex).trim();
                String value = token.substring(equalsIndex + 1).trim();
                if (key.startsWith("Row:")) {
                    String[] fragments = key.split(" ");
                    key = fragments[fragments.length - 1];
                }
                values.put(key, value);
            }
        }
        return values;
    }

    private String formatEpochMillis(String millisText) {
        long millis = Long.parseLong(millisText.trim());
        LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
        return timestamp.format(FORMATTER);
    }

    private int parseInteger(String value) {
        return Integer.parseInt(value.trim());
    }

    private String detectCategory(String path) {
        String lowerCasePath = path.toLowerCase();
        if (lowerCasePath.endsWith(".jpg") || lowerCasePath.endsWith(".jpeg") || lowerCasePath.endsWith(".png")) {
            return "Image";
        }
        if (lowerCasePath.endsWith(".mp4") || lowerCasePath.endsWith(".mov") || lowerCasePath.endsWith(".mkv")) {
            return "Video";
        }
        if (lowerCasePath.endsWith(".pdf") || lowerCasePath.endsWith(".doc") || lowerCasePath.endsWith(".docx")) {
            return "Document";
        }
        return "File";
    }

    private String singleLine(List<String> lines) {
        if (lines.isEmpty()) {
            return "UNKNOWN";
        }
        String value = lines.get(0).trim();
        return value.isEmpty() ? "UNKNOWN" : value;
    }
}
