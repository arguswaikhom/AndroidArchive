package com.crown.smartparking.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crown.smartparking.R;
import com.crown.smartparking.utils.TimeUtils;
import com.crown.smartparking.view.ParkingSpotAdapter;

import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = ParkingSpotAdapter.class.getName();
    private List<ListItem> mDataSet;
    private HistoryItemClickListener mListener;

    public interface HistoryItemClickListener {
        void onHistoryItemClicked(View view, int position);
    }

    public HistoryAdapter(List<ListItem> myDataSet, HistoryItemClickListener listener) {
        mDataSet = myDataSet;
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView;
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_parking_history, parent, false);
        return new ViewHolders.ParkingHistoryViewHolder(rootView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == History.TYPE) {
            History history = (History) mDataSet.get(position);
            ViewHolders.ParkingHistoryViewHolder vh = (ViewHolders.ParkingHistoryViewHolder) holder;

            String startAt = TimeUtils.getTime(history.getStartTime().getSeconds());
            String endAt = history.getEndTime() != null ? TimeUtils.getTime(history.getEndTime().getSeconds()) : "...";
            String price = history.getPrice() != null ? String.format(Locale.ENGLISH, "₹ %.2f", history.getPrice()) : "₹ ...";

            /*CountDownTimer durationCountDown = new CountDownTimer(Long.MAX_VALUE, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    vh.durationTV.setText(TimeUtils.getOnDuration(history.getStartTime().getSeconds()));
                }

                @Override
                public void onFinish() {

                }
            };*/

            if (history.getDuration() == null && history.getEndTime() == null && history.getStatus().equals("parking")) {
                vh.durationTV.setText("...");
                // durationCountDown.start();
            } else {
                vh.durationTV.setText(TimeUtils.getDuration(history.getDuration()));
                // durationCountDown.cancel();
            }

            vh.dateTV.setText(TimeUtils.getDay(history.getStartTime().getSeconds()));
            vh.startTimeTV.setText(String.format("%s to %s", startAt, endAt));
            vh.priceTV.setText(price);
            vh.billStatusTV.setText(history.getStatus().toUpperCase());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSet.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
