package com.eventra.mobile;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class EventDetailActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView ivEventImage;
    private TextView tvTitle, tvDescription, tvDate, tvLocation, tvCapacity, tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        btnBack = findViewById(R.id.btnBack);
        ivEventImage = findViewById(R.id.ivEventImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvDate = findViewById(R.id.tvDate);
        tvLocation = findViewById(R.id.tvLocation);
        tvCapacity = findViewById(R.id.tvCapacity);
        tvStatus = findViewById(R.id.tvStatus);

        btnBack.setOnClickListener(v -> finish());

        loadData();
    }

    private void loadData() {
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
}