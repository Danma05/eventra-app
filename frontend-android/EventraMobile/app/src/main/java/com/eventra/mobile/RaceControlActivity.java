package com.eventra.mobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Scanner;

public class RaceControlActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvRaceTitle, tvLiveRanking;
    private EditText etStartLatitude, etStartLongitude, etFinishLatitude, etFinishLongitude;
    private Button btnSaveRoute, btnStartRace, btnPauseRace, btnResumeRace, btnFinishRace, btnRefreshRanking;

    private SessionManager sessionManager;
    private long eventId;
    private String eventTitle;

    private static final String EVENTS_BASE_URL = ApiConfig.EVENTS_URL + "/events";
    private static final String ACTIVITY_BASE_URL = ApiConfig.ACTIVITY_URL + "/activities";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_race_control);

        sessionManager = new SessionManager(this);
        eventId = getIntent().getLongExtra("event_id", 0);
        eventTitle = getIntent().getStringExtra("event_title");

        initViews();
        setupListeners();
        loadEventData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvRaceTitle = findViewById(R.id.tvRaceTitle);
        tvLiveRanking = findViewById(R.id.tvLiveRanking);
        etStartLatitude = findViewById(R.id.etStartLatitude);
        etStartLongitude = findViewById(R.id.etStartLongitude);
        etFinishLatitude = findViewById(R.id.etFinishLatitude);
        etFinishLongitude = findViewById(R.id.etFinishLongitude);
        btnSaveRoute = findViewById(R.id.btnSaveRoute);
        btnStartRace = findViewById(R.id.btnStartRace);
        btnPauseRace = findViewById(R.id.btnPauseRace);
        btnResumeRace = findViewById(R.id.btnResumeRace);
        btnFinishRace = findViewById(R.id.btnFinishRace);
        btnRefreshRanking = findViewById(R.id.btnRefreshRanking);

        tvRaceTitle.setText(eventTitle != null ? "Control: " + eventTitle : "Control de carrera");
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSaveRoute.setOnClickListener(v -> saveRoute());
        btnStartRace.setOnClickListener(v -> sendEmptyPost(EVENTS_BASE_URL + "/" + eventId + "/start", "Carrera iniciada"));
        btnPauseRace.setOnClickListener(v -> sendEmptyPost(EVENTS_BASE_URL + "/" + eventId + "/pause", "Carrera pausada"));
        btnResumeRace.setOnClickListener(v -> sendEmptyPost(EVENTS_BASE_URL + "/" + eventId + "/resume", "Carrera reanudada"));
        btnFinishRace.setOnClickListener(v -> {
            sendEmptyPost(EVENTS_BASE_URL + "/" + eventId + "/finish", "Carrera finalizada");
            sendEmptyPost(ACTIVITY_BASE_URL + "/event/" + eventId + "/finish-open-sessions", "Sesiones abiertas cerradas");
        });
        btnRefreshRanking.setOnClickListener(v -> loadLiveRanking());
    }

    private String getTokenOrNull() {
        String token = sessionManager.getToken();
        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_LONG).show();
            return null;
        }
        return token;
    }

    private void loadEventData() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(EVENTS_BASE_URL + "/" + eventId);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();
                InputStream is = responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream();
                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                if (responseCode == 200) {
                    JSONObject event = new JSONObject(response);
                    runOnUiThread(() -> {
                        etStartLatitude.setText(event.optString("start_latitude", ""));
                        etStartLongitude.setText(event.optString("start_longitude", ""));
                        etFinishLatitude.setText(event.optString("finish_latitude", ""));
                        etFinishLongitude.setText(event.optString("finish_longitude", ""));
                    });
                }
            } catch (Exception ignored) {
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void saveRoute() {
        String token = getTokenOrNull();
        if (token == null) return;

        String startLat = etStartLatitude.getText().toString().trim();
        String startLng = etStartLongitude.getText().toString().trim();
        String finishLat = etFinishLatitude.getText().toString().trim();
        String finishLng = etFinishLongitude.getText().toString().trim();

        if (startLat.isEmpty() || startLng.isEmpty() || finishLat.isEmpty() || finishLng.isEmpty()) {
            Toast.makeText(this, "Completa salida y meta", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(EVENTS_BASE_URL + "/" + eventId + "/route");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                JSONObject body = new JSONObject();
                body.put("start_latitude", Double.parseDouble(startLat));
                body.put("start_longitude", Double.parseDouble(startLng));
                body.put("finish_latitude", Double.parseDouble(finishLat));
                body.put("finish_longitude", Double.parseDouble(finishLng));

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                InputStream is = responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream();
                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                runOnUiThread(() -> showServerResult(responseCode, response, "Ruta guardada"));
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error guardando ruta", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void sendEmptyPost(String urlString, String successMessage) {
        String token = getTokenOrNull();
        if (token == null) return;

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                os.write("{}".getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                InputStream is = responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream();
                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                runOnUiThread(() -> showServerResult(responseCode, response, successMessage));
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "No fue posible conectar", Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void loadLiveRanking() {
        String token = getTokenOrNull();
        if (token == null) return;

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(ACTIVITY_BASE_URL + "/event/" + eventId + "/live-ranking");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();
                InputStream is = responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream();
                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                if (responseCode == 200) {
                    JSONArray array = new JSONArray(response);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject p = array.getJSONObject(i);
                        builder.append("#").append(p.optInt("current_position"))
                                .append("  Corredor ").append(p.optLong("auth_user_id"))
                                .append("\nMeta: ").append(formatMeters(p.optDouble("distance_to_finish_meters", 0)))
                                .append(" | Gap: ").append(formatMeters(p.optDouble("gap_to_previous_meters", 0)))
                                .append(" | ").append(p.optString("status", ""))
                                .append("\n\n");
                    }
                    String text = builder.length() == 0 ? "Todavía no hay corredores activos" : builder.toString();
                    runOnUiThread(() -> tvLiveRanking.setText(text));
                } else {
                    runOnUiThread(() -> tvLiveRanking.setText("No fue posible cargar ranking"));
                }
            } catch (Exception e) {
                runOnUiThread(() -> tvLiveRanking.setText("Error cargando ranking"));
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private String formatMeters(double meters) {
        if (meters >= 1000) {
            return String.format(Locale.US, "%.2f km", meters / 1000.0);
        }
        return String.format(Locale.US, "%.0f m", meters);
    }

    private void showServerResult(int responseCode, String response, String defaultSuccess) {
        try {
            JSONObject json = new JSONObject(response);
            String message = json.optString("message", responseCode >= 200 && responseCode < 300 ? defaultSuccess : "Operación no completada");
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, responseCode >= 200 && responseCode < 300 ? defaultSuccess : "Operación no completada", Toast.LENGTH_LONG).show();
        }
    }
}
