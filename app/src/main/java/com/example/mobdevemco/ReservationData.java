package com.example.mobdevemco;

public class ReservationData {
    private String courtNname;
    private String reservationDdate;
    private String reservationTime;

    public ReservationData(String courtNname, String reservationDdate, String reservationTime) {
        this.courtNname = courtNname;
        this.reservationDdate = reservationDdate;
        this.reservationTime = reservationTime;
    }

    public String getCourtNname() {
        return courtNname;
    }

    public String getReservationDdate() {
        return reservationDdate;
    }

    public String getReservationTime() {
        return reservationTime;
    }

    public void setCourtNname(String courtNname) {
        this.courtNname = courtNname;
    }

    public void setReservationDdate(String reservationDdate) {
        this.reservationDdate = reservationDdate;
    }

    public void setReservationTime(String reservationTime) {
        this.reservationTime = reservationTime;
    }
}
