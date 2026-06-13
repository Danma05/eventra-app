package com.eventra.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private ImageButton btnBack, btnTogglePassword;
    private EditText etName, etEmail, etPassword;
    private RadioGroup rgUserType;
    private RadioButton rbRunner, rbOrganizer;
    private Button btnRegister;
    private TextView tvTerms;

    private boolean isPasswordVisible = false;
    private SessionManager sessionManager;

    private static final String REGISTER_URL = ApiConfig.AUTH_URL + "/auth/register";
    private static final String LOGIN_URL = ApiConfig.AUTH_URL + "/auth/login";
    private static final String CREATE_PROFILE_URL = ApiConfig.USER_URL + "/users/profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);

        initViews();
        setupEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        rgUserType = findViewById(R.id.rgUserType);
        rbRunner = findViewById(R.id.rbRunner);
        rbOrganizer = findViewById(R.id.rbOrganizer);

        btnRegister = findViewById(R.id.btnRegister);
        tvTerms = findViewById(R.id.tvTerms);

        rbRunner.setChecked(true);
        updateUserTypeSelection();
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());

        btnRegister.setOnClickListener(v -> validateForm());

        tvTerms.setOnClickListener(v ->
                Toast.makeText(this, "Pendiente pantalla de términos y condiciones", Toast.LENGTH_SHORT).show()
        );

        rgUserType.setOnCheckedChangeListener((group, checkedId) -> updateUserTypeSelection());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btnTogglePassword.setImageResource(android.R.drawable.ic_menu_view);
            isPasswordVisible = false;
        } else {
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            btnTogglePassword.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            isPasswordVisible = true;
        }
        etPassword.setSelection(etPassword.getText().length());
    }

    private void updateUserTypeSelection() {
        if (rbRunner.isChecked()) {
            rbRunner.setBackgroundResource(R.drawable.bg_option_selected);
            rbOrganizer.setBackgroundResource(R.drawable.bg_option_unselected);
        } else if (rbOrganizer.isChecked()) {
            rbOrganizer.setBackgroundResource(R.drawable.bg_option_selected);
            rbRunner.setBackgroundResource(R.drawable.bg_option_unselected);
        }
    }

    private void validateForm() {
        String fullName = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            etName.setError("El nombre completo es obligatorio");
            etName.requestFocus();
            return;
        }

        if (!fullName.contains(" ")) {
            etName.setError("Ingresa nombre y apellido");
            etName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("El correo electrónico es obligatorio");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Ingresa un correo válido");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("La contraseña es obligatoria");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 8) {
            etPassword.setError("La contraseña debe tener al menos 8 caracteres");
            etPassword.requestFocus();
            return;
        }

        if (!NetworkUtils.isConnected(this)) {
            Toast.makeText(this, "No hay conexión a Internet", Toast.LENGTH_LONG).show();
            return;
        }

        String accountType = rbOrganizer.isChecked() ? "ORGANIZER" : "RUNNER";

        registerUser(fullName, email, password, accountType);
    }

    private void registerUser(String fullName, String email, String password, String accountType) {
        btnRegister.setEnabled(false);
        btnRegister.setText("Registrando...");

        new Thread(() -> {
            try {
                boolean registered = registerAuth(email, password, accountType);

                if (!registered) {
                    runOnUiThread(() -> {
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Crear cuenta");
                    });
                    return;
                }

                JSONObject loginData = loginAuth(email, password);

                if (loginData == null) {
                    runOnUiThread(() -> {
                        btnRegister.setEnabled(true);
                        btnRegister.setText("Crear cuenta");
                        Toast.makeText(RegisterActivity.this, "Cuenta creada, pero no fue posible iniciar sesión", Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                String token = loginData.getString("token");
                JSONObject userJson = loginData.getJSONObject("user");
                String role = userJson.optString("account_type", accountType);

                boolean profileCreated = createBasicProfile(token, fullName);

                runOnUiThread(() -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Crear cuenta");

                    if (profileCreated) {
                        sessionManager.saveSession(token, email, role);

                        Toast.makeText(
                                RegisterActivity.this,
                                "Registro exitoso",
                                Toast.LENGTH_LONG
                        ).show();

                        Intent intent = new Intent(RegisterActivity.this, MainNavActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(
                                RegisterActivity.this,
                                "Cuenta creada, pero falta completar perfil",
                                Toast.LENGTH_LONG
                        ).show();

                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error de conexión durante el registro", e);

                runOnUiThread(() -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Crear cuenta");
                    Toast.makeText(RegisterActivity.this, "No fue posible completar el registro", Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private boolean registerAuth(String email, String password, String accountType) {
        HttpURLConnection conn = null;

        try {
            URL url = new URL(REGISTER_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);
            json.put("account_type", accountType);

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

            if (responseCode == 201) {
                return true;
            }

            JSONObject responseJson = new JSONObject(response);
            String message = responseJson.optString("message", "Error al registrar usuario");

            runOnUiThread(() ->
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show()
            );

            return false;

        } catch (Exception e) {
            Log.e(TAG, "Error de conexión durante la conexion con Auth", e);

            runOnUiThread(() ->
                    Toast.makeText(RegisterActivity.this, "No fue posible conectar con Auth", Toast.LENGTH_LONG).show()
            );
            return false;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private JSONObject loginAuth(String email, String password) {
        HttpURLConnection conn = null;

        try {
            URL url = new URL(LOGIN_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("password", password);

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

            if (responseCode == 200) {
                return new JSONObject(response);
            }

            return null;

        } catch (Exception e) {
            Log.e(TAG, "Error de conexión durante el login", e);
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private boolean createBasicProfile(String token, String fullName) {
        HttpURLConnection conn = null;

        try {
            String[] parts = fullName.trim().split("\\s+", 2);
            String firstName = parts[0];
            String lastName = parts.length > 1 ? parts[1] : "Usuario";

            String username = generateUsername(firstName, lastName);

            URL url = new URL(CREATE_PROFILE_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Authorization", "Bearer " + token);
            conn.setDoOutput(true);
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("first_name", firstName);
            json.put("last_name", lastName);

            OutputStream os = conn.getOutputStream();
            os.write(json.toString().getBytes("UTF-8"));
            os.close();

            int responseCode = conn.getResponseCode();

            return responseCode == 201 || responseCode == 200;

        } catch (Exception e) {
            Log.e(TAG, "Error de conexión durante la creación del perfil", e);
            return false;
        } finally {
            if (conn != null) conn.disconnect();
        }
    }

    private String generateUsername(String firstName, String lastName) {
        String cleanFirst = firstName.toLowerCase().replaceAll("[^a-z0-9]", "");
        String cleanLast = lastName.toLowerCase().replaceAll("[^a-z0-9]", "");

        int random = (int) (Math.random() * 9000) + 1000;

        return cleanFirst + cleanLast + random;
    }
}