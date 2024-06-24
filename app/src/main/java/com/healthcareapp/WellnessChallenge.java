package com.healthcareapp;

public class WellnessChallenge {

    private String challengeId;
    private String title;
    private String duration;
    private String description;

    public WellnessChallenge() {
        // Default constructor required for Firebase
    }

    public WellnessChallenge(String challengeId, String title, String duration, String description) {
        this.challengeId = challengeId;
        this.title = title;
        this.duration = duration;
        this.description = description;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(String challengeId) {
        this.challengeId = challengeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
