package com.eventra.mobile;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvFullName, tvEmail, tvPhone, tvBirthDate, tvGender, tvCity, tvCountry, tvBio, tvStatus;
    private ImageButton btnBack;
    private SessionManager sessionManager;

    private static final String PROFILE_URL = "http://172.20.10.11:3002/users/profile/me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);

        initViews();
        setupEvents();
        loadProfile();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);

        tvUsername = findViewById(R.id.tvUsername);
        tvFullName = findViewById(R.id.tvFullName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvBirthDate = findViewById(R.id.tvBirthDate);
        tvGender = findViewById(R.id.tvGender);
        tvCity = findViewById(R.id.tvCity);
        tvCountry = findViewById(R.id.tvCountry);
        tvBio = findViewById(R.id.tvBio);
        tvStatus = findViewById(R.id.tvStatus);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadProfile() {
        String token = sessionManager.getToken();
        String email = sessionManager.getEmail();

        if (token == null) {
            Toast.makeText(this, "No hay sesión activa", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(PROFILE_URL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
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

                runOnUiThread(() -> {
                    try {
                        if (responseCode == 200) {
                            JSONObject profileJson = new JSONObject(response);

                            String username = profileJson.optString("username", "No disponible");
                            String firstName = profileJson.optString("first_name", "");
                            String lastName = profileJson.optString("last_name", "");
                            String phone = profileJson.optString("phone", "No disponible");
                            String birthDate = profileJson.optString("birth_date", "No disponible");
                            String gender = profileJson.optString("gender", "No disponible");
                            String city = profileJson.optString("city", "No disponible");
                            String country = profileJson.optString("country", "No disponible");
                            String bio = profileJson.optString("bio", "No disponible");
                            String status = profileJson.optString("profile_status", "No disponible");

                            tvUsername.setText(username);
                            tvFullName.setText((firstName + " " + lastName).trim());
                            tvEmail.setText(email != null ? email : "No disponible");
                            tvPhone.setText(phone);
                            tvBirthDate.setText(birthDate);
                            tvGender.setText(gender);
                            tvCity.setText(city);
                            tvCountry.setText(country);
                            tvBio.setText(bio);
                            tvStatus.setText(status);
                        } else {
                            Toast.makeText(ProfileActivity.this, "No fue posible cargar el perfil", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(ProfileActivity.this, "Respuesta inválida del servidor", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(ProfileActivity.this, "No fue posible conectar con el backend", Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }
}