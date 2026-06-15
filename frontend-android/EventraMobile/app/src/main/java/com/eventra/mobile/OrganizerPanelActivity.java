package com.eventra.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

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

import java.io.OutputStream;
import java.util.HashMap;

import android.app.AlertDialog;

import android.app.AlertDialog;
import java.io.OutputStream;

public class OrganizerPanelActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private Button btnCreateEvent;

    private TextView tvTotalEvents, tvActiveEvents, tvTotalRegistrations;
    private TextView tvTabActive, tvTabFinished, tvOrganizerEmpty;

    private RecyclerView recyclerOrganizerEvents;
    private OrganizerEventAdapter adapter;

    private final ArrayList<Event> allOrganizerEvents = new ArrayList<>();
    private final ArrayList<Event> filteredEvents = new ArrayList<>();

    private SessionManager sessionManager;

    private boolean showingActive = true;

    private final HashMap<Long, Integer> registrationCountsByEventId = new HashMap<>();

    private static final String REGISTRATION_COUNTS_URL = ApiConfig.REGISTRATIONS_URL + "/registrations/counts";
    private static final String ORGANIZER_EVENTS_URL = ApiConfig.EVENTS_URL + "/events/organizer/my";
    private static final String ORGANIZER_COUNTS_URL = ApiConfig.REGISTRATIONS_URL + "/registrations/my/counts";
    private static final String EVENTS_BASE_URL = ApiConfig.EVENTS_URL + "/events";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_panel);

        sessionManager = new SessionManager(this);

        initViews();
        setupRecycler();
        setupEvents();

        loadOrganizerCounts();
        loadOrganizerEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);

        tvTotalEvents = findViewById(R.id.tvTotalEvents);
        tvActiveEvents = findViewById(R.id.tvActiveEvents);
        tvTotalRegistrations = findViewById(R.id.tvTotalRegistrations);

        tvTabActive = findViewById(R.id.tvTabActive);
        tvTabFinished = findViewById(R.id.tvTabFinished);
        tvOrganizerEmpty = findViewById(R.id.tvOrganizerEmpty);

        recyclerOrganizerEvents = findViewById(R.id.recyclerOrganizerEvents);
    }

    private void setupRecycler() {
        adapter = new OrganizerEventAdapter(
                filteredEvents,
                registrationCountsByEventId,
                new OrganizerEventAdapter.OnOrganizerEventClickListener() {
                    @Override
                    public void onView(Event event) {
                        Intent intent = new Intent(OrganizerPanelActivity.this, EventDetailActivity.class);
                        intent.putExtra("event_id", event.getId());
                        intent.putExtra("title", event.getTitle());
                        intent.putExtra("description", event.getDescription());
                        intent.putExtra("date", event.getEventDate());
                        intent.putExtra("location", event.getLocation());
                        intent.putExtra("capacity", event.getCapacity());
                        intent.putExtra("status", event.getStatus());
                        intent.putExtra("image_url", event.getImageUrl());
                        startActivity(intent);
                    }

                    @Override
                    public void onEdit(Event event) {
                        Intent intent = new Intent(OrganizerPanelActivity.this, CreateEventActivity.class);

                        intent.putExtra("edit_mode", true);
                        intent.putExtra("event_id", event.getId());
                        intent.putExtra("title", event.getTitle());
                        intent.putExtra("description", event.getDescription());
                        intent.putExtra("date", event.getEventDate());
                        intent.putExtra("location", event.getLocation());
                        intent.putExtra("capacity", event.getCapacity());
                        intent.putExtra("status", event.getStatus());
                        intent.putExtra("image_url", event.getImageUrl());

                        startActivity(intent);
                    }

                    @Override
                    public void onDelete(Event event) {
                        confirmDeleteEvent(event);
                    }

                    @Override
                    public void onViewParticipants(Event event) {
                        Intent intent = new Intent(OrganizerPanelActivity.this, EventParticipantsActivity.class);
                        intent.putExtra("event_id", event.getId());
                        intent.putExtra("event_title", event.getTitle());
                        startActivity(intent);
                    }

                    @Override
                    public void onPublishResults(Event event) {
                        confirmFinishEvent(event);
                    }

                    @Override
                    public void onRaceControl(Event event) {
                        Intent intent = new Intent(OrganizerPanelActivity.this, RaceControlActivity.class);
                        intent.putExtra("event_id", event.getId());
                        intent.putExtra("event_title", event.getTitle());
                        startActivity(intent);
                    }

                    @Override
                    public void onPublishRanking(Event event) {
                        publishRanking(event);
                    }

                }
        );

        recyclerOrganizerEvents.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrganizerEvents.setAdapter(adapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerPanelActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });

        tvTabActive.setOnClickListener(v -> {
            showingActive = true;
            updateTabs();
            applyFilter();
        });

        tvTabFinished.setOnClickListener(v -> {
            showingActive = false;
            updateTabs();
            applyFilter();
        });
    }

    private void loadOrganizerCounts() {
        String token = sessionManager.getToken();

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(ORGANIZER_COUNTS_URL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
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
                    try {
                        if (responseCode == 200) {
                            JSONObject json = new JSONObject(response);

                            tvTotalEvents.setText(String.valueOf(json.optInt("total_events", 0)));
                            tvActiveEvents.setText(String.valueOf(json.optInt("active_events", 0)));
                            tvTotalRegistrations.setText(String.valueOf(json.optInt("total_registrations", 0)));
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error procesando estadísticas", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "No fue posible conectar con estadísticas", Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void loadOrganizerEvents() {
        String token = sessionManager.getToken();

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(ORGANIZER_EVENTS_URL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
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
                    try {
                        if (responseCode == 200) {
                            JSONArray jsonArray = new JSONArray(response);
                            allOrganizerEvents.clear();

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
                                        obj.optString("race_status", "CREATED"),
                                        obj.optString("image_url")
                                );

                                event.setResultsPublished(obj.optBoolean("results_published", false));

                                allOrganizerEvents.add(event);
                            }

                            applyFilter();
                            loadRegistrationCountsForOrganizerEvents();
                        } else {
                            Toast.makeText(this, "No fue posible cargar eventos del organizador", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error procesando eventos del organizador", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "No fue posible conectar con eventos", Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void applyFilter() {
        filteredEvents.clear();

        for (Event event : allOrganizerEvents) {
            String status = event.getStatus() != null ? event.getStatus() : "";
            String raceStatus = event.getRaceStatus() != null ? event.getRaceStatus() : "CREATED";

            boolean isDeletedOrInactive =
                    "INACTIVE".equalsIgnoreCase(status) ||
                            "CANCELLED".equalsIgnoreCase(status) ||
                            "CANCELLED".equalsIgnoreCase(raceStatus);

            boolean isFinished = "FINISHED".equalsIgnoreCase(raceStatus);

            if (showingActive && !isDeletedOrInactive && !isFinished) {
                filteredEvents.add(event);
            }

            if (!showingActive && isFinished) {
                filteredEvents.add(event);
            }
        }

        adapter.setShowingActive(showingActive);
        adapter.notifyDataSetChanged();

        if (filteredEvents.isEmpty()) {
            tvOrganizerEmpty.setVisibility(View.VISIBLE);
            recyclerOrganizerEvents.setVisibility(View.GONE);
        } else {
            tvOrganizerEmpty.setVisibility(View.GONE);
            recyclerOrganizerEvents.setVisibility(View.VISIBLE);
        }
    }

    private void updateTabs() {
        if (showingActive) {
            tvTabActive.setTextColor(getResources().getColor(R.color.primary));
            tvTabFinished.setTextColor(getResources().getColor(R.color.text_secondary));
        } else {
            tvTabActive.setTextColor(getResources().getColor(R.color.text_secondary));
            tvTabFinished.setTextColor(getResources().getColor(R.color.primary));
        }
    }

    private void loadRegistrationCountsForOrganizerEvents() {
        String token = sessionManager.getToken();

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_LONG).show();
            return;
        }

        if (allOrganizerEvents.isEmpty()) {
            registrationCountsByEventId.clear();
            adapter.notifyDataSetChanged();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(REGISTRATION_COUNTS_URL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                JSONArray eventIds = new JSONArray();

                for (Event event : allOrganizerEvents) {
                    eventIds.put(event.getId());
                }

                JSONObject body = new JSONObject();
                body.put("event_ids", eventIds);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();

                InputStream is = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                runOnUiThread(() -> {
                    try {
                        if (responseCode == 200) {
                            JSONArray jsonArray = new JSONArray(response);

                            registrationCountsByEventId.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                long eventId = obj.optLong("event_id");
                                int total = obj.optInt("total", 0);

                                registrationCountsByEventId.put(eventId, total);
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(
                                    OrganizerPanelActivity.this,
                                    "No fue posible cargar inscritos por evento",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(
                                OrganizerPanelActivity.this,
                                "Error procesando inscritos por evento",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(
                                OrganizerPanelActivity.this,
                                "No fue posible conectar con conteo de inscritos",
                                Toast.LENGTH_LONG
                        ).show()
                );
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    private void confirmDeleteEvent(Event event) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar evento")
                .setMessage("¿Seguro que deseas eliminar el evento \"" + event.getTitle() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteEvent(event))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void deleteEvent(Event event) {
        String token = sessionManager.getToken();

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(EVENTS_BASE_URL + "/" + event.getId());
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("DELETE");
                conn.setRequestProperty("Authorization", "Bearer " + token);
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
                    try {
                        if (responseCode == 200 || responseCode == 204) {
                            Toast.makeText(
                                    OrganizerPanelActivity.this,
                                    "Evento eliminado correctamente",
                                    Toast.LENGTH_LONG
                            ).show();

                            loadOrganizerCounts();
                            loadOrganizerEvents();

                        } else {
                            String message = "No fue posible eliminar el evento";

                            try {
                                JSONObject errorJson = new JSONObject(response);
                                message = errorJson.optString("message", message);
                            } catch (Exception ignored) {}

                            Toast.makeText(
                                    OrganizerPanelActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(
                                OrganizerPanelActivity.this,
                                "Error procesando eliminación",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(
                                OrganizerPanelActivity.this,
                                "No fue posible conectar con backend-events",
                                Toast.LENGTH_LONG
                        ).show()
                );
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    private void confirmFinishEvent(Event event) {
        if ("FINISHED".equalsIgnoreCase(event.getRaceStatus())) {
            Toast.makeText(
                    this,
                    "Este evento ya está completado",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Publicar resultados")
                .setMessage("¿Deseas marcar el evento \"" + event.getTitle() + "\" como completado?")
                .setPositiveButton("Publicar", (dialog, which) -> finishEvent(event))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void finishEvent(Event event) {
        String token = sessionManager.getToken();

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(EVENTS_BASE_URL + "/" + event.getId());
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                JSONObject body = new JSONObject();
                body.put("title", event.getTitle());
                body.put("description", event.getDescription());
                body.put("event_date", event.getEventDate());
                body.put("location", event.getLocation());
                body.put("capacity", event.getCapacity());
                body.put("status", "FINISHED");
                body.put("image_url", event.getImageUrl());

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();

                InputStream is = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                runOnUiThread(() -> {
                    if (responseCode == 200) {
                        Toast.makeText(
                                OrganizerPanelActivity.this,
                                "Resultados publicados. Evento completado.",
                                Toast.LENGTH_LONG
                        ).show();

                        showingActive = false;
                        updateTabs();

                        loadOrganizerCounts();
                        loadOrganizerEvents();

                    } else {
                        try {
                            JSONObject errorJson = new JSONObject(response);
                            String message = errorJson.optString(
                                    "message",
                                    "No fue posible completar el evento"
                            );

                            Toast.makeText(
                                    OrganizerPanelActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();

                        } catch (Exception e) {
                            Toast.makeText(
                                    OrganizerPanelActivity.this,
                                    "No fue posible completar el evento",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(
                                OrganizerPanelActivity.this,
                                "No fue posible conectar con backend-events",
                                Toast.LENGTH_LONG
                        ).show()
                );
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    private void publishRanking(Event event) {
        String token = sessionManager.getToken();

        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(ApiConfig.RESULTS_URL + "/results/event/" + event.getId() + "/publish");
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();

                InputStream is = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                Log.d("OrganizerPanel", "Publish ranking responseCode: " + responseCode);
                Log.d("OrganizerPanel", "Publish ranking response: " + response);

                runOnUiThread(() -> {
                    if (responseCode == 200) {
                        event.setResultsPublished(true);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(
                                OrganizerPanelActivity.this,
                                "Ranking publicado correctamente",
                                Toast.LENGTH_LONG
                        ).show();
                    } else {
                        Toast.makeText(
                                OrganizerPanelActivity.this,
                                "No se pudo publicar el ranking",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });

            } catch (Exception e) {
                Log.e("OrganizerPanel", "Error al publicar ranking", e);

                runOnUiThread(() -> Toast.makeText(
                        OrganizerPanelActivity.this,
                        "Error de conexión al publicar ranking",
                        Toast.LENGTH_LONG
                ).show());

            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrganizerCounts();
        loadOrganizerEvents();
    }
}