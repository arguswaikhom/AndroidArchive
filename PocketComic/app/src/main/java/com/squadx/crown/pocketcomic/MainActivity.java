package com.squadx.crown.pocketcomic;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String CLASS_NAME_MAIN_ACTIVITY = MainActivity.class.getSimpleName();
    public static boolean canGoBack = true;
    private View mNavigationHeaderView;
    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Importing the webView Contains
        getSupportFragmentManager().beginTransaction().replace(R.id.webViewLayout, new ContentMainFragment()).commit();

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mNavigationHeaderView = navigationView.getHeaderView(0);
        implementNavigationDrawerListView();
    }

    private List<ContentListViewNavigationDrawer> getContentListViewNavigationDrawers() {
        List<ContentListViewNavigationDrawer> list = new ArrayList<>();
        List<String> listTitle = Arrays.asList(getResources().getStringArray(R.array.title_comics));
        List<String> listLink = Arrays.asList(getResources().getStringArray(R.array.link_comics));

        for(int i=0; i<listTitle.size() && i<listLink.size(); i++) {
            list.add(new ContentListViewNavigationDrawer(listTitle.get(i), listLink.get(i)));
//            Log.v(CLASS_NAME_MAIN_ACTIVITY, "Title: " + listTitle.get(i));
//            Log.v(CLASS_NAME_MAIN_ACTIVITY, "Link: " + listLink.get(i));
        }
        return list;
    }

    private void implementNavigationDrawerListView() {

        List<ContentListViewNavigationDrawer> list = getContentListViewNavigationDrawers();

        LayoutInflater factory = getLayoutInflater();
        View rootView = factory.inflate(R.layout.contain_comic_navigation_drawer, null);

        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeAllViews();
        }

        ListView listView = mNavigationHeaderView.findViewById(R.id.listView);
        final ListViewNavigationDrawerAdapter adapter = new ListViewNavigationDrawerAdapter(this,
                (ArrayList<ContentListViewNavigationDrawer>) list);

        listView.setAdapter(adapter);

        int listViewTotalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            listViewTotalHeight += listItem.getMeasuredHeight();
        }

        Log.v("ListViewHeight" , "" + listViewTotalHeight);
        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, listViewTotalHeight));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mDrawer.closeDrawer(GravityCompat.START);
                ContentListViewNavigationDrawer currentObject = adapter.getItem(position);

                ContentMainFragment fragment = new ContentMainFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable(CLASS_NAME_MAIN_ACTIVITY, currentObject);
                fragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().add(R.id.webViewLayout, fragment).commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (canGoBack) {
            ((onBackPressedInterface) getSupportFragmentManager().findFragmentById(R.id.webViewLayout)).onBackPressedWebView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
