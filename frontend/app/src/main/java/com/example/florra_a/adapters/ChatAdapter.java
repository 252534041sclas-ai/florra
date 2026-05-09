package com.example.florra_a.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.florra_a.R;
import com.example.florra_a.models.ChatMessage;

import java.io.File;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;
    private static final int VIEW_TYPE_TYPING = 3;

    private List<ChatMessage> messageList;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageList.get(position);
        if (message.isTyping()) return VIEW_TYPE_TYPING;
        return message.isUser() ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        } else if (viewType == VIEW_TYPE_TYPING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_typing, parent, false);
            return new TypingViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_assistant, parent, false);
            return new BotViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind(message);
        } else if (holder instanceof BotViewHolder) {
            ((BotViewHolder) holder).bind(message);
        }
        // TypingViewHolder doesn't need binding
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        ImageView ivMessageImage;
        View cardMessageImage;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            ivMessageImage = itemView.findViewById(R.id.ivMessageImage);
            cardMessageImage = itemView.findViewById(R.id.cardMessageImage);
        }

        void bind(ChatMessage message) {
            tvMessage.setText(message.getMessage());
            if (message.getImagePath() != null) {
                cardMessageImage.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(new File(message.getImagePath()))
                        .into(ivMessageImage);
            } else {
                cardMessageImage.setVisibility(View.GONE);
            }
        }
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;
        RecyclerView rvProducts;

        BotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            rvProducts = itemView.findViewById(R.id.rvProducts);
        }

        void bind(ChatMessage message) {
            tvMessage.setText(message.getMessage());
            
            // Format time
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault());
            tvTime.setText(sdf.format(new java.util.Date(message.getTimestamp())));

            if (message.getProducts() != null && !message.getProducts().isEmpty()) {
                rvProducts.setVisibility(View.VISIBLE);
                rvProducts.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(
                        itemView.getContext(), RecyclerView.HORIZONTAL, false));
                rvProducts.setAdapter(new ChatProductAdapter(message.getProducts()));
            } else {
                rvProducts.setVisibility(View.GONE);
            }
        }
    }

    static class TypingViewHolder extends RecyclerView.ViewHolder {
        TypingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
