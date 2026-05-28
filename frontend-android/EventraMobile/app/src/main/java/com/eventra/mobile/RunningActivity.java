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

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class RunningActivity extends AppCompatActivity {

    private TextView tvActivityTitle, tvDistance, tvTime, tvPace, tvCalories, tvSpeed;
    private Button btnStartActivity;

    private long eventId;
    private String eventTitle;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private double currentLatitude = 0;
    private double currentLongitude = 0;

    private long activitySessionId = 0;

    private static final String START_ACTIVITY_URL =
            "http://172.20.10.11:3006/activities/start";

    private static final String SEND_LOCATION_URL =
            "http://172.20.10.11:3006/activities/location";

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

            startActivitySession();
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

                    if (activitySessionId > 0) {
                        sendLocationToBackend(location);
                    }

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

    private void startActivitySession() {
        String token = new SessionManager(this).getToken();

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(START_ACTIVITY_URL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("event_id", eventId);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();

                InputStream is = responseCode >= 200 && responseCode < 300
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                runOnUiThread(() -> {
                    try {
                        if (responseCode == 201) {
                            JSONObject json = new JSONObject(response);
                            JSONObject session = json.getJSONObject("session");

                            activitySessionId = session.optLong("id");

                            Toast.makeText(this, "Actividad iniciada correctamente", Toast.LENGTH_LONG).show();

                            btnStartActivity.setText("Actividad en curso");
                            btnStartActivity.setEnabled(false);
                        } else {
                            JSONObject json = new JSONObject(response);
                            Toast.makeText(this, json.optString("message", "No fue posible iniciar actividad"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error procesando inicio de actividad", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "No fue posible conectar con Activity API", Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void sendLocationToBackend(Location location) {
        String token = new SessionManager(this).getToken();

        if (token == null || token.trim().isEmpty()) {
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(SEND_LOCATION_URL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("activity_session_id", activitySessionId);
                body.put("latitude", location.getLatitude());
                body.put("longitude", location.getLongitude());
                body.put("altitude", location.hasAltitude() ? location.getAltitude() : 0);
                body.put("speed_kmh", location.hasSpeed() ? location.getSpeed() * 3.6 : 0);
                body.put("accuracy_meters", location.hasAccuracy() ? location.getAccuracy() : 0);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                conn.getResponseCode();

            } catch (Exception ignored) {
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}