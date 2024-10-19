package com.example.mobdevemco;

public class CourtData {
    private String courtName;
    private String courtLocation;
    private Integer courtImage;


    public CourtData(String courtName, String courtLocation, Integer courtImage) {
        this.courtName = courtName;
        this.courtLocation = courtLocation;
        this.courtImage = courtImage;
    }

    public String getCourtName() {
        return courtName;
    }

    public void setCourtName(String courtName) {
        this.courtName = courtName;
    }

    public String getCourtLocation() {
        return courtLocation;
    }

    public void setCourtLocation(String courtLocation) {
        this.courtLocation = courtLocation;
    }

    public Integer getCourtImage() {
        return courtImage;
    }

    public void setCourtImage(Integer courtImage) {
        this.courtImage = courtImage;
    }
}
