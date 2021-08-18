package com.crown.smartparking.model;

import com.google.firebase.Timestamp;

public class History extends ListItem{
    public static final int TYPE = 2;

    private String userId;
    private String licenseNo;
    private String parking;
    private Timestamp startTime;
    private Timestamp endTime;
    private String status;
    private Long duration;
    private Double price;
    private Long parkingSlot;
    private String historyId;

    public String getUserId() {
        return userId;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public Long getDuration() {
        return duration;
    }

    public Double getPrice() {
        return price;
    }

    public Long getParkingSlot() {
        return parkingSlot;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    @Override
    public int getType() {
        return TYPE;
    }
}
