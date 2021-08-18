package com.crown.smartparking.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

public class User {
    private String userId;
    private String displayName;
    private String licenseNo;

    public User(String userId, String displayName, String licenseNo) {
        this.userId = userId;
        this.displayName = displayName;
        this.licenseNo = licenseNo;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
