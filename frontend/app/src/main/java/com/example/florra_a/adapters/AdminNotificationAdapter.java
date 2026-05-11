package com.example.florra_a.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.R;
import com.example.florra_a.models.AdminNotificationItem;
import java.util.List;

public class AdminNotificationAdapter extends RecyclerView.Adapter<AdminNotificationAdapter.ViewHolder> {

    private List<AdminNotificationItem> items;

    public AdminNotificationAdapter(List<AdminNotificationItem> items) {
        this.items = items;
    }

    public void updateList(List<AdminNotificationItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        AdminNotificationItem item = items.get(position);
        h.tvTitle.setText(item.getTitle());
        h.tvMessage.setText(item.getMessage());
        h.tvTime.setText(item.getTimestamp());
        h.tvType.setText(item.getType() != null ? item.getType().toUpperCase() : "SYSTEM");

        // Color-code the left bar by type
        String type = item.getType() != null ? item.getType().toLowerCase() : "system";
        int color;
        switch (type) {
            case "promotion": color = Color.parseColor("#F59E0B"); break;
            case "alert":     color = Color.parseColor("#EF4444"); break;
            case "announcement": color = Color.parseColor("#3B82F6"); break;
            default:          color = Color.parseColor("#014D4E"); break;
        }
        h.vTypeColor.setBackgroundColor(color);
        h.tvType.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime, tvType;
        View vTypeColor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvMessage = itemView.findViewById(R.id.tvNotifMessage);
            tvTime = itemView.findViewById(R.id.tvNotifTime);
            tvType = itemView.findViewById(R.id.tvNotifType);
            vTypeColor = itemView.findViewById(R.id.vTypeColor);
        }
    }
}
