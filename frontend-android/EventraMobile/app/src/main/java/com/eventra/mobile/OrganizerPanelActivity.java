package com.eventra.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OrganizerPanelActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnCreateEvent, btnMyEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_panel);

        btnBack = findViewById(R.id.btnBack);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        btnMyEvents = findViewById(R.id.btnMyEvents);

        btnBack.setOnClickListener(v -> finish());

        btnCreateEvent.setOnClickListener(v -> {
            Toast.makeText(this, "Formulario de creación en SCRUM-109", Toast.LENGTH_SHORT).show();
        });

        btnMyEvents.setOnClickListener(v -> {
            Toast.makeText(this, "Listado de eventos creados se implementará después", Toast.LENGTH_SHORT).show();
        });
    }
}