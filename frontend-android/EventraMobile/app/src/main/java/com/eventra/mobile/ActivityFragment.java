package com.eventra.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ActivityFragment extends Fragment {

    private TextView tvActivityTitle;
    private TextView tvDistance;
    private TextView tvTime;
    private TextView tvPace;
    private TextView tvCalories;
    private TextView tvSpeed;
    private Button btnStartActivity;

    private long eventId = 0;
    private String eventTitle = "Actividad deportiva";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        getArgumentsData();
        initViews(view);
        setupInitialData();
        setupEvents();

        return view;
    }

    private void getArgumentsData() {
        Bundle bundle = getArguments();

        if (bundle != null) {
            eventId = bundle.getLong("event_id", 0);
            eventTitle = bundle.getString("event_title", "Actividad deportiva");
        }
    }

    private void initViews(View view) {
        tvActivityTitle = view.findViewById(R.id.tvActivityTitle);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvTime = view.findViewById(R.id.tvTime);
        tvPace = view.findViewById(R.id.tvPace);
        tvCalories = view.findViewById(R.id.tvCalories);
        tvSpeed = view.findViewById(R.id.tvSpeed);
        btnStartActivity = view.findViewById(R.id.btnStartActivity);
    }

    private void setupInitialData() {
        tvActivityTitle.setText(eventTitle);

        tvDistance.setText("0.00");
        tvTime.setText("00:00:00");
        tvPace.setText("Ritmo\n\n0.00\nmin/km");
        tvCalories.setText("Calorías\n\n0\nkcal");
        tvSpeed.setText("Vel. Prom.\n\n0.0\nkm/h");
    }

    private void setupEvents() {
        btnStartActivity.setOnClickListener(v -> {
            if (eventId == 0) {
                Toast.makeText(requireContext(), "Evento no válido", Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(
                    requireContext(),
                    "Actividad lista para iniciar en: " + eventTitle,
                    Toast.LENGTH_LONG
            ).show();
        });
    }
}