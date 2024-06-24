package com.healthcareapp;

public class HealthTip {
    private String content;
    private String date;

    public HealthTip() {
        // Default constructor required for calls to DataSnapshot.getValue(HealthTip.class)
    }

    public HealthTip(String content, String date) {
        this.content = content;
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
