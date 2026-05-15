package com.eventra.mobile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private Button btnOrganizerPanel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_profile, container, false);

        btnOrganizerPanel = view.findViewById(R.id.btnOrganizerPanel);

        btnOrganizerPanel.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), OrganizerPanelActivity.class);
            startActivity(intent);
        });

        return view;
    }
}