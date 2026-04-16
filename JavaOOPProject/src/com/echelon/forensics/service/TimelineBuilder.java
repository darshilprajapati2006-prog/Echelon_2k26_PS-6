package com.echelon.forensics.service;

import com.echelon.forensics.model.CallRecord;
import com.echelon.forensics.model.LocationRecord;
import com.echelon.forensics.model.MessageRecord;
import com.echelon.forensics.model.Severity;
import com.echelon.forensics.model.TimelineEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimelineBuilder {
    public List<TimelineEvent> buildTimeline(
            List<CallRecord> calls,
            List<MessageRecord> messages,
            List<LocationRecord> locations) {
        List<TimelineEvent> timeline = new ArrayList<>();

        for (CallRecord call : calls) {
            timeline.add(new TimelineEvent(
                    call.getTimestamp(),
                    call.getRecordType(),
                    isNightTime(call.getTimestamp().getHour()) ? Severity.HIGH : Severity.NORMAL,
                    String.format("Call to %s (Duration %d sec)", call.getNumber(), call.getDurationSeconds())
            ));
        }

        for (MessageRecord message : messages) {
            String content = message.getMessage().toLowerCase();
            Severity severity = content.contains("link") || "unknown".equalsIgnoreCase(message.getSender())
                    ? Severity.HIGH
                    : Severity.NORMAL;
            timeline.add(new TimelineEvent(
                    message.getTimestamp(),
                    message.getRecordType(),
                    severity,
                    String.format("Message from %s: %s", message.getSender(), message.getMessage())
            ));
        }

        for (LocationRecord location : locations) {
            timeline.add(new TimelineEvent(
                    location.getTimestamp(),
                    location.getRecordType(),
                    isNightTime(location.getTimestamp().getHour()) ? Severity.HIGH : Severity.NORMAL,
                    String.format("Location changed to (%.4f, %.4f)", location.getLatitude(), location.getLongitude())
            ));
        }

        Collections.sort(timeline);
        return timeline;
    }

    private boolean isNightTime(int hour) {
        return hour >= 0 && hour <= 5;
    }
}
