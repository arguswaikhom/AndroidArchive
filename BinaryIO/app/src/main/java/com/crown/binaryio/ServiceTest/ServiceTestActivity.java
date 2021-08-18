package com.crown.binaryio.ServiceTest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.crown.binaryio.R;

public class ServiceTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.st_activity_service_test);

        Button startServiceBtn = findViewById(R.id.btn_sast_start_service);
        Button stopServiceBtn = findViewById(R.id.btn_sast_stop_service);

        startServiceBtn.setOnClickListener(this::startService);
        stopServiceBtn.setOnClickListener(this::stopService);
    }

    private void stopService(View view) {
        stopService(new Intent(getBaseContext(), ServiceTestService.class));
    }

    private void startService(View view) {
        startService(new Intent(getBaseContext(), ServiceTestService.class));
    }
}
