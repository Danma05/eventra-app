package com.eventra.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class ActivitySelectionFragment extends Fragment {

    private RecyclerView recyclerRegisteredEvents;
    private TextView tvEmptyRegisteredEvents;

    private RegisteredEventAdapter adapter;
    private final ArrayList<RegisteredEvent> registeredEvents = new ArrayList<>();

    private SessionManager sessionManager;

    private static final String MY_REGISTRATIONS_URL =
            "http://172.20.10.11:3004/registrations/my";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_activity_selection, container, false);

        sessionManager = new SessionManager(requireContext());

        recyclerRegisteredEvents = view.findViewById(R.id.recyclerRegisteredEvents);
        tvEmptyRegisteredEvents = view.findViewById(R.id.tvEmptyRegisteredEvents);

        adapter = new RegisteredEventAdapter(registeredEvents, event -> {
            Bundle bundle = new Bundle();
            bundle.putLong("event_id", event.getEventId());
            bundle.putString("event_title", event.getTitle());

            ActivityFragment activityFragment = new ActivityFragment();
            activityFragment.setArguments(bundle);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameContainer, activityFragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerRegisteredEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerRegisteredEvents.setAdapter(adapter);

        loadMyRegistrations();

        return view;
    }

    private void loadMyRegistrations() {
        String token = sessionManager.getToken();

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Sesión inválida", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(MY_REGISTRATIONS_URL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();

                InputStream is = responseCode >= 200 && responseCode < 300
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                requireActivity().runOnUiThread(() -> {
                    try {
                        if (responseCode == 200) {
                            JSONArray array = new JSONArray(response);

                            registeredEvents.clear();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);

                                long eventId = obj.optLong("event_id");
                                String status = obj.optString("registration_status", "REGISTERED");

                                String title = obj.optString("title", "Evento #" + eventId);
                                String date = obj.optString("event_date", "Fecha no disponible");
                                String location = obj.optString("location", "Ubicación no disponible");

                                if ("REGISTERED".equalsIgnoreCase(status)) {
                                    registeredEvents.add(new RegisteredEvent(
                                            eventId,
                                            title,
                                            date,
                                            location,
                                            status
                                    ));
                                }
                            }

                            adapter.notifyDataSetChanged();

                            boolean empty = registeredEvents.isEmpty();
                            tvEmptyRegisteredEvents.setVisibility(empty ? View.VISIBLE : View.GONE);
                            recyclerRegisteredEvents.setVisibility(empty ? View.GONE : View.VISIBLE);
                        } else {
                            Toast.makeText(requireContext(), "No fue posible cargar tus inscripciones", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Error procesando inscripciones", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "No fue posible conectar con inscripciones", Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}