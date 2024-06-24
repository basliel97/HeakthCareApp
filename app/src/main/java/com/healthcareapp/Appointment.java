package com.healthcareapp;

public class Appointment {
    private String id; // Add this field
    private String user_id;
    private String date;
    private String time;
    private String notes;

    public Appointment() {
        // Default constructor required for calls to DataSnapshot.getValue(Appointment.class)
    }

    public Appointment(String id, String user_id, String date, String time, String notes) {
        this.id = id; // Initialize this field
        this.user_id = user_id;
        this.date = date;
        this.time = time;
        this.notes = notes;
    }

    // Getter and setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
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
