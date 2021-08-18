package com.crown.smartparking.model;

import com.google.firebase.firestore.GeoPoint;

public class Parking {
    private String title;
    private String address;
    private GeoPoint location;
    private String image;
    private String price;
    private String parkingId;

    public Parking() {
    }

    public String getAddress() {
        return address;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }
}
