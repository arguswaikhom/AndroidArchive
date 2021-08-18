package com.crown.smartparking.page;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.crown.smartparking.R;
import com.crown.smartparking.controller.AppController;
import com.crown.smartparking.model.History;
import com.crown.smartparking.model.HistoryAdapter;
import com.crown.smartparking.model.HistoryDetails;
import com.crown.smartparking.model.ListItem;
import com.crown.smartparking.model.ParkingSpot;
import com.crown.smartparking.utils.JSONParsing;
import com.crown.smartparking.utils.Network.HttpVolleyRequest;
import com.crown.smartparking.utils.Network.OnHttpResponse;
import com.crown.smartparking.utils.TimeUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class HistoryFragment extends Fragment implements EventListener<QuerySnapshot>, HistoryAdapter.HistoryItemClickListener {
    private final String TAG = HistoryFragment.class.getName();
    private HistoryAdapter mAdapter;
    private List<ListItem> mDataSet;
    private MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        fireStore.collection("history")
                .whereEqualTo("userId", AppController.getInstance().getUser().getUserId())
                .addSnapshotListener(this);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.history_fragment, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.rv_rvl_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        mDataSet = new ArrayList<>();
        mAdapter = new HistoryAdapter(mDataSet, this);
        recyclerView.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
            updateItemList(queryDocumentSnapshots.getDocuments());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            ParkingSpot spot = data.getParcelableExtra(ParkingSpotActivity.KEY_SELECTED_SLOT);
            String parkingId = data.getStringExtra(ParkingSpotActivity.KEY_PARKING_ID);
            acquireSpot(parkingId, spot);
        }
    }

    private void acquireSpot(String parkingId, ParkingSpot spot) {
        Map<String, Object> map = new HashMap<>();
        map.put("isAvailable", false);
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        fireStore.collection("parking").document(parkingId).collection("parkingSlot").document(String.valueOf(spot.getIndex())).set(map);
        fireStore.collection("user").document(AppController.getInstance().getUser().getUserId()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot user = task.getResult();
                updateParkingSpot(user.getString("currentParkingId"), spot);
            }
        });
    }

    private void updateParkingSpot(String currentParkingId, ParkingSpot spot) {
        Map<String, Object> map = new HashMap<>();
        map.put("parkingSlot", spot.getIndex());
        FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
        fireStore.collection("history").document(currentParkingId).update(map);
    }

    private void updateItemList(List<DocumentSnapshot> documents) {
        mDataSet.clear();
        for (DocumentSnapshot doc : documents) {
            if (doc.exists()) {
                History item = doc.toObject(History.class);
                item.setHistoryId(doc.getId());
                if (doc.getString("parking") != null) {
                    item.setParking(doc.getString("parking"));
                }
                mDataSet.add(item);

                if (item != null && item.getParkingSlot() == null && item.getEndTime() == null && item.getDuration() == null) {
                    chooseParkingSpot(item);
                }
            }
        }
        Collections.sort(mDataSet, (o1, o2) -> {
            long value = (((History) o2).getStartTime().getSeconds() - ((History) o1).getStartTime().getSeconds());
            return (int) value;
        });
        mAdapter.notifyDataSetChanged();
    }

    private void chooseParkingSpot(History item) {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), ParkingSpotActivity.class);
        intent.putExtra(ParkingSpotActivity.KEY_PARKING_ID, item.getParking());
        startActivityForResult(intent, 0);
    }

    @Override
    public void onHistoryItemClicked(View view, int position) {
        History history = (History) mDataSet.get(position);

        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_parking_history_details, null);
        AlertDialog dialog = new AlertDialog.Builder(activity, R.style.LoadingDialogTheme).setView(dialogView).create();

        dialogView.findViewById(R.id.pbar_dphd_loading).setVisibility(View.VISIBLE);
        dialogView.findViewById(R.id.cv_dphd_cardView).setVisibility(View.INVISIBLE);
        dialog.show();

        String url = getString(R.string.domain) + "/getParkingHistoryDetails/";
        Map<String, String> param = new HashMap<>();
        param.put("historyID", history.getHistoryId());
        new HttpVolleyRequest(Request.Method.POST, url, 0, null, param, new OnHttpResponse() {
            @Override
            public void onHttpResponse(String response, int request) {
                // Log.v(TAG, "Response: " + response);
                JSONObject object = JSONParsing.ObjectFromString(response);

                String status = JSONParsing.StringFromObject(object, "status");
                if (status.equals("200")) {
                    JSONObject data = JSONParsing.ObjectFromObject(object, "data");
                    showHistoryDialog(dialog, dialogView, data);
                }
            }

            @Override
            public void onHttpErrorResponse(VolleyError error, int request) {
                Toast.makeText(activity, "Can't get history data", Toast.LENGTH_SHORT).show();
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }).execute();
    }

    private void showHistoryDialog(AlertDialog dialog, View view, JSONObject data) {
        Log.v(TAG, "Data: " + data);
        if (dialog.isShowing()) {
            view.findViewById(R.id.pbar_dphd_loading).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.cv_dphd_cardView).setVisibility(View.VISIBLE);

            HistoryDetails.Parking parking = HistoryDetails.fromJson(data.toString()).getParking();
            if (parking != null) {
                if (parking.getImage() != null && !parking.getImage().equals(""))
                    Glide.with(activity).load(parking.getImage()).into((ImageView) view.findViewById(R.id.iv_dphd_image));
                if (parking.getTitle() != null && !parking.getTitle().equals(""))
                    ((TextView) view.findViewById(R.id.tv_dphd_parking_title)).setText(parking.getTitle());
                if (parking.getAddress() != null && !parking.getAddress().equals(""))
                    ((TextView) view.findViewById(R.id.tv_dphd_parking_address)).setText(parking.getAddress());
            }

            String parkingSpot = JSONParsing.StringFromObject(data, "parkingSlot").equals("") ? "..." : JSONParsing.StringFromObject(data, "parkingSlot");
            ((TextView) view.findViewById(R.id.tv_dphd_parking_slot)).setText(String.format("Parking slot: %s", parkingSpot));

            JSONObject st = JSONParsing.ObjectFromString(JSONParsing.StringFromObject(data, "startTime"));
            long startTime = Long.parseLong(JSONParsing.StringFromObject(st, "_seconds"));
            String stMessage = "...";
            if (startTime != 0L) {
                stMessage = TimeUtils.getTime(startTime) + ", " + TimeUtils.getDay(startTime);
            }

            JSONObject et = JSONParsing.ObjectFromString(JSONParsing.StringFromObject(data, "endTime"));
            String etMessage = "...";
            if (!JSONParsing.StringFromObject(et, "_seconds").equals("")) {
                long endTime = Long.parseLong(JSONParsing.StringFromObject(et, "_seconds"));
                if (endTime != 0L)
                    etMessage = TimeUtils.getTime(endTime) + ", " + TimeUtils.getDay(endTime);
            }

            ((TextView) view.findViewById(R.id.tv_dphd_start_time)).setText(stMessage);
            ((TextView) view.findViewById(R.id.tv_dphd_end_time)).setText(etMessage);

            String duration = !JSONParsing.StringFromObject(data, "duration").equals("null") ? String.format(Locale.ENGLISH, "%s", TimeUtils.getDuration(Long.parseLong(JSONParsing.StringFromObject(data, "duration")))) : "...";
            ((TextView) view.findViewById(R.id.tv_dphd_duration)).setText(duration);

            String price = !JSONParsing.StringFromObject(data, "price").equals("null") ? String.format(Locale.ENGLISH, "₹ %.2f", Double.parseDouble(JSONParsing.StringFromObject(data, "price"))) : "...";
            ((TextView) view.findViewById(R.id.tv_dphd_price)).setText(price);
        }
    }
}