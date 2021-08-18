package com.crown.binaryio.ActivityForResult;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.crown.binaryio.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    TextView mResultTextView;
    Button mGetTextButton;
    private final int GET_TEXT_DATA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.afr_activity_main);

        mResultTextView = findViewById(R.id.tv_aam_text_result);
        mGetTextButton = findViewById(R.id.btn_aam_get_text);

        mGetTextButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ContentProviderActivity.class);
            startActivityForResult(intent, GET_TEXT_DATA);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == GET_TEXT_DATA) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                mResultTextView.setText(data.getStringExtra("result"));
            }
        }
        Log.d(LOG_TAG, "requestCode : " + String.valueOf(requestCode));
        Log.d(LOG_TAG, "resultCode : " + String.valueOf(resultCode));
    }
}
