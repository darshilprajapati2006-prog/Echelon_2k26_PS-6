package com.echelon.forensics.model;

public class MediaRecord {
    private final String path;
    private final String category;

    public MediaRecord(String path, String category) {
        this.path = path;
        this.category = category;
    }

    public String getPath() {
        return path;
    }

    public String getCategory() {
        return category;
    }

    public String toDisplayString() {
        return String.format("Category: %s, Path: %s", category, path);
    }
}
