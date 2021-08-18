package com.crown.smartparking.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.crown.smartparking.R;
import com.crown.smartparking.utils.GetDialogText;

public class PopUpDialogFragment extends DialogFragment {
    public static final String TAG_REQUEST = "request";
    public static final String TAG_ID = "id";
    public static final String TAG_START_TIME = "start_time";
    public static final String TAG_END_TIME = "end_time";
    public static final String TAG_PRICE = "price";

    public static final int RC_VEHICLE_LOGOUT = 0;
    public static final int RC_VEHICLE_LOGIN = 1;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.pop_up_layout, null);

        TextView infoTV = root.findViewById(R.id.tv_pul_info);
        Bundle bundle = getArguments();

        String dialogText = "";
        if (bundle != null) {
             int request = bundle.getInt(TAG_REQUEST);
             if (request == RC_VEHICLE_LOGIN) {
                 String id = bundle.getString(TAG_ID, "");
                 String startTime = bundle.getString(TAG_START_TIME);
                 dialogText = GetDialogText.getLoginDialogText(id, startTime);
             } else if (request == RC_VEHICLE_LOGOUT) {
                 String id = bundle.getString(TAG_ID, "");
                 String startTime = bundle.getString(TAG_START_TIME);
                 String endTime = bundle.getString(TAG_END_TIME);
                 String price = bundle.getString(TAG_PRICE);
                 dialogText = GetDialogText.getLogoutDialogText(id, startTime, endTime, price);
             }
        }
        infoTV.setText(Html.fromHtml(dialogText));

        builder.setView(root)
                .setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PopUpDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
