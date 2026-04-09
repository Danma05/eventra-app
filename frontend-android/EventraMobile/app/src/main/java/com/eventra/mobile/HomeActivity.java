package com.eventra.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnGoProfile, btnLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            goToLogin();
            return;
        }

        tvWelcome = findViewById(R.id.tvWelcome);
        btnGoProfile = findViewById(R.id.btnGoProfile);
        btnLogout = findViewById(R.id.btnLogout);

        String email = sessionManager.getEmail();
        tvWelcome.setText("Bienvenido a Eventra\n" + email);

        btnGoProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            goToLogin();
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}