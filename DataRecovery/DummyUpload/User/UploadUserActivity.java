package com.projectreachout.DummyUpload.User;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectreachout.R;
import com.projectreachout.Utilities.NetworkUtils.HttpVolleyRequest;
import com.projectreachout.Utilities.NetworkUtils.OnHttpResponse;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.projectreachout.GeneralStatic.JSONParsingArrayFromString;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.getDomainUrl;

public class UploadUserActivity extends AppCompatActivity implements View.OnClickListener, OnHttpResponse {
    private static final String TAG = UploadUserActivity.class.getName();

    private TextView mResponseTv;
    private List<DummyUser> mDummyUser = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.du_activity_dummy_upload);

        Button mGetBtn = findViewById(R.id.btn_dadu_get);
        Button mUploadBtn = findViewById(R.id.btn_dadu_upload);
        mResponseTv = findViewById(R.id.tv_dadu_response);

        mGetBtn.setOnClickListener(this);
        mUploadBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_dadu_get: {
                onClickedGet();
                break;
            }
            case R.id.btn_dadu_upload: {
                onClickedUpload();
                break;
            }
        }
    }

    private void onClickedUpload() {
        Log.v("abc", "User size: " + mDummyUser.size());
        Toast.makeText(this, "User size: " + mDummyUser.size(), Toast.LENGTH_LONG).show();
        for (DummyUser user: mDummyUser) {
            Map<String, Object> article = new HashMap<>();
            article.put("account_type", user.getAccount_type());

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("BufferUser")
                    .document(user.getEmail())
                    .set(article)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Upload Completed", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Log.w("abc", "Error adding document: " + user.getEmail()));


        }
    }

    private void onClickedGet() {
        String url = getDomainUrl() + "/all_users/";

        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, 0, this);
        httpVolleyRequest.execute();
    }

    @Override
    public void onHttpResponse(String response, int request) {
        Log.v(TAG, response);
        if (request == 0) {
            mResponseTv.setText(response);
            handleResponse(response);
        }
    }

    private void handleResponse(String response) {
        JSONArray responseList = JSONParsingArrayFromString(response);
        for (int i=0; i<responseList.length(); i++) {
            DummyUser user = DummyUser.fromJson(JSONParsingObjectFromArray(responseList, i).toString());
            mDummyUser.add(user);
        }
    }

    @Override
    public void onHttpErrorResponse(VolleyError error, int request) {

    }
}
