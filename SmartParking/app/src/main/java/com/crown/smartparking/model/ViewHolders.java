package com.crown.smartparking.model;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.crown.smartparking.R;

class ViewHolders {
    static class ParkingHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView dateTV;
        TextView startTimeTV;
        TextView durationTV;
        TextView priceTV;
        TextView billStatusTV;
        HistoryAdapter.HistoryItemClickListener listener;

        ParkingHistoryViewHolder(View v, HistoryAdapter.HistoryItemClickListener listener) {
            super(v);
            this.listener = listener;
            dateTV = v.findViewById(R.id.tv_phi_date);
            startTimeTV = v.findViewById(R.id.tv_phi_start_and_end_time);
            durationTV = v.findViewById(R.id.tv_phi_duration);
            priceTV = v.findViewById(R.id.tv_phi_price);
            billStatusTV = v.findViewById(R.id.tv_phi_bill_status);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onHistoryItemClicked(v, this.getLayoutPosition());
        }
    }
}