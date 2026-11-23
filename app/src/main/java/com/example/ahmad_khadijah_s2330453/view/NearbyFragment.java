package com.example.ahmad_khadijah_s2330453.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.location.Location;
import android.location.LocationManager;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ahmad_khadijah_s2330453.R;
import com.example.ahmad_khadijah_s2330453.model.Situation;

import java.util.ArrayList;

public class NearbyFragment extends Fragment {

    private Button nearbyButton;
    private LinearLayout nearbyContent;
    private double phoneLatitude;
    private double phoneLongitude;
    private boolean hasPhoneLocation = false;
    private int selectedRadius = 5;

    private SituationAdapter adapter;
    private ArrayList<Situation> nearbySituations = new ArrayList<>();

    public NearbyFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nearby, container, false);

        nearbyButton = view.findViewById(R.id.btnSearchNearby);
        nearbyContent = view.findViewById(R.id.nearbyContent);

        // Set up RecyclerView once
        RecyclerView recyclerView = view.findViewById(R.id.nearbyRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new SituationAdapter(nearbySituations);
        recyclerView.setAdapter(adapter);

        int[] distances = {5, 10, 25, 50};

        SeekBar seekBar = view.findViewById(R.id.distanceSeekBar);
        TextView distanceText = view.findViewById(R.id.distanceText);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceText.setText("Distance: " + distances[progress] + " km");
                selectedRadius = distances[progress];

                if (fromUser && hasPhoneLocation) {
                    updateNearbySituations();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        nearbyButton.setOnClickListener(v -> checkLocationPermission());

        return view;
    }

    private void showNearbyContent() {
        nearbyButton.setVisibility(View.GONE);
        nearbyContent.setVisibility(View.VISIBLE);
    }

    private final ActivityResultLauncher<String> locationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            showNearbyContent();
                            getPhoneLocation();
                        } else {
                            new AlertDialog.Builder(requireContext())
                                    .setTitle("Location Permission Denied")
                                    .setMessage("Location access is required to use the Nearby feature.")
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .setCancelable(false)
                                    .show();
                        }
                    });

    private void checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            showNearbyContent();
            getPhoneLocation();

        } else {
            locationPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void getPhoneLocation() {

        LocationManager locationManager = (LocationManager)
                requireContext().getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                location -> {

                    phoneLatitude = location.getLatitude();
                    phoneLongitude = location.getLongitude();

                    android.util.Log.d("LocationTest",
                            "Device latitude: " + phoneLatitude
                                    + ", longitude: " + phoneLongitude);

                    hasPhoneLocation = true;
                    updateNearbySituations();
                },
                null
        );
    }

    private float calculateDistance(double latitude, double longitude) {

        float[] result = new float[1];

        Location.distanceBetween(
                phoneLatitude, phoneLongitude,
                latitude, longitude,
                result
        );

        return result[0] / 1000;
    }

    private void updateNearbySituations() {

        if (!isAdded()) return;

        MainActivity mainActivity = (MainActivity) requireActivity();
        ArrayList<Situation> situations = mainActivity.getAllSituations();

        nearbySituations.clear();

        for (Situation situation : situations) {

            float distance = calculateDistance(
                    situation.getLatitude(),
                    situation.getLongitude()
            );

            if (distance <= selectedRadius) {
                nearbySituations.add(situation);
            }
        }

        adapter.notifyDataSetChanged();

    }
}