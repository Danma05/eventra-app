package com.eventra.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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

    private ImageButton btnBack, btnTogglePassword;
    private EditText etName, etEmail, etPassword;
    private RadioGroup rgUserType;
    private RadioButton rbRunner, rbOrganizer;
    private Button btnRegister;
    private TextView tvTerms;

    private boolean isPasswordVisible = false;

    // IP local real de tu computador
    private static final String REGISTER_URL = "http://172.20.10.11:3001/auth/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        registerUser(email, password);
    }

    private void registerUser(String email, String password) {
        btnRegister.setEnabled(false);
        btnRegister.setText("Registrando...");

        new Thread(() -> {
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
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Crear cuenta");

                    try {
                        JSONObject responseJson = new JSONObject(response);

                        if (responseCode == 201) {
                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Registro exitoso. Ahora puedes iniciar sesión.",
                                    Toast.LENGTH_LONG
                            ).show();

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            String message = responseJson.optString("message", "Error al registrar usuario");
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(RegisterActivity.this, "Respuesta inválida del servidor", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    btnRegister.setEnabled(true);
                    btnRegister.setText("Crear cuenta");
                    Toast.makeText(RegisterActivity.this, "No fue posible conectar con el backend", Toast.LENGTH_LONG).show();
                });
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }).start();
    }
}