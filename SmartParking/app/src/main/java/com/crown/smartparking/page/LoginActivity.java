package com.crown.smartparking.page;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, OnHttpResponse {
    private final String TAG = LoginActivity.class.getName();
    private final int RC_LOGIN = 0;

    private EditText mUsernameET;
    private EditText mPasswordET;
    private TextView mSignUpTV;
    private Button mLoginBtn;
    private ProgressBar mLoadingPbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        if (AppController.getInstance().isAuthenticated()) {
            navigateToMainActivity();
        }

        setUpUi();

        mSignUpTV.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);
    }

    private void setUpUi() {
        mUsernameET = findViewById(R.id.et_al_username);
        mPasswordET = findViewById(R.id.et_al_password);
        mSignUpTV = findViewById(R.id.tv_al_sign_up);
        mLoginBtn = findViewById(R.id.btn_al_login);
        mLoadingPbar = findViewById(R.id.pbar_al_loading);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_al_login) {
            onClickedLogin();
        } else if (v.getId() == R.id.tv_al_sign_up) {
            startActivity(new Intent(this, SignUpActivity.class));
        }
    }

    private void onClickedLogin() {
        String username = mUsernameET.getText().toString().trim();
        String password = mPasswordET.getText().toString().trim();

        if (username.isEmpty()) {
            mUsernameET.setError("Invalid input!!");
            return;
        }

        if (password.isEmpty()) {
            mPasswordET.setError("Invalid input!!");
            return;
        }

        getUserData(username, password);
    }

    private void getUserData(String username, String password) {
        final String url = getString(R.string.domain) + "/signIn/";

        Map<String, String> param = new HashMap<>();
        param.put(AppController.TAG_DISPLAY_NAME, username);
        param.put(AppController.TAG_PASSWORD, password);

        AppController.getInstance().saveCredentials(null, username, password, null);

        HttpVolleyRequest request = new HttpVolleyRequest(Request.Method.POST, url, RC_LOGIN, null, param, this);
        mLoadingPbar.setVisibility(View.VISIBLE);
        request.execute();
    }

    @Override
    public void onHttpResponse(String response, int request) {
        mLoadingPbar.setVisibility(View.GONE);
        Log.v(TAG, response);
        if (request == RC_LOGIN) {
            JSONObject object = JSONParsing.ObjectFromString(response);
            int status = Integer.parseInt(JSONParsing.StringFromObject(object, "status"));
            if (status == 200) {
                fetchResponse(JSONParsing.ObjectFromObject(object, "data"));
            }else if (status == 400) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            } else if (status == 404){
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onHttpErrorResponse(VolleyError error, int request) {
        mLoadingPbar.setVisibility(View.GONE);
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    }

    private void fetchResponse(JSONObject data) {
        String userID = JSONParsing.StringFromObject(data, AppController.TAG_USER_ID);
        String username = JSONParsing.StringFromObject(data, AppController.TAG_DISPLAY_NAME);
        String license = JSONParsing.StringFromObject(data, AppController.TAG_LICENSE_NO);

        AppController.getInstance().saveCredentials(userID, username, null, license);
        navigateToMainActivity();
    }

    private void navigateToMainActivity () {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
