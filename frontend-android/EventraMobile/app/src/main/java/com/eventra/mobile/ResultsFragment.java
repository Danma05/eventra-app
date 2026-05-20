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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ResultsFragment extends Fragment {

    private TextView tvResultsSummary;
    private TextView tvResultsList;

    private SessionManager sessionManager;

    private static final String MY_REGISTRATIONS_URL =
            "http://172.20.10.11:3004/registrations/my";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_results, container, false);

        sessionManager = new SessionManager(requireContext());

        tvResultsSummary = view.findViewById(R.id.tvResultsSummary);
        tvResultsList = view.findViewById(R.id.tvResultsList);

        loadMyResults();

        return view;
    }

    private void loadMyResults() {
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

                InputStream is = responseCode == 200
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                requireActivity().runOnUiThread(() -> {
                    try {
                        if (responseCode == 200) {
                            JSONArray array = new JSONArray(response);

                            tvResultsSummary.setText(array.length() + "\nEventos inscritos");

                            if (array.length() == 0) {
                                tvResultsList.setText("Aún no tienes resultados o eventos inscritos.");
                                return;
                            }

                            StringBuilder builder = new StringBuilder();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);

                                builder.append("Evento ID: ")
                                        .append(obj.optLong("event_id"))
                                        .append("\nEstado inscripción: ")
                                        .append(obj.optString("registration_status"))
                                        .append("\nFecha inscripción: ")
                                        .append(obj.optString("created_at"))
                                        .append("\n\n");
                            }

                            tvResultsList.setText(builder.toString());

                        } else {
                            tvResultsList.setText("No fue posible cargar resultados.");
                        }

                    } catch (Exception e) {
                        tvResultsList.setText("Error procesando resultados.");
                    }
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        tvResultsList.setText("No fue posible conectar con backend-registrations.")
                );
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }
}