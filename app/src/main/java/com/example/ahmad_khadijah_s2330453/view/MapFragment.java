package com.example.ahmad_khadijah_s2330453.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ahmad_khadijah_s2330453.R;
import com.example.ahmad_khadijah_s2330453.model.Situation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager()
                        .findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        LatLng scotland = new LatLng(56.5, -4.2);

        googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(scotland, 7)
        );

        MainActivity mainActivity = (MainActivity) requireActivity();

        for (Situation situation : mainActivity.getAllSituations()) {

            LatLng position = new LatLng(
                    situation.getLatitude(),
                    situation.getLongitude()
            );

            googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(situation.getSituationType())
                    .snippet(situation.getDescription()));

        }

        googleMap.setOnMarkerClickListener(marker -> {

            LatLng position = marker.getPosition();

            android.util.Log.d("MapTest",
                    "Marker selected - Latitude: " + position.latitude
                            + ", Longitude: " + position.longitude);

            return false;
        });
    }

}