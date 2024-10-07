package com.example.easyfix;

import java.util.ArrayList;
import java.util.List;

public class UserAccount {
    private String fullName;
    private String phoneNumber;
    private String userId;
    private String imageUrl;
    private String location;
    private int age;
    private List<String> serviceProfileUid;

    // No-argument constructor required for Firestore
    public UserAccount() {
        this.serviceProfileUid = new ArrayList<>();
    }

    public UserAccount(String fullName, String phoneNumber, String userId, String imageUrl, String location, int age) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.location = location;
        this.age = age;
        this.serviceProfileUid = new ArrayList<>();
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getServiceProfileUid() {
        return serviceProfileUid;
    }

    public void setServiceProfileUid(List<String> serviceProfileUid) {
        this.serviceProfileUid = serviceProfileUid;
    }

    // Method to add a new item to the list
    public void addServiceProfileUid(String uid) {
        this.serviceProfileUid.add(uid);
    }

    // Method to remove an item from the list
    public void removeServiceProfileUid(String uid) {
        this.serviceProfileUid.remove(uid);
    }
}
