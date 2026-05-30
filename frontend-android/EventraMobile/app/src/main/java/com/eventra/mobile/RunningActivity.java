package com.eventra.mobile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import android.util.Log;

public class RunningActivity extends AppCompatActivity {

    private TextView tvActivityTitle, tvDistance, tvTime, tvPace, tvCalories, tvSpeed;
    private Button btnStartActivity;

    private long eventId;
    private String eventTitle;

    private long activitySessionId = 0;
    private boolean isRunning = false;

    private long startTimeMillis = 0;
    private int elapsedSeconds = 0;

    private double totalDistanceMeters = 0.0;
    private Location lastLocation = null;

    private static final float MIN_DISTANCE_METERS = 2.0f;
    private static final float MAX_DISTANCE_METERS = 80.0f;
    private static final float MAX_ACCURACY_METERS = 25.0f;
    private static final float MAX_REASONABLE_SPEED_KMH = 30.0f;

    private double smoothedSpeedKmh = 0.0;
    private static final double SPEED_SMOOTHING_FACTOR = 0.25;

    private double estimatedWeightKg = 75.0;
    private Handler timerHandler = new Handler(Looper.getMainLooper());

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private static final String START_ACTIVITY_URL =
            "http://172.20.10.11:3006/activities/start";

    private static final String SEND_LOCATION_URL =
            "http://172.20.10.11:3006/activities/location";

    private static final String FINISH_ACTIVITY_URL =
            "http://172.20.10.11:3006/activities/finish";

    private static final String ACTIVE_PARTICIPANTS_URL =
            "http://172.20.10.11:3006/activities/event/";

    private final ArrayList<ActiveParticipant> activeParticipants = new ArrayList<>();

    private Handler participantsHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);

        eventId = getIntent().getLongExtra("event_id", 0);
        eventTitle = getIntent().getStringExtra("event_title");

        initViews();
        setupInitialData();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnStartActivity.setOnClickListener(v -> {
            if (!isRunning) {
                startActivitySession();
            } else {
                finishActivitySession();
            }
        });
    }

    private void initViews() {
        tvActivityTitle = findViewById(R.id.tvActivityTitle);
        tvDistance = findViewById(R.id.tvDistance);
        tvTime = findViewById(R.id.tvTime);
        tvPace = findViewById(R.id.tvPace);
        tvCalories = findViewById(R.id.tvCalories);
        tvSpeed = findViewById(R.id.tvSpeed);
        btnStartActivity = findViewById(R.id.btnStartActivity);
    }

    private void setupInitialData() {
        tvActivityTitle.setText(eventTitle != null ? eventTitle : "Actividad deportiva");

        tvDistance.setText("0.00");
        tvTime.setText("00:00:00");
        tvPace.setText("Ritmo\n\n0.00\nmin/km");
        tvCalories.setText("Calorías\n\n0\nkcal");
        tvSpeed.setText("Vel. Prom.\n\n0.0\nkm/h");

        btnStartActivity.setText("▷  Iniciar Carrera");
    }

    private void startActivitySession() {
        if (eventId == 0) {
            Toast.makeText(this, "Evento no válido", Toast.LENGTH_LONG).show();
            return;
        }

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

                            resetTrackingData();
                            isRunning = true;

                            btnStartActivity.setText("■  Parar Carrera");

                            startTimer();
                            startLocationUpdates();
                            startActiveParticipantsUpdates();

                            Toast.makeText(this, "Carrera iniciada", Toast.LENGTH_LONG).show();
                        } else {
                            JSONObject json = new JSONObject(response);
                            Toast.makeText(
                                    this,
                                    json.optString("message", "No fue posible iniciar la carrera"),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error procesando inicio", Toast.LENGTH_LONG).show();
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

    private void resetTrackingData() {
        startTimeMillis = System.currentTimeMillis();
        elapsedSeconds = 0;
        totalDistanceMeters = 0.0;
        lastLocation = null;

        tvDistance.setText("0.00");
        tvTime.setText("00:00:00");
        tvPace.setText("Ritmo\n\n0.00\nmin/km");
        tvCalories.setText("Calorías\n\n0\nkcal");
        tvSpeed.setText("Vel. Prom.\n\n0.0\nkm/h");
    }

    private void startTimer() {
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isRunning) return;

            elapsedSeconds = (int) ((System.currentTimeMillis() - startTimeMillis) / 1000);

            updateMetricsUI();

            timerHandler.postDelayed(this, 1000);
        }
    };

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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
                if (locationResult == null || !isRunning) return;

                for (Location location : locationResult.getLocations()) {
                    processNewLocation(location);
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                getMainLooper()
        );
    }

    private void processNewLocation(Location location) {
        if (location == null) return;

        if (location.hasAccuracy() && location.getAccuracy() > MAX_ACCURACY_METERS) {
            return;
        }

        if (lastLocation != null) {
            float distanceMeters = lastLocation.distanceTo(location);
            long timeDiffMillis = location.getTime() - lastLocation.getTime();

            if (timeDiffMillis > 0) {
                double timeHours = timeDiffMillis / 3600000.0;
                double instantSpeedKmh = (distanceMeters / 1000.0) / timeHours;

                boolean validDistance =
                        distanceMeters >= MIN_DISTANCE_METERS &&
                                distanceMeters <= MAX_DISTANCE_METERS;

                boolean validSpeed =
                        instantSpeedKmh >= 0 &&
                                instantSpeedKmh <= MAX_REASONABLE_SPEED_KMH;

                if (validDistance && validSpeed) {
                    totalDistanceMeters += distanceMeters;

                    if (smoothedSpeedKmh == 0.0) {
                        smoothedSpeedKmh = instantSpeedKmh;
                    } else {
                        smoothedSpeedKmh =
                                (SPEED_SMOOTHING_FACTOR * instantSpeedKmh) +
                                        ((1 - SPEED_SMOOTHING_FACTOR) * smoothedSpeedKmh);
                    }
                }
            }
        }

        lastLocation = location;
        updateMetricsUI();

        if (activitySessionId > 0) {
            sendLocationToBackend(location);
        }
    }

    private void updateMetricsUI() {
        double distanceKm = totalDistanceMeters / 1000.0;

        tvDistance.setText(String.format("%.2f", distanceKm));
        tvTime.setText(formatTime(elapsedSeconds));

        double averageSpeedKmh =
                elapsedSeconds > 0
                        ? distanceKm / (elapsedSeconds / 3600.0)
                        : 0.0;

        int paceSecondsPerKm =
                distanceKm > 0.01
                        ? (int) Math.round(elapsedSeconds / distanceKm)
                        : 0;

        int calories = estimateCalories(distanceKm, elapsedSeconds, averageSpeedKmh);

        tvSpeed.setText("Vel. Prom.\n\n" + String.format("%.1f", averageSpeedKmh) + "\nkm/h");

        if (paceSecondsPerKm > 0) {
            tvPace.setText("Ritmo\n\n" + formatPace(paceSecondsPerKm) + "\nmin/km");
        } else {
            tvPace.setText("Ritmo\n\n--:--\nmin/km");
        }

        tvCalories.setText("Calorías\n\n" + calories + "\nkcal");
    }

    private int estimateCalories(double distanceKm, int elapsedSeconds, double averageSpeedKmh) {
        if (distanceKm <= 0 || elapsedSeconds <= 0) {
            return 0;
        }

        double met;

        if (averageSpeedKmh < 6) {
            met = 3.8;
        } else if (averageSpeedKmh < 8) {
            met = 7.0;
        } else if (averageSpeedKmh < 10) {
            met = 9.8;
        } else if (averageSpeedKmh < 12) {
            met = 11.0;
        } else if (averageSpeedKmh < 14) {
            met = 12.8;
        } else {
            met = 14.5;
        }

        double hours = elapsedSeconds / 3600.0;

        double calories = met * estimatedWeightKg * hours;

        return (int) Math.round(calories);
    }

    private String formatTime(int totalSeconds) {
        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", h, m, s);
    }

    private String formatPace(int paceSeconds) {
        int m = paceSeconds / 60;
        int s = paceSeconds % 60;

        return String.format("%d:%02d", m, s);
    }

    private void sendLocationToBackend(Location location) {
        String token = new SessionManager(this).getToken();

        if (token == null || token.trim().isEmpty()) return;

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

    private void finishActivitySession() {
        isRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
        participantsHandler.removeCallbacks(participantsRunnable);

        stopLocationUpdates();

        String token = new SessionManager(this).getToken();

        if (token == null || token.trim().isEmpty()) return;

        double distanceKm = totalDistanceMeters / 1000.0;

        final double averageSpeedKmh =
                elapsedSeconds > 0
                        ? distanceKm / (elapsedSeconds / 3600.0)
                        : 0.0;

        final int averagePaceSecondsPerKm =
                distanceKm > 0
                        ? (int) (elapsedSeconds / distanceKm)
                        : 0;

        int calories = estimateCalories(distanceKm, elapsedSeconds, averageSpeedKmh);

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(FINISH_ACTIVITY_URL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject body = new JSONObject();
                body.put("activity_session_id", activitySessionId);
                body.put("total_time_seconds", elapsedSeconds);
                body.put("total_distance_km", distanceKm);
                body.put("average_speed_kmh", averageSpeedKmh);
                body.put("average_pace_seconds_per_km", averagePaceSecondsPerKm);
                body.put("calories_burned", calories);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();

                runOnUiThread(() -> {
                    if (responseCode >= 200 && responseCode < 300) {
                        Toast.makeText(this, "Carrera finalizada y guardada", Toast.LENGTH_LONG).show();
                        btnStartActivity.setText("▷  Iniciar Carrera");
                        activitySessionId = 0;
                    } else {
                        Toast.makeText(this, "No fue posible finalizar la carrera", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error conectando con backend al finalizar", Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void loadActiveParticipants() {
        String token = new SessionManager(this).getToken();

        if (token == null || token.trim().isEmpty()) {
            return;
        }

        if (eventId == 0) {
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(
                        ACTIVE_PARTICIPANTS_URL
                                + eventId
                                + "/active"
                );

                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();

                InputStream is = responseCode >= 200 && responseCode < 300
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                if (responseCode == 200) {
                    JSONArray array = new JSONArray(response.toString());

                    activeParticipants.clear();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);

                        boolean isTracking = obj.optBoolean("is_tracking", false);

                        if (isTracking) {
                            ActiveParticipant participant = new ActiveParticipant(
                                    obj.optLong("activity_session_id"),
                                    obj.optLong("auth_user_id"),
                                    obj.optDouble("latitude"),
                                    obj.optDouble("longitude"),
                                    obj.optDouble("speed_kmh"),
                                    obj.optInt("current_position")
                            );

                            activeParticipants.add(participant);
                        }
                    }

                    Log.d("ACTIVE_PARTICIPANTS", "Total activos: " + activeParticipants.size());
                }

            } catch (Exception e) {
                Log.e("ACTIVE_PARTICIPANTS", "Error cargando participantes: " + e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    private void startActiveParticipantsUpdates() {
        participantsHandler.postDelayed(participantsRunnable, 3000);
    }

    private final Runnable participantsRunnable = new Runnable() {
        @Override
        public void run() {
            if (isRunning) {
                loadActiveParticipants();
                participantsHandler.postDelayed(this, 3000);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
        participantsHandler.removeCallbacks(participantsRunnable);
        stopLocationUpdates();
    }
}