package com.healthcareapp;
public class EmergencyContact {
    private String name;
    private String phoneNumber;
    private String relationship;

    public EmergencyContact() {
        // Default constructor required for calls to DataSnapshot.getValue(EmergencyContact.class)
    }

    public EmergencyContact(String name, String phoneNumber, String relationship) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.relationship = relationship;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRelationship() {
        return relationship;
    }
}
