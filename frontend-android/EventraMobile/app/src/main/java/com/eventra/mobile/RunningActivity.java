package com.eventra.mobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class RunningActivity extends AppCompatActivity {

    private TextView tvActivityTitle, tvDistance, tvTime, tvPace, tvCalories, tvSpeed;
    private Button btnStartActivity;

    private long eventId;
    private String eventTitle;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private double currentLatitude = 0;
    private double currentLongitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);

        eventId = getIntent().getLongExtra("event_id", 0);
        eventTitle = getIntent().getStringExtra("event_title");

        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        tvDistance = findViewById(R.id.tvDistance);
        tvTime = findViewById(R.id.tvTime);
        tvPace = findViewById(R.id.tvPace);
        tvCalories = findViewById(R.id.tvCalories);
        tvSpeed = findViewById(R.id.tvSpeed);
        btnStartActivity = findViewById(R.id.btnStartActivity);

        tvActivityTitle.setText(eventTitle != null ? eventTitle : "Actividad deportiva");

        btnStartActivity.setOnClickListener(v -> {
            if (eventId == 0) {
                Toast.makeText(this, "Evento no válido", Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(this, "Actividad lista para iniciar", Toast.LENGTH_LONG).show();
        });

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        startLocationUpdates();
    }

    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    1001
            );

            return;
        }

        LocationRequest locationRequest =
                new LocationRequest.Builder(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        3000
                ).build();

        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {

                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    tvDistance.setText(
                            currentLatitude + "\n" + currentLongitude
                    );
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                getMainLooper()
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}