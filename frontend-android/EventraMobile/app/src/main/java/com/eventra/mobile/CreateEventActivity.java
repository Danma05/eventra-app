package com.eventra.mobile;

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

        btnCreateEvent.setOnClickListener(v ->
                Toast.makeText(this, "Validaciones en SCRUM-110", Toast.LENGTH_SHORT).show()
        );
    }
}