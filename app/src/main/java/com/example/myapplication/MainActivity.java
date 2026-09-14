package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityResultLauncher<String> coarseLocationPermissionLauncher;
    private GoogleMap nMap;
    private FusedLocationProviderClient locationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        locationClient =
                LocationServices.getFusedLocationProviderClient(this);
        coarseLocationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Approximate location access granted
                        Toast.makeText(this, "Coarse location granted", Toast.LENGTH_SHORT).show();
//                        startLocationFeatures();
                    } else {
                        // Permission denied
                        Toast.makeText(this, "Coarse location denied", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // 3. Trigger the check on startup
        checkAndRequestCoarsePermission();
        // Obtain the SupportMapFragment and get notified when map is ready to use.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


    }
    private void checkAndRequestCoarsePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Already granted
        } else {
            // Request the single coarse permission
            coarseLocationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Set default camera view (e.g., centered in a location)
        nMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            googleMap.setMyLocationEnabled(true);

            return;
        }
        googleMap.setMyLocationEnabled(true);
        locationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {

                    if (location != null) {

                        LatLng userLocation = new LatLng(
                                location.getLatitude(),
                                location.getLongitude()
                        );

                        googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                        userLocation,
                                        15f
                                )
                        );
                    }
                });
//        googleMap.addMarker(new MarkerOptions().position(sydney).title("Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10));
    }
}