package com.crown.smartparking.page;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.crown.smartparking.R;
import com.crown.smartparking.controller.AppController;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;

import static com.crown.smartparking.controller.FirebaseMessagingService.sendDeviceToken;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolBar = findViewById(R.id.sp_toolbar);
        setSupportActionBar(mToolBar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getHeaderView(0).<TextView>findViewById(R.id.tv_nhm_name).setText(AppController.getInstance().getUser().getDisplayName());

        getDeviceToken();
        openHomeFragment();
    }

    private void getDeviceToken() {
        String token = AppController.getInstance().getDeviceToken();
        Log.v(TAG, "Token state: " + token);
        if (token == null) {
            Log.v(TAG, "Getting token");
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                Log.v(TAG, "" + task.isSuccessful());

                if (task.isSuccessful()) {
                    Log.v(TAG, "Token: " + task.getResult().getToken());
                    sendDeviceToken(task.getResult().getToken());
                }
            }).addOnFailureListener(error -> Log.v(TAG, "#####\n" + error + "\n#####"));
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_history: {
                openHistoryFragment();
                break;
            }
            case R.id.nav_home: {
                openHomeFragment();
                break;
            }
            case R.id.nav_sign_out: {
                AppController.getInstance().signOut(this);
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openHomeFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_abm_include, new MapFragment());
        fragmentTransaction.commit();
    }

    private void openHistoryFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_abm_include, new HistoryFragment());
        fragmentTransaction.commit();
    }
}
