package com.eventra.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnGoProfile, btnGoEvents, btnLogout;
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
        btnGoEvents = findViewById(R.id.btnGoEvents);
        btnLogout = findViewById(R.id.btnLogout);

        String email = sessionManager.getEmail();
        tvWelcome.setText("Bienvenido a Eventra\n" + email);



        btnGoEvents.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, EventsActivity.class);
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