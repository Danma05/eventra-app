package com.eventra.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ProfileFragment extends Fragment {

    private TextView tvFullName, tvUsername, tvLocation, tvProfileStatus;
    private TextView tvEventsRegistered, tvEventsOrganized, tvEventsCompleted;
    private ImageButton btnProfileSettings;
    private Button btnOrganizerFloating;

    private SessionManager sessionManager;

    private static final String PROFILE_URL =
            "http://172.20.10.11:3002/users/profile/me";

    private static final String MY_REGISTRATIONS_URL =
            "http://172.20.10.11:3004/registrations/my";

    private static final String ORGANIZER_EVENTS_URL =
            "http://172.20.10.11:3003/events/organizer/my";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_profile, container, false);

        sessionManager = new SessionManager(requireContext());

        tvFullName = view.findViewById(R.id.tvFullName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvLocation = view.findViewById(R.id.tvLocation);
        tvProfileStatus = view.findViewById(R.id.tvProfileStatus);

        tvEventsRegistered = view.findViewById(R.id.tvEventsRegistered);
        tvEventsOrganized = view.findViewById(R.id.tvEventsOrganized);
        tvEventsCompleted = view.findViewById(R.id.tvEventsCompleted);

        btnProfileSettings = view.findViewById(R.id.btnProfileSettings);
        btnOrganizerFloating = view.findViewById(R.id.btnOrganizerFloating);

        btnProfileSettings.setOnClickListener(v -> showProfileMenu());

        btnOrganizerFloating.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), OrganizerPanelActivity.class);
            startActivity(intent);
        });

        loadProfile();
        validateOrganizerButton();
        loadProfileStats();

        return view;
    }

    private void showProfileMenu() {
        PopupMenu menu = new PopupMenu(requireContext(), btnProfileSettings);

        menu.getMenu().add("Editar perfil");
        menu.getMenu().add("Cerrar sesión");

        menu.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();

            if (title.equals("Editar perfil")) {
                Intent intent = new Intent(requireContext(), EditProfileActivity.class);
                startActivity(intent);
                return true;
            }

            if (title.equals("Cerrar sesión")) {
                sessionManager.clearSession();

                Intent intent = new Intent(requireContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            }

            return false;
        });

        menu.show();
    }

    private void validateOrganizerButton() {
        String role = sessionManager.getRole();

        if (role != null && role.equalsIgnoreCase("ORGANIZER")) {
            btnOrganizerFloating.setVisibility(View.VISIBLE);
        } else {
            btnOrganizerFloating.setVisibility(View.GONE);
        }
    }

    private void loadProfile() {
        String token = sessionManager.getToken();

        if (token == null || token.trim().isEmpty()) {
            Toast.makeText(requireContext(), "Sesión inválida", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;

            try {
                URL url = new URL(PROFILE_URL);
                conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", "Bearer " + token);

                int responseCode = conn.getResponseCode();

                InputStream is = responseCode == 200
                        ? conn.getInputStream()
                        : conn.getErrorStream();

                Scanner scanner = new Scanner(is).useDelimiter("\\A");
                String response = scanner.hasNext() ? scanner.next() : "";
                scanner.close();

                requireActivity().runOnUiThread(() -> {
                    try {
                        if (responseCode == 200) {
                            JSONObject profile = new JSONObject(response);

                            String firstName = profile.optString("first_name", "");
                            String lastName = profile.optString("last_name", "");
                            String username = profile.optString("username", "");
                            String city = profile.optString("city", "");
                            String country = profile.optString("country", "");
                            String status = profile.optString("profile_status", "INCOMPLETE");

                            tvFullName.setText(firstName + " " + lastName);
                            tvUsername.setText("@" + username);

                            if (!city.isEmpty() || !country.isEmpty()) {
                                tvLocation.setText(city + ", " + country);
                            } else {
                                tvLocation.setText("Ubicación no registrada");
                            }

                            if ("ACTIVE".equals(status)) {
                                tvProfileStatus.setText("Perfil completo");
                            } else {
                                tvProfileStatus.setText("Debes completar tu perfil");
                            }

                        } else {
                            Toast.makeText(requireContext(), "No fue posible cargar perfil", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Error procesando perfil", Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "No fue posible conectar con backend-user", Toast.LENGTH_LONG).show()
                );
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void loadProfileStats() {
        String token = sessionManager.getToken();

        if (token == null || token.trim().isEmpty()) {
            return;
        }

        new Thread(() -> {
            int registeredCount = getArrayCount(MY_REGISTRATIONS_URL, token);
            int organizedCount = getArrayCount(ORGANIZER_EVENTS_URL, token);
            int completedCount = getCompletedEventsCount(ORGANIZER_EVENTS_URL, token);

            requireActivity().runOnUiThread(() -> {
                tvEventsRegistered.setText(registeredCount + "\nInscritos");
                tvEventsOrganized.setText(organizedCount + "\nOrganizados");
                tvEventsCompleted.setText(completedCount + "\nCompletados");
            });
        }).start();
    }

    private int getArrayCount(String urlString, String token) {
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlString);
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

            if (responseCode == 200) {
                JSONArray array = new JSONArray(response);
                return array.length();
            }

        } catch (Exception ignored) {
        } finally {
            if (conn != null) conn.disconnect();
        }

        return 0;
    }

    private int getCompletedEventsCount(String urlString, String token) {
        HttpURLConnection conn = null;

        try {
            URL url = new URL(urlString);
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

            if (responseCode == 200) {
                JSONArray array = new JSONArray(response);
                int count = 0;

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);

                    if ("FINISHED".equalsIgnoreCase(obj.optString("status"))) {
                        count++;
                    }
                }

                return count;
            }

        } catch (Exception ignored) {
        } finally {
            if (conn != null) conn.disconnect();
        }

        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
        validateOrganizerButton();
        loadProfileStats();
    }
}