package com.crown.binaryio.DynamicListView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.crown.binaryio.R;

import java.util.ArrayList;
import java.util.List;

public class DynamicListViewMainActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

    private ListView mListView;
    private ArrayAdapter<Integer> mArrayAdapter;
    private List<Integer> mListDateL;
    private View mFooterView;
    private int mLastItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_list_view_main);

        mListView = findViewById(R.id.lv_adlvm_list_view);
        mListDateL = new ArrayList<>();
        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mListDateL);
        mFooterView = LayoutInflater.from(this).inflate(R.layout.loading_layout, mListView, false);
        mListView.addFooterView(mFooterView);
        mListView.setAdapter(mArrayAdapter);

        generateDummyDate();

        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
            mLastItem += 20;
            generateDummyDate();
            Toast.makeText(DynamicListViewMainActivity.this,
                    firstVisibleItem + " : " + visibleItemCount + " : " + totalItemCount, Toast.LENGTH_SHORT).show();
        }
    }

    private void generateDummyDate() {
        for (int i = mLastItem; i < mLastItem + 20; i++) {
            mListDateL.add(i);
        }
        mArrayAdapter.notifyDataSetChanged();
    }
}
