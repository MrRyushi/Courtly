package com.example.mobdevemco;

import java.util.List;

public class Reservations {
    private String courtName;
    private List<String> timeSlots; // Change this to a list
    private String date;
    private String userId;
    private String reservationDateTime; // The exact date and time the reservation was made

    public Reservations(String courtName, List<String> timeSlots, String date, String userId, String reservationDateTime) {
        this.courtName = courtName;
        this.timeSlots = timeSlots;
        this.date = date;
        this.userId = userId;
        this.reservationDateTime = reservationDateTime;
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

    public String getReservationDateTime() {
        return reservationDateTime;
    }

    public void setReservationDateTime(String reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }
}

