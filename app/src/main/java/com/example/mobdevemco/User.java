package com.example.mobdevemco;

public class User {
    public String fullName;
    public String email;
    public boolean member;
    public String membershipStatus;
    public int totalReservations;
    public String recentReservation;


    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, boolean member, String membershipStatus, int totalReservations, String recentReservation) {
        this.fullName = username;
        this.email = email;
        this.member = member;
        this.membershipStatus = membershipStatus;
        this.totalReservations = totalReservations;
        this.recentReservation = recentReservation;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean member() {
        return member;
    }

    public void setMember(boolean member) {
        this.member = member;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public int getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(int totalReservations) {
        this.totalReservations = totalReservations;
    }

}
