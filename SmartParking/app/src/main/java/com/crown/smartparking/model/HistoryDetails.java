package com.crown.smartparking.model;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.Gson;

public class HistoryDetails {
    Double duration;
    Timestamp startTime;
    Timestamp endTime;
    String licenseNo;
    Long parkingSlot;
    Double price;
    String status;
    Parking parking;

    public HistoryDetails() {
    }

    public static HistoryDetails fromJson(String json) {
        return new Gson().fromJson(json, HistoryDetails.class);
    }

    public static class Parking {
        String address;
        String image;
        GeoPoint location;
        String title;

        public Parking() {
        }

        public String getAddress() {
            return address;
        }

        public String getImage() {
            return image;
        }

        public GeoPoint getLocation() {
            return location;
        }

        public String getTitle() {
            return title;
        }
    }

    public Double getDuration() {
        return duration;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public Long getParkingSlot() {
        return parkingSlot;
    }

    public Double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public Parking getParking() {
        return parking;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
