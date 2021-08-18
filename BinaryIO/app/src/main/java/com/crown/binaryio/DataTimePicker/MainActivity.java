package com.crown.binaryio.DataTimePicker;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crown.binaryio.R;

public class MainActivity extends AppCompatActivity {

    TextView mShowTimeTextView;
    Button mSetTimeButton;
    Button mSetDateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dtp_activity_main);

        mShowTimeTextView = findViewById(R.id.tv_dam_show_time);
        mSetTimeButton = findViewById(R.id.btn_dam_set_time);
        mSetDateButton = findViewById(R.id.btn_dam_set_date);

        mSetTimeButton.setOnClickListener(this::showTimePickerDialog);
        mSetDateButton.setOnClickListener(this::showDatePickerDialog);
    }

    private void showDatePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
}
