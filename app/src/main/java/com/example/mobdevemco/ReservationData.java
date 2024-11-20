package com.example.mobdevemco;

import java.util.Collections;
import java.util.List;

public class ReservationData {
    private String id; // Firebase unique key
    private String courtName;
    private String reservationDate;
    private String reservationDateTime;
    private List<String> reservationTimeSlot; // For the time slot (e.g., "7:00 PM - 8:00 PM")
    private String userId;

    // Empty constructor required for Firebase deserialization
    public ReservationData() {
    }

    public ReservationData(String id, String courtName, String reservationDate, String reservationDateTime, List<String> reservationTimeSlot, String userId) {
        this.id = id;
        this.courtName = courtName;
        this.reservationDate = reservationDate;
        this.reservationDateTime = reservationDateTime;
        this.reservationTimeSlot = reservationTimeSlot;
        this.userId = userId;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(String reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getReservationTimeSlot() {
        return reservationTimeSlot.toString().replace("[", "").replace("]", "");
    }

    public void setReservationTimeSlot(String reservationTimeSlot) {
        this.reservationTimeSlot = Collections.singletonList(reservationTimeSlot);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
