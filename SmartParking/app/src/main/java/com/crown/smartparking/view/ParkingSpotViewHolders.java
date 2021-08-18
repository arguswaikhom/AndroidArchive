package com.crown.smartparking.view;

import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.crown.smartparking.R;

class ParkingSpotViewHolders {
    public static class SpotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView index;
        ParkingSpotAdapter.ParkingSpotClickListener listener;

        SpotViewHolder(View v, ParkingSpotAdapter.ParkingSpotClickListener listener) {
            super(v);
            this.listener = listener;
            cv = v.findViewById(R.id.cv_lips_view);
            index = v.findViewById(R.id.tv_lips_index);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onParkingSpotClick(v, this.getLayoutPosition());
        }
    }
}