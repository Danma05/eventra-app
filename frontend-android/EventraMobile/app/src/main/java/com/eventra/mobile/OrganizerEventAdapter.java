package com.eventra.mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class OrganizerEventAdapter extends RecyclerView.Adapter<OrganizerEventAdapter.ViewHolder> {

    public interface OnOrganizerEventClickListener {
        void onView(Event event);
        void onEdit(Event event);
        void onDelete(Event event);
        void onViewParticipants(Event event);
        void onPublishResults(Event event);
        void onRaceControl(Event event);
        void onPublishRanking(Event event);
    }

    private final List<Event> events;
    private final HashMap<Long, Integer> registrationCounts;
    private final OnOrganizerEventClickListener listener;
    private boolean showingActive = true;

    public OrganizerEventAdapter(
            List<Event> events,
            HashMap<Long, Integer> registrationCounts,
            OnOrganizerEventClickListener listener
    ) {
        this.events = events;
        this.registrationCounts = registrationCounts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrganizerEventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_organizer_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizerEventAdapter.ViewHolder holder, int position) {
        Event event = events.get(position);

        int registered = registrationCounts.getOrDefault(event.getId(), 0);
        int capacity = event.getCapacity();

        holder.tvTitle.setText(event.getTitle());
        holder.tvDate.setText(event.getEventDate());
        String raceStatus = event.getRaceStatus() != null ? event.getRaceStatus() : "CREATED";
        holder.tvStatus.setText(raceStatus);

        holder.tvCapacity.setText(registered + " / " + capacity);

        int progress = 0;
        if (capacity > 0) {
            progress = (registered * 100) / capacity;
        }

        holder.progressCapacity.setProgress(progress);

        holder.btnView.setOnClickListener(v -> listener.onView(event));
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(event));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(event));
        holder.btnViewParticipants.setOnClickListener(v -> listener.onViewParticipants(event));

        holder.btnPublishResults.setAlpha(1f);

        if (showingActive) {

            holder.btnPublishResults.setText("Control Carrera");
            holder.btnPublishResults.setEnabled(true);
            holder.btnPublishResults.setAlpha(1f);
            holder.btnPublishResults.setOnClickListener(v -> listener.onRaceControl(event));

        } else {

            if (event.isResultsPublished()) {

                holder.btnPublishResults.setText("Ranking Publicado");
                holder.btnPublishResults.setEnabled(false);
                holder.btnPublishResults.setAlpha(0.6f);
                holder.btnPublishResults.setOnClickListener(null);

            } else {

                holder.btnPublishResults.setText("Publicar Ranking");
                holder.btnPublishResults.setEnabled(true);
                holder.btnPublishResults.setAlpha(1f);
                holder.btnPublishResults.setOnClickListener(v -> listener.onPublishRanking(event));
            }
        }
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDate, tvStatus, tvCapacity;
        ImageButton btnView, btnEdit, btnDelete;
        Button btnViewParticipants, btnPublishResults;
        ProgressBar progressCapacity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvOrganizerEventTitle);
            tvDate = itemView.findViewById(R.id.tvOrganizerEventDate);
            tvStatus = itemView.findViewById(R.id.tvOrganizerEventStatus);
            tvCapacity = itemView.findViewById(R.id.tvOrganizerEventCapacity);

            btnView = itemView.findViewById(R.id.btnViewEvent);
            btnEdit = itemView.findViewById(R.id.btnEditEvent);
            btnDelete = itemView.findViewById(R.id.btnDeleteEvent);
            btnViewParticipants = itemView.findViewById(R.id.btnViewParticipants);
            btnPublishResults = itemView.findViewById(R.id.btnPublishResults);

            progressCapacity = itemView.findViewById(R.id.progressCapacity);
        }
    }

    public void setShowingActive(boolean showingActive) {
        this.showingActive = showingActive;
        notifyDataSetChanged();
    }
}