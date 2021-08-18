package com.squadx.crown.pocketcomic;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class ContentListViewNavigationDrawer implements Parcelable {
    private int mImageId;
    private String mTitle;
    private String mLink;

    ContentListViewNavigationDrawer(String title, String link /*, int imageId*/) {
        mTitle = title;
        mLink = link;
        //mImageId = imageId;
    }

    public String getLink() { return mLink; }

    //public int getImageId() { return mImageId; }

    public String getTitle() {
        return mTitle;
    }

    protected ContentListViewNavigationDrawer(Parcel in) {
        mImageId = in.readInt();
        mTitle = in.readString();
        mLink = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mImageId);
        dest.writeString(mTitle);
        dest.writeString(mLink);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ContentListViewNavigationDrawer> CREATOR = new Creator<ContentListViewNavigationDrawer>() {
        @Override
        public ContentListViewNavigationDrawer createFromParcel(Parcel in) {
            return new ContentListViewNavigationDrawer(in);
        }

        @Override
        public ContentListViewNavigationDrawer[] newArray(int size) {
            return new ContentListViewNavigationDrawer[size];
        }
    };
}
