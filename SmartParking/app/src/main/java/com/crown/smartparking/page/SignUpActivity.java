package com.crown.smartparking.page;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.crown.smartparking.R;
import com.crown.smartparking.controller.AppController;
import com.crown.smartparking.utils.JSONParsing;
import com.crown.smartparking.utils.Network.HttpVolleyRequest;
import com.crown.smartparking.utils.Network.OnHttpResponse;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, OnHttpResponse {

    private static final String TAG = SignUpActivity.class.getName();
    private final int RC_SIGN_UP = 0;

    private EditText mUsernameET;
    private EditText mPasswordET;
    private EditText mConfirmPasswordET;
    private EditText mLicenseET;
    private Button mSignUpBtn;
    private ProgressBar mLoadingPbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setUpUi();

        mSignUpBtn.setOnClickListener(this);
    }

    private void setUpUi() {
        mUsernameET = findViewById(R.id.et_asu_username);
        mPasswordET = findViewById(R.id.et_asu_password);
        mConfirmPasswordET = findViewById(R.id.et_asu_confirm_password);
        mLicenseET = findViewById(R.id.et_asu_licence_no);
        mSignUpBtn = findViewById(R.id.btn_asu_sign_up);
        mLoadingPbar = findViewById(R.id.pbar_asu_loading);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_asu_sign_up) {
            onClickedSignUp();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void onClickedSignUp() {
        String username = mUsernameET.getText().toString().trim();
        String password = mPasswordET.getText().toString().trim();
        String confirmPassword = mConfirmPasswordET.getText().toString().trim();
        String license = mLicenseET.getText().toString().trim();

        if (username.isEmpty()) {
            mUsernameET.setError("Invalid field!!");
            return;
        }

        if (password.isEmpty()) {
            mPasswordET.setError("Invalid field!!");
            return;
        }

        if (confirmPassword.isEmpty()) {
            mConfirmPasswordET.setError("Invalid field!!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            mConfirmPasswordET.setError("Password doesn't match!!");
            return;
        }

        if (license.isEmpty()) {
            mLicenseET.setError("Invalid field!!");
            return;
        }

        signIn(username, password, license);
    }

    private void signIn(String username, String password, String license) {
        final String url = getString(R.string.domain) + "/createUser/";

        final Map<String, String> param = new HashMap<>();
        param.put(AppController.TAG_DISPLAY_NAME, username);
        param.put(AppController.TAG_PASSWORD, password);
        param.put(AppController.TAG_LICENSE_NO, license.toUpperCase());

        AppController.getInstance().saveCredentials(null, username, password, null);

        HttpVolleyRequest request = new HttpVolleyRequest(Request.Method.POST, url, RC_SIGN_UP, null, param, this);
        mLoadingPbar.setVisibility(View.VISIBLE);
        request.execute();
    }

    @Override
    public void onHttpResponse(String response, int request) {
        Log.v(TAG, response);
        mLoadingPbar.setVisibility(View.GONE);
        if (request == RC_SIGN_UP) {
            JSONObject object = JSONParsing.ObjectFromString(response);
            int status = Integer.parseInt(JSONParsing.StringFromObject(object, "status"));

            if (status == 200) {
                fetchResponse(JSONParsing.ObjectFromObject(object, "data"));
            } else if (status == 400) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            } else if (status == 409) {
                Toast.makeText(this, "License no. conflicted", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onHttpErrorResponse(VolleyError error, int request) {
        mLoadingPbar.setVisibility(View.GONE);
        Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show();
    }

    private void fetchResponse(JSONObject data) {
        String userID = JSONParsing.StringFromObject(data, AppController.TAG_USER_ID);
        String username = JSONParsing.StringFromObject(data, AppController.TAG_DISPLAY_NAME);
        String license = JSONParsing.StringFromObject(data, AppController.TAG_LICENSE_NO);

        AppController.getInstance().saveCredentials(userID, username, null, license);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
