package com.projectreachout.DummyUpload.User;

import com.google.gson.Gson;

public class DummyUser {
    private String id;
    private String username;
    private String image;
    private String email;
    private String account_type;

    public static DummyUser fromJson(String jsonString) {
        return new Gson().fromJson(jsonString, DummyUser.class);
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

    public String getEmail() {
        return email;
    }

    public String getAccount_type() {
        return account_type;
    }
}
