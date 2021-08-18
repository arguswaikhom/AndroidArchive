package com.projectreachout.DummyUpload;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.projectreachout.Article.GetArticle.Article;
import com.projectreachout.DummyUpload.DownloadImage.DownloadTask;
import com.projectreachout.DummyUpload.DownloadImage.DummyArticle;
import com.projectreachout.R;
import com.projectreachout.Utilities.NetworkUtils.HttpVolleyRequest;
import com.projectreachout.Utilities.NetworkUtils.OnHttpResponse;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.projectreachout.GeneralStatic.JSONParsingArrayFromString;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.getDomainUrl;

public class DummyUploadActivity extends AppCompatActivity implements View.OnClickListener, OnHttpResponse {
    private static final String TAG = DummyUploadActivity.class.getName();

    private TextView mResponseTv;
    private List<DummyArticle> mArticles = new ArrayList<>();

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

    private void onClickedGet() {
        String url = getDomainUrl() + "/get_articles/";

        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, 0, this);
        httpVolleyRequest.execute();
    }

    private void onClickedUpload() {
        Log.v("abc", "Article size: " + mArticles.size());
        Toast.makeText(this, "Article size: " + mArticles.size(), Toast.LENGTH_LONG).show();
        for (DummyArticle dummyArticle: mArticles) {
            DownloadTask downloadTask = new DownloadTask(this, dummyArticle);
            //Log.v("abc", mArticles.get(0).toString());
            downloadTask.execute();
        }
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
            DummyArticle article = DummyArticle.fromJson(JSONParsingObjectFromArray(responseList, i).toString());
            mArticles.add(article);
        }
    }

    @Override
    public void onHttpErrorResponse(VolleyError error, int request) {
        Log.e(TAG, error.getMessage());
    }
}
