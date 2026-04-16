package com.echelon.forensics.service;

import com.echelon.forensics.model.AppRecord;
import com.echelon.forensics.model.CallRecord;
import com.echelon.forensics.model.ForensicRecord;
import com.echelon.forensics.model.LocationRecord;
import com.echelon.forensics.model.MessageRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ForensicAnalyzer {
    public List<CallRecord> findSuspiciousCalls(List<CallRecord> calls) {
        List<CallRecord> suspiciousCalls = new ArrayList<>();
        for (CallRecord call : calls) {
            if (isNightTime(call.getTimestamp())) {
                suspiciousCalls.add(call);
            }
        }
        return suspiciousCalls;
    }

    public List<MessageRecord> findSuspiciousMessages(List<MessageRecord> messages) {
        List<MessageRecord> suspiciousMessages = new ArrayList<>();
        for (MessageRecord message : messages) {
            String content = message.getMessage().toLowerCase();
            if (content.contains("link") || "unknown".equalsIgnoreCase(message.getSender())) {
                suspiciousMessages.add(message);
            }
        }
        return suspiciousMessages;
    }

    public List<AppRecord> findSuspiciousApps(List<AppRecord> apps) {
        List<AppRecord> suspiciousApps = new ArrayList<>();
        for (AppRecord app : apps) {
            if ("full access".equalsIgnoreCase(app.getPermission())) {
                suspiciousApps.add(app);
            }
        }
        return suspiciousApps;
    }

    public List<LocationRecord> findSuspiciousLocations(List<LocationRecord> locations) {
        List<LocationRecord> suspiciousLocations = new ArrayList<>();
        for (LocationRecord location : locations) {
            if (isNightTime(location.getTimestamp())) {
                suspiciousLocations.add(location);
            }
        }
        return suspiciousLocations;
    }

    public List<ForensicRecord> findTimestampAnomalies(List<? extends ForensicRecord> records) {
        List<ForensicRecord> anomalies = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (ForensicRecord record : records) {
            if (record.getTimestamp().isAfter(now)) {
                anomalies.add(record);
            }
        }
        return anomalies;
    }

    private boolean isNightTime(LocalDateTime time) {
        int hour = time.getHour();
        return hour >= 0 && hour <= 5;
    }
}
