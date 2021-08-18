package com.crown.smartparking.utils.Network;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
import com.crown.smartparking.controller.AppController;

import java.util.Map;

public class HttpVolleyRequest {
    private int mMethod;
    private int mRequest;
    private String mUrl;
    private final String TAG = HttpVolleyRequest.class.getSimpleName();
    private OnHttpResponse mOnHttpResponse;
    private Map<String, String> mHttpHeader = null;
    private Map<String, String> mHttpParam = null;

    public HttpVolleyRequest(int method, String url, int request, OnHttpResponse onHttpResponse) {
        this.mMethod = method;
        this.mUrl = url;
        this.mOnHttpResponse = onHttpResponse;
        this.mRequest = request;
    }

    public HttpVolleyRequest(int method, String url, int request, Map<String, String> header, Map<String, String> param, OnHttpResponse onHttpResponse) {
        this(method, url, request, onHttpResponse);
        this.mHttpParam = param;
        this.mHttpHeader = header;
    }

    public void execute() {
        StringRequest stringRequest = new StringRequest(this.mMethod, this.mUrl,
                response -> mOnHttpResponse.onHttpResponse(response, mRequest),
                error -> mOnHttpResponse.onHttpErrorResponse(error, mRequest)) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.v(TAG, "Header: " + mHttpHeader);
                if (mHttpHeader != null && !mHttpHeader.isEmpty()) {
                    return mHttpHeader;
                }
                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Log.v(TAG, "Param: " + mHttpParam);
                if (mHttpParam != null && !mHttpParam.isEmpty()) {
                    return mHttpParam;
                }
                return super.getParams();
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}