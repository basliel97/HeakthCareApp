package com.healthcareapp;
public class HealthcareInfo {
    private String name;
    private String dateOfBirth;
    private String allergies;
    private String currentMedication;
    private String immunization;
    private String labResults;

    // Default constructor required for calls to DataSnapshot.getValue(HealthcareInfo.class)
    public HealthcareInfo() {
    }

    public HealthcareInfo(String name, String dateOfBirth, String allergies, String currentMedication, String immunization, String labResults) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.allergies = allergies;
        this.currentMedication = currentMedication;
        this.immunization = immunization;
        this.labResults = labResults;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getCurrentMedication() {
        return currentMedication;
    }

    public void setCurrentMedication(String currentMedication) {
        this.currentMedication = currentMedication;
    }

    public String getImmunization() {
        return immunization;
    }

    public void setImmunization(String immunization) {
        this.immunization = immunization;
    }

    public String getLabResults() {
        return labResults;
    }

    public void setLabResults(String labResults) {
        this.labResults = labResults;
    }
}

