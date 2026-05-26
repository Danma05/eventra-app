package com.eventra.mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private final List<Result> results;

    public ResultAdapter(List<Result> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public ResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultAdapter.ViewHolder holder, int position) {
        Result result = results.get(position);

        if (result.getPosition() == 1) {
            holder.tvPosition.setText("🥇");
        } else if (result.getPosition() == 2) {
            holder.tvPosition.setText("🥈");
        } else if (result.getPosition() == 3) {
            holder.tvPosition.setText("🥉");
        } else {
            holder.tvPosition.setText(String.valueOf(result.getPosition()));
        }

        holder.tvRunnerName.setText(result.getRunnerName());
        holder.tvCategory.setText(result.getCategory());
        holder.tvTime.setText(formatTime(result.getTotalTimeSeconds()));
        holder.tvPace.setText(formatPace(result.getPaceSecondsPerKm()));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    private String formatTime(int totalSeconds) {
        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;
        return String.format("%d:%02d:%02d", h, m, s);
    }

    private String formatPace(int paceSeconds) {
        int m = paceSeconds / 60;
        int s = paceSeconds % 60;
        return String.format("%d:%02d min/km", m, s);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPosition, tvRunnerName, tvCategory, tvTime, tvPace;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvRunnerName = itemView.findViewById(R.id.tvRunnerName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvPace = itemView.findViewById(R.id.tvPace);
        }
    }
}