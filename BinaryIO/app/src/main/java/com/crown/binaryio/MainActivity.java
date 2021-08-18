package com.crown.binaryio;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.crown.binaryio.ActiveLinkTextView.ActiveLinkTextView;
import com.crown.binaryio.Calculator.CalculatorMainActivity;
import com.crown.binaryio.CompoundView.CompoundViewActivity;
import com.crown.binaryio.DynamicListView.DynamicListViewMainActivity;
import com.crown.binaryio.EllipsizeTextView.EllipsizeTextViewActivity;
import com.crown.binaryio.ImageCompression.ImageCompressionActivity;
import com.crown.binaryio.QueryChecker.QueryCheckerActivity;
import com.crown.binaryio.Requests.SampleRequest;
import com.crown.binaryio.WorkManager.WorkManagerMainActivity;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.lv_aslv_list_view);
        final ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("ActiveLinkTextView");
        arrayList.add("ChooseImageAndCrop");
        arrayList.add("EllipsizeTextView");
        arrayList.add("FacebookLikeFeed");
        arrayList.add("SimpleNetworkRequest");
        arrayList.add("DateTimePicker");
        arrayList.add("ActivityForResult");
        arrayList.add("ServiceTest");
        arrayList.add("ImageCompression");
        arrayList.add("QueryChecker");
        arrayList.add("WorkManager");
        arrayList.add("DynamicListView");
        arrayList.add("Calculator");
        arrayList.add("CompoundView");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: {
                startActivity(getIntent(view.getContext(), ActiveLinkTextView.class));
                break;
            }
            case 1: {
                startActivity(getIntent(view.getContext(), com.crown.binaryio.ChooseImageAndCrop.MainActivity.class));
                break;
            }
            case 2: {
                startActivity(getIntent(view.getContext(), EllipsizeTextViewActivity.class));
                break;
            }
            case 3: {
                startActivity(getIntent(view.getContext(), com.crown.binaryio.FacebookLikeFeed.MainActivity.class));
                break;
            }
            case 4: {
                startActivity(getIntent(view.getContext(), SampleRequest.class));
                break;
            }
            case 5: {
                startActivity(getIntent(view.getContext(), com.crown.binaryio.DataTimePicker.MainActivity.class));
                break;
            }
            case 6: {
                startActivity(getIntent(view.getContext(), com.crown.binaryio.ActivityForResult.MainActivity.class));
                break;
            }
            case 7: {
                startActivity(getIntent(view.getContext(), com.crown.binaryio.ServiceTest.ServiceTestActivity.class));
                break;
            }
            case 8: {
                startActivity(getIntent(view.getContext(), ImageCompressionActivity.class));
                break;
            }
            case 9: {
                startActivity(getIntent(view.getContext(), QueryCheckerActivity.class));
                break;
            }
            case 10: {
                startActivity(getIntent(view.getContext(), WorkManagerMainActivity.class));
                break;
            }
            case 11: {
                startActivity(getIntent(view.getContext(), DynamicListViewMainActivity.class));
                break;
            }
            case 12: {
                startActivity(getIntent(view.getContext(), CalculatorMainActivity.class));
                break;
            }
            case 13: {
                startActivity(getIntent(view.getContext(), CompoundViewActivity.class));
                break;
            }
        }
    }

    private Intent getIntent(Context context, Class<?> cls) {
        return new Intent(context, cls);
    }
}
