package com.crown.smartparking.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crown.smartparking.R;
import com.crown.smartparking.model.ParkingSpot;

import java.util.List;

public class ParkingSpotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ParkingSpot> mDataset;
    private ParkingSpotClickListener listener;

    public interface ParkingSpotClickListener {
        void onParkingSpotClick(View view, int position);
    }

    public ParkingSpotAdapter(ParkingSpotClickListener listener, List<ParkingSpot> myDataset) {
        this.listener = listener;
        this.mDataset = myDataset;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_parking_spot, parent, false);
        return new ParkingSpotViewHolders.SpotViewHolder(rootView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ParkingSpot parkingSpot = mDataset.get(position);
        ParkingSpotViewHolders.SpotViewHolder vh = (ParkingSpotViewHolders.SpotViewHolder) holder;

        int color = getParkingStatusIndicator(vh.cv, parkingSpot.getStatus());
        vh.cv.setCardBackgroundColor(color);
        vh.index.setText(parkingSpot.getIndex() + "");
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private int getParkingStatusIndicator(View view, int status) {
        if (status == ParkingSpot.STATUS_RECOMMENDED) {
            return view.getContext().getResources().getColor(android.R.color.holo_blue_dark);
        } else if (status == ParkingSpot.STATUS_AVAILABLE) {
            return view.getContext().getResources().getColor(android.R.color.holo_blue_light);
        } else {
            return view.getContext().getResources().getColor(android.R.color.holo_red_dark);
        }
    }
}
