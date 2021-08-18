package com.crown.smartparking.utils;

public class GetDialogText {
    public static String getLoginDialogText(String id, String startTime) {
        return null;
    }

    public static String getLogoutDialogText(String id, String startTime, String endTime, String price) {
        id = "<h1>ID: " + id + "</h1>";
        String timeBetween = "<h4>" + TimeUtils.getTime(startTime) + " to " + TimeUtils.getTime(endTime) + "</h4>";
        String onDate = "<p>" + TimeUtils.getDay(startTime) + "</p>";
        String timeDuration = "<h6>Duration: " + TimeUtils.getDuration(startTime, endTime) + "</h6>";
        price = "<h6>Bill: " + price + "</h6>";

        return (id + timeBetween + onDate + timeDuration + price);
    }
}
