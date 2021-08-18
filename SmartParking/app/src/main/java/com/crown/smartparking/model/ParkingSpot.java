package com.crown.smartparking.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

public class ParkingSpot implements Parcelable {
    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_NOT_AVAILABLE = 1;
    public static final int STATUS_RECOMMENDED = 2;

    private int status;
    private int index;

    public ParkingSpot(int status) {
        this.status = status;
    }

    public ParkingSpot(int status, int index) {
        this.status = status;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.status);
        dest.writeInt(this.index);
    }

    protected ParkingSpot(Parcel in) {
        this.status = in.readInt();
        this.index = in.readInt();
    }

    public static final Parcelable.Creator<ParkingSpot> CREATOR = new Parcelable.Creator<ParkingSpot>() {
        @Override
        public ParkingSpot createFromParcel(Parcel source) {
            return new ParkingSpot(source);
        }

        @Override
        public ParkingSpot[] newArray(int size) {
            return new ParkingSpot[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}