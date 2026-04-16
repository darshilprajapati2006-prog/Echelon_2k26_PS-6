package com.echelon.forensics.model;

public class MessageRecord extends ForensicRecord {
    private final String sender;
    private final String message;

    public MessageRecord(String sender, String message, String timestamp) {
        super(timestamp);
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getRecordType() {
        return "MESSAGE";
    }

    @Override
    public String toDisplayString() {
        return String.format("Sender: %s, Message: %s, Time: %s", sender, message, getFormattedTimestamp());
    }
}
