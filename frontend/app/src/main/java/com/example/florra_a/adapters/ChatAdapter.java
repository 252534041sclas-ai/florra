package com.example.florra_a.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.R;
import com.example.florra_a.models.ChatMessage;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessages.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ChatMessage.TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_ai, parent, false);
            return new AIViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).tvMessage.setText(message.getMessage());
            ((UserViewHolder) holder).tvTime.setText(message.getTime());
        } else {
            ((AIViewHolder) holder).tvMessage.setText(message.getMessage());
            ((AIViewHolder) holder).tvTime.setText(message.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }

    static class AIViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;

        AIViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
