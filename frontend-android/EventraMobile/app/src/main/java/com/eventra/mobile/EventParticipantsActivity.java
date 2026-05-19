package com.eventra.mobile;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class EventParticipantsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvTitle, tvParticipants;

    private SessionManager sessionManager;
    private long eventId;
    private String eventTitle;

    private static final String BASE_URL =
            "http://172.20.10.11:3004/registrations/event/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_participants);

        sessionManager = new SessionManager(this);

        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvParticipants = findViewById(R.id.tvParticipants);

        eventId = getIntent().getLongExtra("event_id", 0);
        eventTitle = getIntent().getStringExtra("event_title");

        tvTitle.setText("Participantes - " + eventTitle);

        btnBack.setOnClickListener(v -> finish());

        loadParticipants();
    }

    private void loadParticipants() {
        String token = sessionManager.getToken();

        if (token == null) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(BASE_URL + eventId + "/participants");
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = conn.getResponseCode();

                InputStream is = responseCode == 200
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                runOnUiThread(() -> {
                    try {
                        if (responseCode == 200) {
                            JSONArray array = new JSONArray(response);

                            if (array.length() == 0) {
                                tvParticipants.setText("Este evento aún no tiene participantes inscritos.");
                                return;
                            }

                            StringBuilder builder = new StringBuilder();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);

                                builder.append("Participante #")
                                        .append(obj.optLong("auth_user_id"))
                                        .append("\nEstado: ")
                                        .append(obj.optString("registration_status"))
                                        .append("\n\n");
                            }

                            tvParticipants.setText(builder.toString());
                        } else {
                            tvParticipants.setText("No fue posible cargar participantes.");
                        }
                    } catch (Exception e) {
                        tvParticipants.setText("Error procesando participantes.");
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "No fue posible conectar con backend-registrations", Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}