package com.crown.smartparking.utils.Network;

import com.android.volley.VolleyError;

public interface OnHttpResponse {
    void onHttpResponse(String response, int request);
    void onHttpErrorResponse(VolleyError error, int request);
}