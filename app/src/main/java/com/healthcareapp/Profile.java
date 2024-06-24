package com.healthcareapp;

public class Profile {
    private String image;
    private String name;
    private String email;
    private String password;

    // Default constructor required for calls to DataSnapshot.getValue(Profile.class)
    public Profile() {
    }

    public Profile(String image, String name, String email, String password) {
        this.image = image;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters and setters
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
