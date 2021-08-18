package com.crown.binaryio.ActiveLinkTextView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import com.crown.binaryio.R;

public class ActiveLinkTextView extends AppCompatActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.altv_activity_main);

        mTextView = findViewById(R.id.tv_altv_text_view);

        mTextView.setText("Click my web site: www.stackoverflow.com and also check www.google.com okay");
        Linkify.addLinks(mTextView, Linkify.WEB_URLS);
    }
}
