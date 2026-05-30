package com.eventra.mobile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

public class ResultsFragment extends Fragment {

    private EditText etSearchRunner;
    private LinearLayout layoutPublishedEvents;
    private TextView tvSelectedEvent, tvSelectedEventInfo, tvResultsEmpty;
    private RecyclerView recyclerResults;

    private ResultAdapter adapter;
    private final ArrayList<Result> resultList = new ArrayList<>();

    private SessionManager sessionManager;
    private long selectedEventId = 0;

    private static final String BASE_URL = ApiConfig.RESULTS_URL + "/results";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_results, container, false);

        sessionManager = new SessionManager(requireContext());

        etSearchRunner = view.findViewById(R.id.etSearchRunner);
        layoutPublishedEvents = view.findViewById(R.id.layoutPublishedEvents);
        tvSelectedEvent = view.findViewById(R.id.tvSelectedEvent);
        tvSelectedEventInfo = view.findViewById(R.id.tvSelectedEventInfo);
        tvResultsEmpty = view.findViewById(R.id.tvResultsEmpty);
        recyclerResults = view.findViewById(R.id.recyclerResults);

        adapter = new ResultAdapter(resultList);
        recyclerResults.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerResults.setAdapter(adapter);

        etSearchRunner.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectedEventId > 0) loadResultsByEvent(selectedEventId, s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadPublishedEvents();

        return view;
    }

    private void loadPublishedEvents() {
        String token = sessionManager.getToken();
        if (token == null) return;

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(BASE_URL + "/events/published");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int code = conn.getResponseCode();
                InputStream is = code == 200 ? conn.getInputStream() : conn.getErrorStream();

                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                requireActivity().runOnUiThread(() -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        layoutPublishedEvents.removeAllViews();

                        if (array.length() == 0) {
                            tvResultsEmpty.setVisibility(View.VISIBLE);
                            tvResultsEmpty.setText("No hay eventos con resultados publicados.");
                            return;
                        }

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject event = array.getJSONObject(i);

                            long eventId = event.optLong("id");
                            String title = event.optString("title", "Evento " + eventId);
                            String date = event.optString("event_date", "");
                            int capacity = event.optInt("capacity", 0);

                            Button button = new Button(requireContext());
                            button.setText(title);
                            button.setTextSize(12);
                            button.setAllCaps(false);
                            button.setBackgroundResource(R.drawable.bg_card_white);
                            button.setTextColor(getResources().getColor(R.color.text_primary));

                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    360,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(0, 0, 12, 0);
                            button.setLayoutParams(params);

                            button.setOnClickListener(v -> {
                                selectedEventId = eventId;
                                tvSelectedEvent.setText(title);
                                tvSelectedEventInfo.setText("Evento publicado");
                                loadResultsByEvent(eventId, etSearchRunner.getText().toString());
                            });

                            layoutPublishedEvents.addView(button);

                            if (i == 0) {
                                selectedEventId = eventId;
                                tvSelectedEvent.setText(title);
                                tvSelectedEventInfo.setText("Evento publicado");
                                loadResultsByEvent(eventId, "");
                            }
                        }

                    } catch (Exception e) {
                        tvResultsEmpty.setVisibility(View.VISIBLE);
                        tvResultsEmpty.setText("Error cargando eventos publicados.");
                    }
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    tvResultsEmpty.setVisibility(View.VISIBLE);
                    tvResultsEmpty.setText("No fue posible conectar con resultados.");
                });
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void loadResultsByEvent(long eventId, String search) {
        String token = sessionManager.getToken();
        if (token == null) return;

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                String urlString = BASE_URL + "/event/" + eventId;

                if (search != null && !search.trim().isEmpty()) {
                    urlString += "?search=" + URLEncoder.encode(search.trim(), "UTF-8");
                }

                URL url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int code = conn.getResponseCode();
                InputStream is = code == 200 ? conn.getInputStream() : conn.getErrorStream();

                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                requireActivity().runOnUiThread(() -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        resultList.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            resultList.add(new Result(
                                    obj.optLong("id"),
                                    obj.optLong("event_id"),
                                    obj.optLong("auth_user_id"),
                                    obj.optInt("position"),
                                    obj.optString("bib_number"),
                                    obj.optString("category"),
                                    obj.optInt("total_time_seconds"),
                                    obj.optInt("pace_seconds_per_km"),
                                    obj.optDouble("distance_km"),
                                    obj.optString("runner_name", "Corredor #" + obj.optLong("auth_user_id"))
                            ));
                        }

                        adapter.notifyDataSetChanged();

                        boolean empty = resultList.isEmpty();
                        tvResultsEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                        recyclerResults.setVisibility(empty ? View.GONE : View.VISIBLE);

                        if (empty) {
                            tvResultsEmpty.setText("No hay resultados para esta búsqueda.");
                        }

                    } catch (Exception e) {
                        tvResultsEmpty.setVisibility(View.VISIBLE);
                        tvResultsEmpty.setText("Error procesando ranking.");
                    }
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    tvResultsEmpty.setVisibility(View.VISIBLE);
                    tvResultsEmpty.setText("No fue posible cargar ranking.");
                });
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}