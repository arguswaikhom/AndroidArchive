package com.crown.smartparking.page;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.crown.smartparking.R;
import com.crown.smartparking.controller.AppController;
import com.crown.smartparking.databinding.MapFragmentBinding;
import com.crown.smartparking.model.History;
import com.crown.smartparking.model.Parking;
import com.crown.smartparking.model.ParkingSpot;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, EventListener<QuerySnapshot> {
    private final String TAG = MapFragment.class.getName();
    private List<Parking> parkingList = new ArrayList<>();
    private Location myLocation;
    private MainActivity activity;

    private MapFragmentBinding binding;

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
        View root = inflater.inflate(R.layout.map_fragment, container, false);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Dexter.withActivity(getActivity()).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted()) {
                        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                        mapFragment.getMapAsync(MapFragment.this::onMapReady);
                    }

                    if (report.isAnyPermissionPermanentlyDenied()) {
                        showSettingsDialog(getActivity());
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            }).check();
        } else {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapFragment.this::onMapReady);
        }

        return root;
    }

    @Override
    @SuppressLint("MissingPermission")
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        googleMap.setTrafficEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        getAllParking(googleMap);
    }

    private void getAllParking(GoogleMap googleMap) {
        FirebaseFirestore.getInstance().collection("parking").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();
                for (int i = 0; i < documentSnapshots.size(); i++) {
                    Parking parking = documentSnapshots.get(i).toObject(Parking.class);
                    if (parking == null) continue;
                    MarkerOptions markerOptions = new MarkerOptions();
                    parking.setParkingId(documentSnapshots.get(i).getId());
                    parkingList.add(parking);
                    markerOptions.position(new LatLng(parking.getLocation().getLatitude(), parking.getLocation().getLongitude()));
                    googleMap.addMarker(markerOptions);
                }
                Log.v(TAG, "Parking: " + documentSnapshots.size());
            }
            zoomAtMe(googleMap);
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Can't get parking data", Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("MissingPermission")
    private void zoomAtMe(GoogleMap googleMap) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                myLocation = location;
                Log.v(TAG, "Current location: " + location.getLatitude() + " : " + location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
            } else {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Error")
                        .setMessage("Unable to get your current location. \n\nTIP: Run Google Map first and try again.")
                        .setPositiveButton("Open Map", ((dialog, which) -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/"));
                            getActivity().startActivity(intent);
                        }))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    private static void showSettingsDialog(Activity activity) {
        new AlertDialog.Builder(activity)
                .setTitle("Grant Permissions")
                .setMessage("This app needs permission to use this feature. You can grant them in app settings.")
                .setPositiveButton("Goto Settings", (dialog, which) -> {
                    dialog.cancel();
                    openSettings(activity);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel())
                .show();
    }

    private static void openSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivityForResult(intent, 101);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng latLng = marker.getPosition();
        for (Parking parking : parkingList) {
            if (parking.getLocation().equals(new GeoPoint(latLng.latitude, latLng.longitude))) {
                showParkingDialog(parking);
                break;
            }
        }
        return false;
    }

    private void showParkingDialog(Parking parking) {
        if (getContext() == null) return;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_parking_details, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.LoadingDialogTheme).setView(view).create();

        TextView statusTV = view.findViewById(R.id.tv_dpd_status);
        ProgressBar loading = view.findViewById(R.id.pbar_dpd_loading);
        loading.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection(getString(R.string.ref_parking))
                .document(parking.getParkingId())
                .collection(getString(R.string.ref_parking_slot))
                .get()
                .addOnSuccessListener(snapshots -> {
                    loading.setVisibility(View.INVISIBLE);
                    boolean isPA = false;
                    if (snapshots != null && !snapshots.isEmpty()) {
                        for (DocumentSnapshot doc : snapshots) {
                            if (doc.exists()) {
                                Boolean isAvailable = (Boolean) doc.get("isAvailable");
                                if (isAvailable != null && isAvailable) {
                                    isPA = true;
                                    break;
                                }
                            }
                        }
                    }
                    statusTV.setVisibility(View.VISIBLE);
                    if (isPA) statusTV.setText("AVAILABLE");
                    else statusTV.setText("NOT AVAILABLE");
                })
                .addOnFailureListener(error -> {
                    loading.setVisibility(View.INVISIBLE);
                });


        ((TextView) view.findViewById(R.id.tv_dpd_title)).setText(parking.getTitle());
        ((TextView) view.findViewById(R.id.tv_dpd_address)).setText(parking.getAddress());
        ((TextView) view.findViewById(R.id.tv_dpd_price)).setText(String.format("Price: ₹ %s/min", parking.getPrice()));
        view.findViewById(R.id.direction).setOnClickListener(v -> {
            String uri = "https://www.google.com/maps/dir//" + parking.getLocation().getLatitude() + "," + parking.getLocation().getLongitude();
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        });

        if (myLocation != null) {
            Location parkingLocation = new Location("end");
            parkingLocation.setLatitude(parking.getLocation().getLatitude());
            parkingLocation.setLongitude(parking.getLocation().getLongitude());

            String distance = String.format(Locale.ENGLISH, "<b>%.2f KM</b> from your current location", myLocation.distanceTo(parkingLocation) / 1000);
            ((TextView) view.findViewById(R.id.tv_dpd_distance)).setText(Html.fromHtml(distance));
        }

        Glide.with(getContext()).load(parking.getImage()).into((ImageView) view.findViewById(R.id.iv_dpd_image));
        dialog.show();
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
        for (DocumentSnapshot doc : documents) {
            if (doc.exists()) {
                History item = doc.toObject(History.class);
                if (doc.getString("parking") != null) {
                    item.setParking(doc.getString("parking"));
                }

                if (item != null && item.getParkingSlot() == null && item.getEndTime() == null && item.getDuration() == null) {
                    chooseParkingSpot(item);
                }
            }
        }
    }

    private void chooseParkingSpot(History item) {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), ParkingSpotActivity.class);
        intent.putExtra(ParkingSpotActivity.KEY_PARKING_ID, item.getParking());
        startActivityForResult(intent, 0);
    }
}