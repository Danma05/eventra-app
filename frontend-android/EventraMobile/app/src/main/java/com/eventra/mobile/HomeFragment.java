package com.eventra.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private Button btnGoEvents, btnGoProfile, btnLogout;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_home, container, false);

        sessionManager = new SessionManager(requireContext());

        btnGoEvents = view.findViewById(R.id.btnGoEvents);
        btnGoProfile = view.findViewById(R.id.btnGoProfile);
        btnLogout = view.findViewById(R.id.btnLogout);

        btnGoEvents.setOnClickListener(v ->
                ((MainNavActivity) requireActivity()).goToEventsTab()
        );

        btnGoProfile.setOnClickListener(v ->
                ((MainNavActivity) requireActivity()).goToProfileTab()
        );

        btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            requireActivity().finish();
        });

        return view;
    }
}