package com.crown.binaryio.Requests;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.crown.binaryio.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class SampleRequest extends AppCompatActivity {

    private EditText mEditTextUrl;
    private Button mBtnSearch;
    private TextView mTextViewResponse;


    private final String DUMMY_URL = "http://10.24.56.120:8000/json/serialize/details/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_request);

        mEditTextUrl = findViewById(R.id.et_url);
        mBtnSearch = findViewById(R.id.btn_search);
        mTextViewResponse = findViewById(R.id.tv_response);

        mBtnSearch.setOnClickListener(v -> {
            String url = mEditTextUrl.getText().toString();
            getResponse(url);

            /*URL queryUrl = null;
            try {
                queryUrl = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            new QueryTask().execute(queryUrl);*/
        });

       // getResponse(DUMMY_URL);
    }

    public class QueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL url = urls[0];
            String result = null;
            try{
                result = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e){
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s != null && !s.equals("")){
                mTextViewResponse.setText("\n\nResponse is: \n\n" + s);
            }else {
                mTextViewResponse.setText("\n\nThat didn't work!");
            }
        }
    }


    private void getResponse(final String url) {


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Display the first 500 characters of the response string.
                    mTextViewResponse.setText("Url : " + url + "\n\nResponse is: \n\n" + response);
                }, error -> mTextViewResponse.setText("Url : " + url + "\n\nThat didn't work!"));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
