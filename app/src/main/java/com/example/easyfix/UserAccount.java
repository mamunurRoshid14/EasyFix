package com.example.easyfix;

import java.util.List;

public class UserAccount {
    private String fullName;
    private String phoneNumber;
    private String userId;
    private String imageUrl;

    // No-argument constructor required for Firestore
    public UserAccount() {}

    public UserAccount(String fullName, String phoneNumber, String userId, String imageUrl) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.imageUrl = imageUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
