package com.example.easyfix;

public class Order {
    private String orderFrom;
    private String orderTo;
    private Boolean confirmed=false;
    private boolean isReviewed=false;
    private String review;
    private double rating;
    private String serviceType;
    private String orderId;

    // Getter and Setter for orderFrom
    public String getOrderFrom() {
        return orderFrom;
    }

    public void setOrderFrom(String orderFrom) {
        this.orderFrom = orderFrom;
    }

    // Getter and Setter for orderTo
    public String getOrderTo() {
        return orderTo;
    }

    public void setOrderTo(String orderTo) {
        this.orderTo = orderTo;
    }

    // Getter and Setter for confirmed
    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    // Getter and Setter for review
    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    // Getter and Setter for rating
    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    // Getter and Setter for serviceType
    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public boolean getIsReviewed() {
        return isReviewed;
    }

    public void setIsReviewed(boolean isReviewed) {
        this.isReviewed = isReviewed;
    }
}
