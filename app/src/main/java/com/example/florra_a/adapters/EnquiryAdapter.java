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
        TextView tvStatus, tvTimeAgo, tvCustomerName, tvEnquiryText, tvDetailInfo, tvAction;
        ImageView ivTileImage;

        public EnquiryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvEnquiryText = itemView.findViewById(R.id.tvEnquiryText);
            tvDetailInfo = itemView.findViewById(R.id.tvDetailInfo);
            tvAction = itemView.findViewById(R.id.tvAction);
            ivTileImage = itemView.findViewById(R.id.ivTileImage);
        }

        public void bind(final Enquiry enquiry, final OnItemClickListener listener) {
            tvStatus.setText(capitalize(enquiry.getStatus()));
            tvTimeAgo.setText(formatDate(enquiry.getCreatedAt()));
            tvCustomerName.setText(enquiry.getCustomerName());
            tvEnquiryText.setText(enquiry.getMessage());

            // Reference or Phone as detail
            String detail = enquiry.getReference() != null && !enquiry.getReference().isEmpty() 
                            ? enquiry.getReference() : enquiry.getPhone();
            tvDetailInfo.setText(detail);

            // Hide image as it's not in the model
            if (ivTileImage != null) {
                ivTileImage.setVisibility(View.GONE);
            }

            // Status Colors
            int bgRes = R.drawable.bg_status_new; // Default
            switch (enquiry.getStatus().toLowerCase()) {
                case "new": bgRes = R.drawable.bg_status_new; break;
                case "quoted": bgRes = R.drawable.bg_status_quoted; break;
                case "site_visit": 
                case "site visit": bgRes = R.drawable.bg_status_site_visit; break;
                case "resolved": bgRes = R.drawable.bg_status_resolved; break;
            }
            tvStatus.setBackgroundResource(bgRes);

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
