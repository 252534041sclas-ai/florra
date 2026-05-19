package com.example.florra_a.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.R;
import com.example.florra_a.models.Enquiry;
import java.util.List;

public class QuotationRequestAdapter extends RecyclerView.Adapter<QuotationRequestAdapter.ViewHolder> {

    public interface OnRespondClickListener {
        void onRespond(Enquiry enquiry);
    }

    private List<Enquiry> items;
    private final OnRespondClickListener listener;

    public QuotationRequestAdapter(List<Enquiry> items, OnRespondClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateList(List<Enquiry> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quotation_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Enquiry e = items.get(position);

        String name = e.getCustomerName() != null ? e.getCustomerName() : "?";

        // Avatar circle fallback / dynamic image loading
        if (e.getCustomerImage() != null && !e.getCustomerImage().isEmpty()) {
            String fullUrl = e.getCustomerImage();
            if (!fullUrl.startsWith("http")) {
                String baseUrl = com.example.florra_a.network.RetrofitClient.BASE_URL;
                if (baseUrl.endsWith("/") && fullUrl.startsWith("/")) fullUrl = baseUrl + fullUrl.substring(1);
                else if (!baseUrl.endsWith("/") && !fullUrl.startsWith("/")) fullUrl = baseUrl + "/" + fullUrl;
                else fullUrl = baseUrl + fullUrl;
            }
            h.ivCustomerProfile.setVisibility(View.VISIBLE);
            h.tvAvatar.setVisibility(View.GONE);
            com.bumptech.glide.Glide.with(h.itemView.getContext())
                .load(fullUrl)
                .circleCrop()
                .into(h.ivCustomerProfile);
        } else {
            h.ivCustomerProfile.setVisibility(View.GONE);
            h.tvAvatar.setVisibility(View.VISIBLE);
            h.tvAvatar.setText(name.length() > 0 ? String.valueOf(name.charAt(0)).toUpperCase() : "?");
        }

        h.tvCustomerName.setText(name);
        h.tvPhone.setText(e.getPhone() != null ? e.getPhone() : "");
        h.tvMessage.setText(e.getMessage() != null ? e.getMessage() : "");

        // Date formatting — strip time if present
        String date = e.getCreatedAt() != null ? e.getCreatedAt() : "";
        if (date.contains("T")) date = date.split("T")[0];
        h.tvDate.setText(date);

        // Status badge styling
        String status = e.getStatus() != null ? e.getStatus().toLowerCase() : "new";
        h.tvStatus.setText(status.toUpperCase().replace("_", " "));
        switch (status) {
            case "new":
                h.tvStatus.setBackgroundResource(R.drawable.bg_tab_selected); // red-ish
                h.tvStatus.setTextColor(Color.WHITE);
                break;
            case "quoted":
                h.tvStatus.setBackgroundColor(Color.parseColor("#F59E0B"));
                h.tvStatus.setTextColor(Color.WHITE);
                break;
            case "resolved":
                h.tvStatus.setBackgroundColor(Color.parseColor("#22C55E"));
                h.tvStatus.setTextColor(Color.WHITE);
                break;
            case "follow_up":
                h.tvStatus.setBackgroundColor(Color.parseColor("#3B82F6"));
                h.tvStatus.setTextColor(Color.WHITE);
                break;
            default:
                h.tvStatus.setBackgroundColor(Color.parseColor("#94A3B8"));
                h.tvStatus.setTextColor(Color.WHITE);
                break;
        }

        // Respond click — whole card + button
        View.OnClickListener respondClick = v -> {
            if (listener != null) listener.onRespond(e);
        };
        h.itemView.setOnClickListener(respondClick);
        h.tvRespond.setOnClickListener(respondClick);
    }

    @Override
    public int getItemCount() { return items != null ? items.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatar, tvCustomerName, tvPhone, tvMessage, tvDate, tvStatus, tvRespond;
        android.widget.ImageView ivCustomerProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar       = itemView.findViewById(R.id.tvAvatar);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvPhone        = itemView.findViewById(R.id.tvPhone);
            tvMessage      = itemView.findViewById(R.id.tvRequestMessage);
            tvDate         = itemView.findViewById(R.id.tvRequestDate);
            tvStatus       = itemView.findViewById(R.id.tvStatus);
            tvRespond      = itemView.findViewById(R.id.tvRespond);
            ivCustomerProfile = itemView.findViewById(R.id.ivCustomerProfile);
        }
    }
}
