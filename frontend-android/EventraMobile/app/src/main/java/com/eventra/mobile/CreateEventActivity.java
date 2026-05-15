package com.eventra.mobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CreateEventActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etTitle, etDescription, etDate, etLocation, etCapacity, etImageUrl;
    private Button btnCreateEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        btnBack = findViewById(R.id.btnBack);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        etCapacity = findViewById(R.id.etCapacity);
        etImageUrl = findViewById(R.id.etImageUrl);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);

        btnBack.setOnClickListener(v -> finish());

        btnCreateEvent.setOnClickListener(v -> validateForm());
    }

    private void validateForm() {
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

        Toast.makeText(this, "Formulario válido. Conexión en SCRUM-111", Toast.LENGTH_SHORT).show();
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