package com.eventra.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class EventsFragment extends Fragment {

    private RecyclerView recyclerEvents;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private EventAdapter adapter;
    private final ArrayList<Event> eventList = new ArrayList<>();

    private static final String EVENTS_URL = "http://172.20.10.11:3003/events";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_events, container, false);

        recyclerEvents = view.findViewById(R.id.recyclerEvents);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setVisibility(View.GONE);
        }

        adapter = new EventAdapter(eventList, event -> {
            Intent intent = new Intent(requireContext(), EventDetailActivity.class);
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

        recyclerEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerEvents.setAdapter(adapter);

        loadEvents();

        return view;
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

                int responseCode = conn.getResponseCode();

                InputStream is = responseCode == 200
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        eventList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            eventList.add(new Event(
                                    obj.optLong("id"),
                                    obj.optLong("organizer_auth_user_id"),
                                    obj.optString("title"),
                                    obj.optString("description"),
                                    obj.optString("event_date"),
                                    obj.optString("location"),
                                    obj.optInt("capacity"),
                                    obj.optString("status"),
                                    obj.optString("image_url")
                            ));
                        }

                        adapter.notifyDataSetChanged();

                        recyclerEvents.setVisibility(eventList.isEmpty() ? View.GONE : View.VISIBLE);
                        tvEmpty.setVisibility(eventList.isEmpty() ? View.VISIBLE : View.GONE);

                    } catch (Exception e) {
                        tvEmpty.setText("Error al cargar eventos");
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    tvEmpty.setText("No fue posible conectar con el backend");
                    tvEmpty.setVisibility(View.VISIBLE);
                });
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}