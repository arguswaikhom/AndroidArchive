package com.squadx.crown.pocketcomic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListViewNavigationDrawerAdapter extends ArrayAdapter<ContentListViewNavigationDrawer> {

    public ListViewNavigationDrawerAdapter(@NonNull Context context, @NonNull ArrayList<ContentListViewNavigationDrawer> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View currentView = convertView;
        if (currentView == null) {
            currentView = LayoutInflater.from(getContext()).inflate(R.layout.contain_comic_navigation_drawer, parent, false);
        }

        ContentListViewNavigationDrawer currentContent = getItem(position);

        TextView title = currentView.findViewById(R.id.textView_title);
        title.setText(currentContent.getTitle());

        return currentView;
    }
}
