package com.crown.smartparking.controller;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crown.smartparking.model.User;
import com.crown.smartparking.page.LoginActivity;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    public static final String TAG_CREDENTIAL_SP = "credential";
    public static final String TAG_DISPLAY_NAME = "displayName";
    public static final String TAG_USER_ID = "userId";
    public static final String TAG_PASSWORD = "password";
    public static final String TAG_LICENSE_NO = "licenseNo";
    public static final String TAG_DEVICE_TOKEN = "deviceToken";

    private RequestQueue mRequestQueue;
    private static AppController mInstance;
    private SharedPreferences mSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mSharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void saveCredentials(String userID, String username, String password, String license) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        if (userID != null) editor.putString(TAG_USER_ID, userID);
        if (username != null) editor.putString(TAG_DISPLAY_NAME, username);
        if (password != null) editor.putString(TAG_PASSWORD, password);
        if (license != null) editor.putString(TAG_LICENSE_NO, license);
        editor.apply();
    }

    public Map<String, String> getCredential() {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
        Map<String, String> credential = new HashMap<>();
        credential.put(TAG_DISPLAY_NAME, sharedPreferences.getString(TAG_DISPLAY_NAME, null));
        credential.put(TAG_USER_ID, sharedPreferences.getString(TAG_USER_ID, null));
        credential.put(TAG_PASSWORD, sharedPreferences.getString(TAG_PASSWORD, null));
        credential.put(TAG_LICENSE_NO, sharedPreferences.getString(TAG_LICENSE_NO, null));
        return credential;
    }

    public void saveDeviceToken(String token) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(TAG_DEVICE_TOKEN, token);
        editor.apply();
    }

    public String getDeviceToken() {
        SharedPreferences sharedPreferences = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
        return sharedPreferences.getString(TAG_DEVICE_TOKEN, null);
    }

    public User getUser() {
        SharedPreferences sp = getSharedPreferences(TAG_CREDENTIAL_SP, MODE_PRIVATE);
        return new User(sp.getString(TAG_USER_ID, null), sp.getString(TAG_DISPLAY_NAME, null), sp.getString(TAG_LICENSE_NO, null));
    }

    public boolean isAuthenticated() {
        Map<String, String> credential = getCredential();
        return credential.get(TAG_DISPLAY_NAME) != null && credential.get(TAG_PASSWORD) != null && credential.get(TAG_USER_ID) != null && credential.get(TAG_LICENSE_NO) != null;
    }

    public void signOut(Activity activity) {
        FirebaseFirestore.getInstance().collection("user").document(getUser().getUserId()).update("deviceToken", FieldValue.arrayRemove(getDeviceToken()));
        mSharedPreferences.edit().clear().apply();

        try {
            FirebaseInstanceId.getInstance().deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);

        activity.finish();
    }
}
