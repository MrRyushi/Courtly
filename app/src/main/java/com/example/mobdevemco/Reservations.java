package com.example.mobdevemco;

import java.util.List;

public class Reservations {
    private String courtName;
    private List<String> timeSlots; // Change this to a list
    private String date;
    private String userId;

    public Reservations(String courtName, List<String> timeSlots, String date, String userId) {
        this.courtName = courtName;
        this.timeSlots = timeSlots;
        this.date = date;
        this.userId = userId;
    }

    // No-argument constructor required for Firebase
    public Reservations() {
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public List<String> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<String> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

