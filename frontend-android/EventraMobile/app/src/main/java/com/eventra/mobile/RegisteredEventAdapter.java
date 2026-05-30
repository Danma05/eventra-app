package com.eventra.mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RegisteredEventAdapter extends RecyclerView.Adapter<RegisteredEventAdapter.ViewHolder> {

    public interface OnRegisteredEventClickListener {
        void onSelect(RegisteredEvent event);
    }

    private final List<RegisteredEvent> registeredEvents;
    private final OnRegisteredEventClickListener listener;

    public RegisteredEventAdapter(List<RegisteredEvent> registeredEvents, OnRegisteredEventClickListener listener) {
        this.registeredEvents = registeredEvents;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RegisteredEventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_registered_event, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegisteredEventAdapter.ViewHolder holder, int position) {
        RegisteredEvent event = registeredEvents.get(position);

        holder.tvTitle.setText(event.getTitle());
        holder.tvDate.setText(event.getEventDate());
        holder.tvLocation.setText(event.getLocation());

        holder.btnSelect.setOnClickListener(v -> listener.onSelect(event));
    }

    @Override
    public int getItemCount() {
        return registeredEvents.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDate, tvLocation;
        Button btnSelect;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvRegisteredEventTitle);
            tvDate = itemView.findViewById(R.id.tvRegisteredEventDate);
            tvLocation = itemView.findViewById(R.id.tvRegisteredEventLocation);
            btnSelect = itemView.findViewById(R.id.btnStartRegisteredEvent);
        }
    }
}