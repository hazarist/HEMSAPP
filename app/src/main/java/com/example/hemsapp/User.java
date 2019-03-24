package com.example.hemsapp;

public class User {

    private String Name;
    private String Surname;
    private String fullName;
    private String birthDay;
    private String email;
    private String position;
    private String status;
    private String image;
    private String thumbImage;
    private String gender;
    private String deviceToken;
    private Boolean online;
    private long lastSeen;

    public User(String name, String surname,String birthDay, String email, String position, String status, String image, String gender,String thumbImage, String deviceToken) {
        this.Name = name;
        this.Surname = surname;
        this.birthDay = birthDay;
        this.email = email;
        this.position = position;
        this.status = status;
        this.image = image;
        this.gender = gender;
        this.thumbImage = thumbImage;
        this.deviceToken = deviceToken;
    }

    public User() {} // Firebase Required

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getFullName(){
        return this.Name + " " + this.Surname;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
