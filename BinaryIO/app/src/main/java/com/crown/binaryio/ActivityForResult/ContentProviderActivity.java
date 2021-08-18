package com.crown.binaryio.ActivityForResult;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.crown.binaryio.R;

public class ContentProviderActivity extends AppCompatActivity {

    private EditText mEditText;
    private Button mDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.afr_activity_content_provider);

        mEditText = findViewById(R.id.et_aacp_edit_text);
        mDoneButton = findViewById(R.id.btn_aacp_done);

        mDoneButton.setOnClickListener(v -> {
            String text = mEditText.getText().toString();
            Intent intent = getIntent().putExtra("result", text);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}
