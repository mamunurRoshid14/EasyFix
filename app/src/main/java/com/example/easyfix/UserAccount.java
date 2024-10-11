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
    private double latitude;
    private double longitude;
    private String typeofService;
    private List<String> reviewIds;

    public UserAccount() {
        this.reviewIds = new ArrayList<>();
    }

    public UserAccount(String fullName, String phoneNumber, String userId, String imageUrl, String location, int age, double latitude, double longitude, String typeofService) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.location = location;
        this.age = age;
        this.latitude = latitude;
        this.longitude = longitude;
        this.typeofService = typeofService;
        this.reviewIds = new ArrayList<>();
    }

    public UserAccount(String fullName, String phoneNumber, String userId, String imageUrl, String location, int age) {
        this(fullName, phoneNumber, userId, imageUrl, location, age, 0.0, 0.0, "");
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTypeofService() {
        return typeofService;
    }

    public void setTypeofService(String typeofService) {
        this.typeofService = typeofService;
    }

    public List<String> getReviewIds() {
        return reviewIds;
    }

    public void setReviewIds(List<String> reviewIds) {
        this.reviewIds = reviewIds;
    }

    // Method to add a new review ID to the list
    public void addReviewId(String reviewId) {
        this.reviewIds.add(reviewId);
    }

    // Method to remove a review ID from the list
    public void removeReviewId(String reviewId) {
        this.reviewIds.remove(reviewId);
    }
}
