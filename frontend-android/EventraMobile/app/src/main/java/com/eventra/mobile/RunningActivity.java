package com.eventra.mobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RunningActivity extends AppCompatActivity {

    private TextView tvActivityTitle, tvDistance, tvTime, tvPace, tvCalories, tvSpeed;
    private Button btnStartActivity;

    private long eventId;
    private String eventTitle;

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
    }
}