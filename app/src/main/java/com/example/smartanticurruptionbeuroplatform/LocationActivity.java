package com.example.smartanticurruptionbeuroplatform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST = 101;

    private TextView tvLocation;
    private Button btnGetLocation, btnOpenMap;

    private FusedLocationProviderClient locationClient;

    private double latitude = 0;
    private double longitude = 0;
    private boolean locationAvailable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


                tvLocation = findViewById(R.id.tvLocation);
                btnGetLocation = findViewById(R.id.btnGetLocation);
                btnOpenMap = findViewById(R.id.btnOpenMap);

                locationClient =
                        LocationServices.getFusedLocationProviderClient(this);

                btnGetLocation.setOnClickListener(v -> getCurrentLocation());

                btnOpenMap.setOnClickListener(v -> {
                    if (!locationAvailable) {
                        Toast.makeText(this,
                                "Get location first",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Uri uri = Uri.parse(
                            "https://www.openstreetmap.org/?mlat="
                                    + latitude + "&mlon=" + longitude
                                    + "#map=16/" + latitude + "/" + longitude
                    );

                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                });
            }

            private void getCurrentLocation() {

                boolean fineGranted =
                        ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED;

                boolean coarseGranted =
                        ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED;

                if (!fineGranted && !coarseGranted) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            },
                            LOCATION_REQUEST
                    );
                    return;
                }

                locationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                showLocation(location);
                            } else {
                                tvLocation.setText(
                                        "Location unavailable. Turn on GPS and try again."
                                );
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(this,
                                        "Could not get location",
                                        Toast.LENGTH_LONG).show()
                        );
            }

            private void showLocation(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                locationAvailable = true;

                tvLocation.setText(
                        "Latitude: " + latitude
                                + "\nLongitude: " + longitude
                );
            }

            @Override
            public void onRequestPermissionsResult(
                    int requestCode,
                    String[] permissions,
                    int[] grantResults) {

                super.onRequestPermissionsResult(
                        requestCode, permissions, grantResults);

                if (requestCode == LOCATION_REQUEST) {
                    boolean granted = false;

                    for (int result : grantResults) {
                        if (result == PackageManager.PERMISSION_GRANTED) {
                            granted = true;
                            break;
                        }
                    }

                    if (granted) {
                        getCurrentLocation();
                    } else {
                        Toast.makeText(this,
                                "Location permission denied",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
