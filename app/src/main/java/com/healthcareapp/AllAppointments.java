package com.healthcareapp;

public class AllAppointments {
    private String userId;
    private String date;
    private String time;
    private String notes;

    public AllAppointments() {
        // Empty constructor needed for Firebase
    }

    public AllAppointments(String userId, String date, String time, String notes) {
        this.userId = userId;
        this.date = date;
        this.time = time;
        this.notes = notes;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
