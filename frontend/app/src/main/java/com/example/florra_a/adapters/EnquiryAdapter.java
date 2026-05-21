package com.example.florra_a.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.R;
import com.example.florra_a.models.Enquiry;
import java.util.List;

public class EnquiryAdapter extends RecyclerView.Adapter<EnquiryAdapter.EnquiryViewHolder> {

    private List<Enquiry> enquiries;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Enquiry enquiry);
        void onRejectClick(Enquiry enquiry);
    }

    public EnquiryAdapter(List<Enquiry> enquiries, OnItemClickListener listener) {
        this.enquiries = enquiries;
        this.listener = listener;
    }

    public void updateData(List<Enquiry> newEnquiries) {
        this.enquiries = newEnquiries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EnquiryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enquiry, parent, false);
        return new EnquiryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnquiryViewHolder holder, int position) {
        Enquiry enquiry = enquiries.get(position);
        holder.bind(enquiry, listener);
    }

    @Override
    public int getItemCount() {
        return enquiries != null ? enquiries.size() : 0;
    }

    static class EnquiryViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus, tvTimeAgo, tvCustomerName, tvEnquiryText, tvDetailInfo, tvAction, tvRejectAction;
        ImageView ivTileImage;

        public EnquiryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvEnquiryText = itemView.findViewById(R.id.tvEnquiryText);
            tvDetailInfo = itemView.findViewById(R.id.tvDetailInfo);
            tvAction = itemView.findViewById(R.id.tvAction);
            tvRejectAction = itemView.findViewById(R.id.tvRejectAction);
            ivTileImage = itemView.findViewById(R.id.ivTileImage);
        }

        public void bind(final Enquiry enquiry, final OnItemClickListener listener) {
            tvStatus.setText(capitalize(enquiry.getStatus()));
            tvTimeAgo.setText(formatDate(enquiry.getCreatedAt()));
            String formattedId = String.format("#%04d", enquiry.getId());
            // Extract first name from full name
            String firstName = "";
            if (enquiry.getCustomerName() != null && !enquiry.getCustomerName().isEmpty()) {
                String[] parts = enquiry.getCustomerName().split(" ");
                firstName = parts.length > 0 ? parts[0] : enquiry.getCustomerName();
            }
            tvCustomerName.setText(firstName + " - " + formattedId);
            // Hide placeholder 'New Quotation Request' text
            String message = enquiry.getMessage();
            if (message != null && message.trim().equalsIgnoreCase("New Quotation Request")) {
                tvEnquiryText.setText("");
            } else {
                tvEnquiryText.setText(message);
            }

            // Show phone number (mobile) and make it clickable
            String phone = enquiry.getPhone() != null && !enquiry.getPhone().isEmpty() ? enquiry.getPhone() : "-";
            tvDetailInfo.setText(phone);
            if (!phone.equals("-")) {
                tvDetailInfo.setOnClickListener(v -> {
                    android.content.Intent dialIntent = new android.content.Intent(android.content.Intent.ACTION_DIAL);
                    dialIntent.setData(android.net.Uri.parse("tel:" + phone));
                    v.getContext().startActivity(dialIntent);
                });
            } else {
                tvDetailInfo.setOnClickListener(null);
            }
            // Hide image as it's not in the model
            if (ivTileImage != null) {
                ivTileImage.setVisibility(View.GONE);
            }

            // Status Colors and Actions visibility
            int bgRes = R.drawable.bg_status_new; // Default
            boolean isNew = false;
            switch (enquiry.getStatus().toLowerCase()) {
                case "new": 
                    bgRes = R.drawable.bg_status_new; 
                    isNew = true;
                    break;
                case "quoted": bgRes = R.drawable.bg_status_quoted; break;
                case "site_visit": 
                case "site visit": bgRes = R.drawable.bg_status_site_visit; break;
                case "resolved": bgRes = R.drawable.bg_status_resolved; break;
                case "rejected": bgRes = R.drawable.bg_status_rejected; break;
            }
            tvStatus.setBackgroundResource(bgRes);

            // Show reject button only for new enquiries
            if (tvRejectAction != null) {
                if (isNew) {
                    tvRejectAction.setVisibility(View.VISIBLE);
                    tvRejectAction.setOnClickListener(v -> listener.onRejectClick(enquiry));
                } else {
                    tvRejectAction.setVisibility(View.GONE);
                }
            }

            itemView.setOnClickListener(v -> listener.onItemClick(enquiry));
            
            // Also handle specific action button click
            if (tvAction != null) {
                tvAction.setOnClickListener(v -> listener.onItemClick(enquiry));
            }
        }

        private String capitalize(String str) {
            if (str == null || str.isEmpty()) return str;
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }

        private String formatDate(String dateString) {
             // Simple basic logic for now, ideally use SimpleDateFormat or relative time
             if (dateString == null) return "Just now";
             if (dateString.length() > 10) return dateString.substring(0, 10);
             return dateString;
        }
    }
}
