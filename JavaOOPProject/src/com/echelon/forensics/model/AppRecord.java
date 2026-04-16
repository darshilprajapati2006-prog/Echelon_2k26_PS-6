package com.echelon.forensics.model;

public class AppRecord {
    private final String appName;
    private final String permission;

    public AppRecord(String appName, String permission) {
        this.appName = appName;
        this.permission = permission;
    }

    public String getAppName() {
        return appName;
    }

    public String getPermission() {
        return permission;
    }

    public String toDisplayString() {
        return String.format("App Name: %s, Permission: %s", appName, permission);
    }
}
