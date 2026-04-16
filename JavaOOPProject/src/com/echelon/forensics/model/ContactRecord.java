package com.echelon.forensics.model;

public class ContactRecord {
    private final String name;
    private final String phoneNumber;

    public ContactRecord(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String toDisplayString() {
        return String.format("Name: %s, Phone: %s", name, phoneNumber);
    }
}
