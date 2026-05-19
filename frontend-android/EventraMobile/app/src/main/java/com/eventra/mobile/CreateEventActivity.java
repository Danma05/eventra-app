package com.eventra.mobile;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;

public class CreateEventActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etTitle, etDescription, etDate, etTime, etLocation, etCapacity, etImageUrl;
    private Button btnCreateEvent;

    private SessionManager sessionManager;

    private boolean editMode = false;
    private long eventId = 0;

    private String selectedDate = "";
    private String selectedTime = "";

    private static final String EVENTS_BASE_URL = "http://172.20.10.11:3003/events";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        sessionManager = new SessionManager(this);

        btnBack = findViewById(R.id.btnBack);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etLocation = findViewById(R.id.etLocation);
        etCapacity = findViewById(R.id.etCapacity);
        etImageUrl = findViewById(R.id.etImageUrl);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);

        readIntentData();

        btnBack.setOnClickListener(v -> finish());
        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());

        btnCreateEvent.setOnClickListener(v -> validateAndSubmitEvent());
    }

    private void readIntentData() {
        editMode = getIntent().getBooleanExtra("edit_mode", false);

        if (editMode) {
            eventId = getIntent().getLongExtra("event_id", 0);

            String title = getIntent().getStringExtra("title");
            String description = getIntent().getStringExtra("description");
            String eventDate = getIntent().getStringExtra("date");
            String location = getIntent().getStringExtra("location");
            int capacity = getIntent().getIntExtra("capacity", 0);
            String imageUrl = getIntent().getStringExtra("image_url");

            etTitle.setText(title);
            etDescription.setText(description);
            etLocation.setText(location);
            etCapacity.setText(String.valueOf(capacity));
            etImageUrl.setText(imageUrl);

            if (eventDate != null && eventDate.length() >= 16) {
                selectedDate = eventDate.substring(0, 10);
                selectedTime = eventDate.substring(11, 16) + ":00";

                etDate.setText(formatVisibleDate(selectedDate));
                etTime.setText(eventDate.substring(11, 16));
            }

            btnCreateEvent.setText("Actualizar Evento");
        } else {
            btnCreateEvent.setText("Crear Evento");
        }
    }

    private String formatVisibleDate(String date) {
        try {
            String[] parts = date.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return date;
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    int realMonth = month + 1;

                    selectedDate = String.format(Locale.US, "%04d-%02d-%02d", year, realMonth, dayOfMonth);
                    etDate.setText(String.format(Locale.US, "%02d/%02d/%04d", dayOfMonth, realMonth, year));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    selectedTime = String.format(Locale.US, "%02d:%02d:00", hourOfDay, minute);
                    etTime.setText(String.format(Locale.US, "%02d:%02d", hourOfDay, minute));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );

        dialog.show();
    }

    private void validateAndSubmitEvent() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String capacityText = etCapacity.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        if (title.isEmpty() || title.length() < 3) {
            etTitle.setError("El nombre debe tener al menos 3 caracteres");
            etTitle.requestFocus();
            return;
        }

        if (selectedDate.isEmpty()) {
            etDate.setError("Selecciona la fecha");
            etDate.requestFocus();
            return;
        }

        if (selectedTime.isEmpty()) {
            etTime.setError("Selecciona la hora");
            etTime.requestFocus();
            return;
        }

        if (location.isEmpty() || location.length() < 3) {
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

        if (description.isEmpty()) {
            etDescription.setError("La descripción es obligatoria");
            etDescription.requestFocus();
            return;
        }

        if (imageUrl.isEmpty() || !isValidUrl(imageUrl)) {
            etImageUrl.setError("Ingresa una URL válida");
            etImageUrl.requestFocus();
            return;
        }

        String eventDate = selectedDate + "T" + selectedTime;

        if (editMode) {
            updateEvent(title, description, eventDate, location, capacity, imageUrl);
        } else {
            createEvent(title, description, eventDate, location, capacity, imageUrl);
        }
    }

    private void createEvent(String title, String description, String eventDate,
                             String location, int capacity, String imageUrl) {
        sendEventRequest("POST", EVENTS_BASE_URL, title, description, eventDate, location, capacity, imageUrl);
    }

    private void updateEvent(String title, String description, String eventDate,
                             String location, int capacity, String imageUrl) {
        sendEventRequest("PUT", EVENTS_BASE_URL + "/" + eventId, title, description, eventDate, location, capacity, imageUrl);
    }

    private void sendEventRequest(String method, String urlString, String title, String description,
                                  String eventDate, String location, int capacity, String imageUrl) {
        String token = sessionManager.getToken();

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_LONG).show();
            return;
        }

        btnCreateEvent.setEnabled(false);
        btnCreateEvent.setText(editMode ? "Actualizando..." : "Creando...");

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod(method);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                JSONObject json = new JSONObject();
                json.put("title", title);
                json.put("description", description);
                json.put("event_date", eventDate);
                json.put("location", location);
                json.put("capacity", capacity);
                json.put("image_url", imageUrl);
                json.put("status", "ACTIVE");

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
                    btnCreateEvent.setText(editMode ? "Actualizar Evento" : "Crear Evento");

                    try {
                        JSONObject responseJson = new JSONObject(response);
                        String message = responseJson.optString("message", "Operación realizada");

                        if (responseCode == 200 || responseCode == 201) {
                            Toast.makeText(
                                    CreateEventActivity.this,
                                    editMode ? "Evento actualizado correctamente" : "Evento creado correctamente",
                                    Toast.LENGTH_LONG
                            ).show();

                            Intent intent = new Intent(CreateEventActivity.this, OrganizerPanelActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                    btnCreateEvent.setText(editMode ? "Actualizar Evento" : "Crear Evento");
                    Toast.makeText(CreateEventActivity.this, "No fue posible conectar con backend-events", Toast.LENGTH_LONG).show();
                });
            } finally {
                if (conn != null) conn.disconnect();
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