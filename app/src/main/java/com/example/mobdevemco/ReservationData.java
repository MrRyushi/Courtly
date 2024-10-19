package com.example.mobdevemco;

public class ReservationData {
    private int id;
    private String courtName;
    private String reservationDate;
    private String reservationTime;

    public ReservationData(int id, String courtNname, String reservationDdate, String reservationTime) {
        this.id = id;
        this.courtName = courtNname;
        this.reservationDate = reservationDdate;
        this.reservationTime = reservationTime;
    }

    public String getCourtName() {
        return courtName;
    }

    public String getReservationDate() {
        return reservationDate;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public int getId() { return id; }

    public void setCourtName(String courtNname) {
        this.courtName = courtNname;
    }

    public void setReservationDate(String reservationDdate) {
        this.reservationDate = reservationDdate;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }

    public void setId(int id) {
        this.id = id;
    }
}
