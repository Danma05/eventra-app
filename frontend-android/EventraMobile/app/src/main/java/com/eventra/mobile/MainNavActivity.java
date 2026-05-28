package com.eventra.mobile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainNavActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);

        bottomNavigation = findViewById(R.id.bottomNavigation);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            }

            if (id == R.id.nav_events) {
                loadFragment(new EventsFragment());
                return true;
            }

            if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }

            if (id == R.id.nav_activity) {
                loadFragment(new ActivitySelectionFragment());
                return true;
            }

            if (id == R.id.nav_results) {
                loadFragment(new ResultsFragment());
                return true;
            }

            return false;
        });

        boolean openEvents = getIntent().getBooleanExtra("open_events", false);

        if (openEvents) {
            bottomNavigation.setSelectedItemId(R.id.nav_events);
        } else {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    public void goToEventsTab() {
        bottomNavigation.setSelectedItemId(R.id.nav_events);
    }

    public void goToProfileTab() {
        bottomNavigation.setSelectedItemId(R.id.nav_profile);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameContainer, fragment)
                .commit();
    }
}