package com.eventra.mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    private final List<Event> eventList;
    private final OnEventClickListener listener;

    public EventAdapter(List<Event> eventList, OnEventClickListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.tvTitle.setText(event.getTitle());
        holder.tvLocation.setText(event.getLocation());
        holder.tvDate.setText(event.getEventDate());
        holder.tvCapacity.setText("Capacidad: " + event.getCapacity());
        holder.tvStatus.setText("Estado: " + event.getStatus());

        ImageLoader.loadImage(event.getImageUrl(), holder.ivEventImage);

        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        ImageView ivEventImage;
        TextView tvTitle, tvLocation, tvDate, tvCapacity, tvStatus;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvCapacity = itemView.findViewById(R.id.tvEventCapacity);
            tvStatus = itemView.findViewById(R.id.tvEventStatus);
        }
    }
}