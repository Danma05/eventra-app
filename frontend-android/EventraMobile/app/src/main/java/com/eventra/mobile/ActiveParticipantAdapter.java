package com.eventra.mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ActiveParticipantAdapter extends RecyclerView.Adapter<ActiveParticipantAdapter.ViewHolder> {

    private final List<ActiveParticipant> participants;

    public ActiveParticipantAdapter(List<ActiveParticipant> participants) {
        this.participants = participants;
    }

    @NonNull
    @Override
    public ActiveParticipantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active_participant, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveParticipantAdapter.ViewHolder holder, int position) {
        ActiveParticipant participant = participants.get(position);

        holder.tvPosition.setText("#" + participant.getCurrentPosition());
        holder.tvName.setText("Corredor #" + participant.getAuthUserId());

        holder.tvLocation.setText(
                String.format("%.5f, %.5f",
                        participant.getLatitude(),
                        participant.getLongitude()
                )
        );

        holder.tvSpeed.setText(String.format("%.1f km/h", participant.getSpeedKmh()));
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPosition, tvName, tvLocation, tvSpeed;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPosition = itemView.findViewById(R.id.tvParticipantPosition);
            tvName = itemView.findViewById(R.id.tvParticipantName);
            tvLocation = itemView.findViewById(R.id.tvParticipantLocation);
            tvSpeed = itemView.findViewById(R.id.tvParticipantSpeed);
        }
    }
}