package com.eventra.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class EventsActivity extends AppCompatActivity {

    private static final String TAG = "EventsActivity";

    private ImageButton btnBack;
    private RecyclerView recyclerEvents;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private EventAdapter adapter;
    private final ArrayList<Event> eventList = new ArrayList<>();

    private static final String EVENTS_URL = ApiConfig.EVENTS_URL + "/events";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        initViews();
        setupRecycler();
        setupEvents();
        loadEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        recyclerEvents = findViewById(R.id.recyclerEvents);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);
    }

    private void setupRecycler() {
        adapter = new EventAdapter(eventList, event -> {
            Intent intent = new Intent(EventsActivity.this, EventDetailActivity.class);
            intent.putExtra("event_id", event.getId());
            intent.putExtra("title", event.getTitle());
            intent.putExtra("description", event.getDescription());
            intent.putExtra("date", event.getEventDate());
            intent.putExtra("location", event.getLocation());
            intent.putExtra("capacity", event.getCapacity());
            intent.putExtra("status", event.getStatus());
            intent.putExtra("image_url", event.getImageUrl());
            startActivity(intent);
        });

        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerEvents.setAdapter(adapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadEvents() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerEvents.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(EVENTS_URL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();

                InputStream is = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    try {
                        if (responseCode == 200) {
                            eventList.clear();

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                Event event = new Event(
                                        obj.optLong("id"),
                                        obj.optLong("organizer_auth_user_id"),
                                        obj.optString("title"),
                                        obj.optString("description"),
                                        obj.optString("event_date"),
                                        obj.optString("location"),
                                        obj.optInt("capacity"),
                                        obj.optString("status"),
                                        obj.optString("image_url")
                                );

                                eventList.add(event);
                            }

                            if (eventList.isEmpty()) {
                                tvEmpty.setVisibility(View.VISIBLE);
                                recyclerEvents.setVisibility(View.GONE);
                            } else {
                                adapter.notifyDataSetChanged();
                                recyclerEvents.setVisibility(View.VISIBLE);
                                tvEmpty.setVisibility(View.GONE);
                            }
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            tvEmpty.setText("No fue posible cargar eventos");
                        }
                    } catch (Exception e) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        tvEmpty.setText("Respuesta inválida del servidor");
                    }
                });

            } catch (Exception e) {

                Log.e(TAG, "Error de conexión durante la activity", e);

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("No fue posible conectar con el backend");
                });
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }
}