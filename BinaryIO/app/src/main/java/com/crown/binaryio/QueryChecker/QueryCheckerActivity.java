package com.crown.binaryio.QueryChecker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.crown.binaryio.FacebookLikeFeed.app.AppController;
import com.crown.binaryio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

public class QueryCheckerActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = QueryCheckerActivity.class.getSimpleName();

    private TextView mHeaderTV;
    private TextView mParamTV;
    private TextView mResultTV;

    private EditText mUrlET;
    private EditText mKeyET;
    private EditText mValueET;

    private Button mAddheaderBtn;
    private Button mAddParamBtn;

    private FloatingActionButton mSubmitFAB;

    private final HashMap<String, String> mHeaderHM = new HashMap<>();
    private final HashMap<String, String> mParamHM = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qc_activity_query_checker);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHeaderTV = findViewById(R.id.tv_qcqc_header);
        mParamTV = findViewById(R.id.tv_qcqc_param);
        mResultTV = findViewById(R.id.tv_qcqc_result);

        mUrlET = findViewById(R.id.et_qaqc_url);
        mKeyET = findViewById(R.id.et_qcqc_key);
        mValueET = findViewById(R.id.et_qcqc_value);

        mAddheaderBtn = findViewById(R.id.btn_qcqc_add_header);
        mAddheaderBtn.setOnClickListener(this);

        mAddParamBtn = findViewById(R.id.btn_qcqc_add_param);
        mAddParamBtn.setOnClickListener(this);

        mSubmitFAB = findViewById(R.id.fab_qaqc_submit);
        mSubmitFAB.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_qcqc_add_header: {
                OnClickedAddHeader();
                break;
            }
            case R.id.btn_qcqc_add_param: {
                OnClickedAddParam();
                break;
            }
            case R.id.fab_qaqc_submit: {
                OnClickedSubmit();
                break;
            }
        }
    }

    private void OnClickedAddHeader() {
        String key = mKeyET.getText().toString().trim();
        String value = mValueET.getText().toString().trim();
        if (key.isEmpty() || value.isEmpty())
            return;
        mHeaderHM.put(key, value);
        mHeaderTV.setText("Header:\n" + mHeaderHM.toString());
        clearKeyValueEditText();
    }

    private void OnClickedAddParam() {
        String key = mKeyET.getText().toString().trim();
        String value = mValueET.getText().toString().trim();
        if (key.isEmpty() || value.isEmpty())
            return;
        mParamHM.put(key, value);
        mParamTV.setText("Param:\n" + mParamHM.toString());
        clearKeyValueEditText();
    }

    private void OnClickedSubmit() {
        String url = mUrlET.getText().toString().trim();
        if (url.isEmpty())
            return;

        int method = (mHeaderHM.isEmpty() && mParamHM.isEmpty()) ? Request.Method.GET : Request.Method.POST;

        StringRequest stringRequest = new StringRequest(method, url, response -> mResultTV.setText(getProperJSONString(response)), error -> Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show()) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (!mHeaderHM.isEmpty())
                    return mHeaderHM;
                return super.getHeaders();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if (!mParamHM.isEmpty())
                    return mParamHM;
                return super.getParams();
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private String getProperJSONString(String jsonString){
        try {
            Object jsonObj = new JSONTokener(jsonString).nextValue();
            if (jsonObj instanceof JSONArray) {
                JSONArray jsonArray = new JSONArray(jsonString);
                jsonString = jsonArray.toString(4);
            } else if (jsonObj instanceof JSONObject) {
                JSONObject jsonObject = new JSONObject(jsonString);
                jsonString = jsonObject.toString(4);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    private void clearKeyValueEditText() {
        mKeyET.getText().clear();
        mValueET.getText().clear();
    }
}
