package com.eventra.mobile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;
import java.util.Scanner;

public class EditProfileActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText etUsername, etFirstName, etLastName, etPhone, etBirthDate;
    private EditText etGender, etCity, etCountry, etImageUrl, etBio;
    private Button btnSaveProfile;

    private SessionManager sessionManager;
    private String selectedBirthDate = "";

    private static final String PROFILE_URL =
            "http://172.20.10.11:3002/users/profile/me";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sessionManager = new SessionManager(this);

        initViews();
        setupEvents();
        loadProfile();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etUsername = findViewById(R.id.etUsername);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        etBirthDate = findViewById(R.id.etBirthDate);
        etGender = findViewById(R.id.etGender);
        etCity = findViewById(R.id.etCity);
        etCountry = findViewById(R.id.etCountry);
        etImageUrl = findViewById(R.id.etImageUrl);
        etBio = findViewById(R.id.etBio);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        etBirthDate.setOnClickListener(v -> showDatePicker());
        btnSaveProfile.setOnClickListener(v -> validateAndSaveProfile());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    int realMonth = month + 1;

                    selectedBirthDate = String.format(
                            Locale.US,
                            "%04d-%02d-%02d",
                            year,
                            realMonth,
                            dayOfMonth
                    );

                    etBirthDate.setText(String.format(
                            Locale.US,
                            "%02d/%02d/%04d",
                            dayOfMonth,
                            realMonth,
                            year
                    ));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void loadProfile() {
        String token = sessionManager.getToken();

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(PROFILE_URL);
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

                runOnUiThread(() -> {
                    try {
                        if (responseCode == 200) {
                            JSONObject profile = new JSONObject(response);

                            etUsername.setText(profile.optString("username", ""));
                            etFirstName.setText(profile.optString("first_name", ""));
                            etLastName.setText(profile.optString("last_name", ""));
                            etPhone.setText(profile.optString("phone", ""));
                            etGender.setText(profile.optString("gender", "PREFER_NOT_TO_SAY"));
                            etCity.setText(profile.optString("city", ""));
                            etCountry.setText(profile.optString("country", ""));
                            etImageUrl.setText(profile.optString("profile_image_url", ""));
                            etBio.setText(profile.optString("bio", ""));

                            String birthDate = profile.optString("birth_date", "");
                            if (birthDate != null && birthDate.length() >= 10) {
                                selectedBirthDate = birthDate.substring(0, 10);
                                etBirthDate.setText(formatVisibleDate(selectedBirthDate));
                            }
                        } else {
                            Toast.makeText(this, "No fue posible cargar perfil", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Error procesando perfil", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "No fue posible conectar con backend-user", Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private String formatVisibleDate(String date) {
        try {
            String[] parts = date.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return date;
        }
    }

    private void validateAndSaveProfile() {
        String username = etUsername.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String gender = etGender.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String country = etCountry.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String bio = etBio.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("El username es obligatorio");
            etUsername.requestFocus();
            return;
        }

        if (firstName.isEmpty()) {
            etFirstName.setError("El nombre es obligatorio");
            etFirstName.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            etLastName.setError("El apellido es obligatorio");
            etLastName.requestFocus();
            return;
        }

        if (gender.isEmpty()) {
            gender = "PREFER_NOT_TO_SAY";
        }

        if (!gender.equals("MALE")
                && !gender.equals("FEMALE")
                && !gender.equals("OTHER")
                && !gender.equals("PREFER_NOT_TO_SAY")) {
            etGender.setError("Usa MALE, FEMALE, OTHER o PREFER_NOT_TO_SAY");
            etGender.requestFocus();
            return;
        }

        saveProfile(username, firstName, lastName, phone, selectedBirthDate, gender, city, country, imageUrl, bio);
    }

    private void saveProfile(String username, String firstName, String lastName,
                             String phone, String birthDate, String gender,
                             String city, String country, String imageUrl, String bio) {

        String token = sessionManager.getToken();

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_LONG).show();
            return;
        }

        btnSaveProfile.setEnabled(false);
        btnSaveProfile.setText("Guardando...");

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(PROFILE_URL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Authorization", "Bearer " + token);
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                JSONObject json = new JSONObject();
                json.put("username", username);
                json.put("first_name", firstName);
                json.put("last_name", lastName);
                json.put("phone", phone);
                json.put("birth_date", birthDate.isEmpty() ? JSONObject.NULL : birthDate);
                json.put("gender", gender);
                json.put("city", city);
                json.put("country", country);
                json.put("profile_image_url", imageUrl);
                json.put("bio", bio);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();

                InputStream is = (responseCode >= 200 && responseCode < 300)
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                runOnUiThread(() -> {
                    btnSaveProfile.setEnabled(true);
                    btnSaveProfile.setText("Guardar perfil");

                    try {
                        JSONObject responseJson = new JSONObject(response);
                        String message = responseJson.optString("message", "Perfil actualizado");

                        if (responseCode == 200) {
                            Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Respuesta inválida del servidor", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    btnSaveProfile.setEnabled(true);
                    btnSaveProfile.setText("Guardar perfil");
                    Toast.makeText(this, "No fue posible conectar con backend-user", Toast.LENGTH_LONG).show();
                });
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }
}