package com.projectreachout.DummyUpload.DownloadImage;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

public class DummyArticle implements Parcelable, Cloneable {
    private String article_id;
    private String username;
    private String profile_picture_url;
    private String desc;
    private String image;
    private String email;
    private String time_stamp;

    public static DummyArticle fromJson(String jsonString) {
        return new Gson().fromJson(jsonString, DummyArticle.class);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getArticle_id() {
        return article_id;
    }

    public String getUsername() {
        return username;
    }

    public String getProfile_picture_url() {
        return profile_picture_url;
    }

    public String getDesc() {
        return desc;
    }

    public String getImage() {
        return image;
    }

    public String getEmail() {
        return email;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    protected DummyArticle(Parcel in) {
        article_id = in.readString();
        username = in.readString();
        profile_picture_url = in.readString();
        desc = in.readString();
        image = in.readString();
        email = in.readString();
        time_stamp = in.readString();
    }

    public static final Creator<DummyArticle> CREATOR = new Creator<DummyArticle>() {
        @Override
        public DummyArticle createFromParcel(Parcel in) {
            return new DummyArticle(in);
        }

        @Override
        public DummyArticle[] newArray(int size) {
            return new DummyArticle[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(article_id);
        parcel.writeString(username);
        parcel.writeString(profile_picture_url);
        parcel.writeString(desc);
        parcel.writeString(image);
        parcel.writeString(email);
        parcel.writeString(time_stamp);
    }

    @NonNull
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
