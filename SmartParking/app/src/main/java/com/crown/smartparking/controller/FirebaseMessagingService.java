package com.crown.smartparking.controller;

import android.util.Log;

import com.crown.smartparking.model.User;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = FirebaseMessagingService.class.getName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendDeviceToken(token);
    }

    public static void sendDeviceToken(String token) {
        User user = AppController.getInstance().getUser();

        if (user == null) return;
        FirebaseFirestore.getInstance().collection("user")
                .document(user.getUserId())
                .update("deviceToken", FieldValue.arrayUnion(token))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AppController.getInstance().saveDeviceToken(token);
                    }
                });
    }
}