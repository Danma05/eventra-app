package com.eventra.mobile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private ImageButton btnBack, btnTogglePassword;
    private EditText etName, etEmail, etPassword;
    private RadioGroup rgUserType;
    private RadioButton rbRunner, rbOrganizer;
    private Button btnRegister;
    private TextView tvTerms;

    private boolean isPasswordVisible = false;

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

        simulateRegister();
    }

    private void simulateRegister() {
        btnRegister.setEnabled(false);
        btnRegister.setText("Registrando...");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            String selectedRole = rbRunner.isChecked() ? "Corredor" : "Organizador";

            btnRegister.setEnabled(true);
            btnRegister.setText("Crear cuenta");

            Toast.makeText(
                    RegisterActivity.this,
                    "Registro válido para " + selectedRole + ". Pendiente integración con backend.",
                    Toast.LENGTH_LONG
            ).show();

        }, 1500);
    }
}