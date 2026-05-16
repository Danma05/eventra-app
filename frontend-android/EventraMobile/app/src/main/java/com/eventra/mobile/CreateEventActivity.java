package com.eventra.mobile;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import android.content.Intent;

public class CreateEventActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etTitle, etDescription, etDate, etLocation, etCapacity, etImageUrl;
    private Button btnCreateEvent;

    private SessionManager sessionManager;

    private static final String CREATE_EVENT_URL = "http://172.20.10.11:3003/events";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        sessionManager = new SessionManager(this);

        btnBack = findViewById(R.id.btnBack);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        etCapacity = findViewById(R.id.etCapacity);
        etImageUrl = findViewById(R.id.etImageUrl);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);

        btnBack.setOnClickListener(v -> finish());
        btnCreateEvent.setOnClickListener(v -> validateAndCreateEvent());
    }

    private void validateAndCreateEvent() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String capacityText = etCapacity.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError("El título es obligatorio");
            etTitle.requestFocus();
            return;
        }

        if (title.length() < 3) {
            etTitle.setError("El título debe tener al menos 3 caracteres");
            etTitle.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            etDescription.setError("La descripción es obligatoria");
            etDescription.requestFocus();
            return;
        }

        if (date.isEmpty()) {
            etDate.setError("La fecha y hora son obligatorias");
            etDate.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            etLocation.setError("La ubicación es obligatoria");
            etLocation.requestFocus();
            return;
        }

        if (location.length() < 3) {
            etLocation.setError("La ubicación debe tener al menos 3 caracteres");
            etLocation.requestFocus();
            return;
        }

        if (capacityText.isEmpty()) {
            etCapacity.setError("La capacidad es obligatoria");
            etCapacity.requestFocus();
            return;
        }

        int capacity;

        try {
            capacity = Integer.parseInt(capacityText);
        } catch (NumberFormatException e) {
            etCapacity.setError("La capacidad debe ser numérica");
            etCapacity.requestFocus();
            return;
        }

        if (capacity <= 0) {
            etCapacity.setError("La capacidad debe ser mayor a 0");
            etCapacity.requestFocus();
            return;
        }

        if (imageUrl.isEmpty()) {
            etImageUrl.setError("La imagen de referencia es obligatoria");
            etImageUrl.requestFocus();
            return;
        }

        if (!isValidUrl(imageUrl)) {
            etImageUrl.setError("Ingresa una URL válida");
            etImageUrl.requestFocus();
            return;
        }

        createEvent(title, description, date, location, capacity, imageUrl);
    }

    private void createEvent(String title, String description, String date,
                             String location, int capacity, String imageUrl) {

        String token = sessionManager.getToken();

        if (token == null) {
            Toast.makeText(this, "Debes iniciar sesión para crear eventos", Toast.LENGTH_LONG).show();
            return;
        }

        btnCreateEvent.setEnabled(false);
        btnCreateEvent.setText("Creando...");

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(CREATE_EVENT_URL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                JSONObject json = new JSONObject();
                json.put("title", title);
                json.put("description", description);
                json.put("event_date", date);
                json.put("location", location);
                json.put("capacity", capacity);
                json.put("image_url", imageUrl);

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
                    btnCreateEvent.setEnabled(true);
                    btnCreateEvent.setText("Crear evento");

                    try {
                        JSONObject responseJson = new JSONObject(response);
                        String message = responseJson.optString("message", "Respuesta procesada");

                        if (responseCode == 201) {

                            Toast.makeText(
                                    CreateEventActivity.this,
                                    "Evento creado correctamente",
                                    Toast.LENGTH_LONG
                            ).show();

                            Intent intent = new Intent(
                                    CreateEventActivity.this,
                                    MainNavActivity.class
                            );

                            intent.putExtra("open_events", true);

                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);

                            finish();
                        } else {
                            Toast.makeText(CreateEventActivity.this, message, Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(CreateEventActivity.this, "Respuesta inválida del servidor", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    btnCreateEvent.setEnabled(true);
                    btnCreateEvent.setText("Crear evento");
                    Toast.makeText(CreateEventActivity.this, "No fue posible conectar con el backend", Toast.LENGTH_LONG).show();
                });
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    private boolean isValidUrl(String url) {
        try {
            Uri uri = Uri.parse(url);
            return uri.getScheme() != null &&
                    (uri.getScheme().equals("http") || uri.getScheme().equals("https")) &&
                    uri.getHost() != null;
        } catch (Exception e) {
            return false;
        }
    }
}