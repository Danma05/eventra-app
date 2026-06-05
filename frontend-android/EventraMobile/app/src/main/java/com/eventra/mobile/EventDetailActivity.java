package com.eventra.mobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class EventDetailActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView ivEventImage;
    private TextView tvTitle, tvDescription, tvDate, tvLocation, tvCapacity, tvStatus;
    private Button btnRegisterEvent;

    private SessionManager sessionManager;
    private long eventId;

    private static final String REGISTER_EVENT_URL = ApiConfig.REGISTRATIONS_URL + "/registrations";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        sessionManager = new SessionManager(this);

        btnBack = findViewById(R.id.btnBack);
        ivEventImage = findViewById(R.id.ivEventImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvDate = findViewById(R.id.tvDate);
        tvLocation = findViewById(R.id.tvLocation);
        tvCapacity = findViewById(R.id.tvCapacity);
        tvStatus = findViewById(R.id.tvStatus);
        btnRegisterEvent = findViewById(R.id.btnRegisterEvent);

        btnBack.setOnClickListener(v -> finish());

        loadData();

        btnRegisterEvent.setOnClickListener(v -> registerToEvent());
    }

    private void loadData() {
        eventId = getIntent().getLongExtra("event_id", 0);
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String date = getIntent().getStringExtra("date");
        String location = getIntent().getStringExtra("location");
        int capacity = getIntent().getIntExtra("capacity", 0);
        String status = getIntent().getStringExtra("status");
        String imageUrl = getIntent().getStringExtra("image_url");

        tvTitle.setText(title);
        tvDescription.setText(description);
        tvDate.setText(date);
        tvLocation.setText(location);
        tvCapacity.setText("Capacidad: " + capacity);
        tvStatus.setText("Estado: " + status);

        ImageLoader.loadImage(imageUrl, ivEventImage);
    }

    private void registerToEvent() {
        String token = sessionManager.getToken();

        if (token == null) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_LONG).show();
            return;
        }

        btnRegisterEvent.setEnabled(false);
        btnRegisterEvent.setText("Inscribiendo...");

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(REGISTER_EVENT_URL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                JSONObject json = new JSONObject();
                json.put("event_id", eventId);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();

                InputStream is = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                runOnUiThread(() -> {
                    btnRegisterEvent.setEnabled(true);
                    btnRegisterEvent.setText("Inscribirme");

                    try {
                        JSONObject responseJson = new JSONObject(response);
                        String message = responseJson.optString("message", "Respuesta procesada");
                        Toast.makeText(EventDetailActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(EventDetailActivity.this, "Respuesta inválida del servidor", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    btnRegisterEvent.setEnabled(true);
                    btnRegisterEvent.setText("Inscribirme");
                    Toast.makeText(EventDetailActivity.this, "No fue posible conectar con el backend", Toast.LENGTH_LONG).show();
                });
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }
}