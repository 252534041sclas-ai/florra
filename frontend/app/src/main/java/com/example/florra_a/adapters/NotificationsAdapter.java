package com.example.florra_a.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.florra_a.R;
import com.example.florra_a.models.Notification;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private Context context;
    private List<Notification> notificationList;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationsAdapter(Context context, List<Notification> notificationList, OnNotificationClickListener listener) {
        this.context = context;
        this.notificationList = notificationList;
        this.listener = listener;
    }

    public void updateList(List<Notification> newList) {
        this.notificationList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        holder.tvTime.setText(notification.getTimestamp());
        
        // Handle Read/Unread state
        if (notification.isRead()) {
            holder.viewUnread.setVisibility(View.GONE);
            holder.cardView.setCardBackgroundColor(Color.parseColor("#F9FAFB"));
            holder.layoutIcon.setBackgroundColor(Color.WHITE);
        } else {
            holder.viewUnread.setVisibility(View.VISIBLE);
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.layoutIcon.setBackgroundColor(Color.parseColor("#F1F5F9"));
        }

        // Load tagged product image if present, otherwise default to icon
        if (notification.getProductImage() != null && !notification.getProductImage().isEmpty()) {
            holder.cardProductImage.setVisibility(View.VISIBLE);
            holder.layoutIcon.setVisibility(View.GONE);

            String imageUrl = notification.getProductImage();
            if (!imageUrl.startsWith("http")) {
                String baseUrl = com.example.florra_a.network.RetrofitClient.BASE_URL;
                if (baseUrl.endsWith("/") && imageUrl.startsWith("/")) {
                    imageUrl = baseUrl + imageUrl.substring(1);
                } else if (!baseUrl.endsWith("/") && !imageUrl.startsWith("/")) {
                    imageUrl = baseUrl + "/" + imageUrl;
                } else {
                    imageUrl = baseUrl + imageUrl;
                }
            }

            com.bumptech.glide.Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_grid_view)
                .error(R.drawable.ic_grid_view)
                .into(holder.imgProduct);
        } else {
            holder.cardProductImage.setVisibility(View.GONE);
            holder.layoutIcon.setVisibility(View.VISIBLE);

            if ("QUOTATION".equalsIgnoreCase(notification.getType())) {
                holder.imgIcon.setImageResource(R.drawable.ic_description); 
            } else if ("SYSTEM".equalsIgnoreCase(notification.getType())) {
                holder.imgIcon.setImageResource(R.drawable.ic_info);
            } else {
                holder.imgIcon.setImageResource(R.drawable.ic_notifications); // Default
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        View layoutIcon;
        ImageView imgIcon;
        CardView cardProductImage;
        ImageView imgProduct;
        TextView tvTitle, tvTime, tvMessage;
        View viewUnread;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardNotification);
            layoutIcon = itemView.findViewById(R.id.layoutIcon);
            imgIcon = itemView.findViewById(R.id.imgIcon);
            cardProductImage = itemView.findViewById(R.id.cardProductImage);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            viewUnread = itemView.findViewById(R.id.viewUnread);
        }
    }
}
