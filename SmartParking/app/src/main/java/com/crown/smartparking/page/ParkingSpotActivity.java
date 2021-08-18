package com.crown.smartparking.page;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crown.smartparking.R;
import com.crown.smartparking.model.ParkingSpot;
import com.crown.smartparking.view.ParkingSpotAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParkingSpotActivity extends AppCompatActivity implements ParkingSpotAdapter.ParkingSpotClickListener, EventListener<QuerySnapshot> {
    private final String TAG = ParkingSpotActivity.class.getName();
    public static final String KEY_PARKING_ID = "PARKING_ID";
    public static final String KEY_SELECTED_SLOT = "SELECTED_SLOT";

    private ParkingSpotAdapter mAdapter;
    private List<ParkingSpot> mDataSet;
    private String parkingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_spot);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        RecyclerView mRecyclerView = findViewById(R.id.rv_aps_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mDataSet = new ArrayList<>();
        mAdapter = new ParkingSpotAdapter(this, mDataSet);

        mRecyclerView.setAdapter(mAdapter);

        parkingId = getIntent().getStringExtra(KEY_PARKING_ID);
        if (parkingId != null) {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("parking").document(parkingId).collection("parkingSlot").addSnapshotListener(this);
        }
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
            updateItemList(queryDocumentSnapshots.getDocuments());
        }
    }

    private void updateItemList(List<DocumentSnapshot> documents) {
        mDataSet.clear();
        for (DocumentSnapshot doc : documents) {
            if (doc.exists()) {
                boolean status = doc.getBoolean("isAvailable");
                ParkingSpot parkingSpot = new ParkingSpot(status? ParkingSpot.STATUS_AVAILABLE: ParkingSpot.STATUS_NOT_AVAILABLE, Integer.parseInt(doc.getId()));
                parkingSpot.setIndex(Integer.parseInt(doc.getId()));
                mDataSet.add(parkingSpot);
            }
        }

        Collections.sort(mDataSet, (o1, o2) -> o1.getIndex() - o2.getIndex());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onParkingSpotClick(View view, int position) {
        ParkingSpot spot = mDataSet.get(position);
        if (spot.getStatus() == ParkingSpot.STATUS_NOT_AVAILABLE) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(KEY_SELECTED_SLOT, mDataSet.get(position));
        if (parkingId != null) {
            intent.putExtra(KEY_PARKING_ID, parkingId);
        }
        setResult(RESULT_OK, intent);
        finish();
    }
}
