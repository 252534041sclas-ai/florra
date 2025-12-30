package com.example.florra_a;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class EnquiryAdapter extends RecyclerView.Adapter<EnquiryAdapter.EnquiryViewHolder> {

    private List<EnquiriesActivity.EnquiryItem> enquiryList;
    private List<EnquiriesActivity.EnquiryItem> filteredList;

    public EnquiryAdapter(List<EnquiriesActivity.EnquiryItem> enquiryList) {
        this.enquiryList = enquiryList;
        this.filteredList = new ArrayList<>(enquiryList);
    }

    @NonNull
    @Override
    public EnquiryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enquiry, parent, false);
        return new EnquiryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EnquiryViewHolder holder, int position) {
        EnquiriesActivity.EnquiryItem item = filteredList.get(position);

        holder.tvCustomerName.setText(item.getCustomerName());
        holder.tvEnquiryText.setText(item.getEnquiryText());
        holder.tvTimeAgo.setText(item.getTimeAgo());
        holder.tvStatus.setText(item.getStatus());
        holder.tvDetailInfo.setText(item.getDetailInfo());
        holder.tvAction.setText(item.getActionText());

        // Set image
        holder.ivTileImage.setImageResource(item.getImageResource());

        // Set icons based on action type
        if (item.getDetailInfo().contains("+1") || item.getDetailInfo().contains("$")) {
            holder.ivDetailIcon.setImageResource(R.drawable.ic_phone);
        } else if (item.getDetailInfo().contains("Order")) {
            holder.ivDetailIcon.setImageResource(R.drawable.ic_inventory);
        } else if (item.getDetailInfo().contains("Fri") || item.getDetailInfo().contains("AM") || item.getDetailInfo().contains("PM")) {
            holder.ivDetailIcon.setImageResource(R.drawable.ic_calendar);
        }

        // Set action button click listener
        holder.tvAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action = item.getActionText();
                Toast.makeText(v.getContext(), action + " clicked for " + item.getCustomerName(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set status background based on status
        switch (item.getStatus()) {
            case "New":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_new);
                break;
            case "Quoted":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_quoted);
                break;
            case "Site Visit":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_site_visit);
                break;
            case "Resolved":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_resolved);
                holder.cardView.setAlpha(0.75f);
                break;
            default:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_default);
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void filterEnquiries(String filterType) {
        filteredList.clear();

        if (filterType.equals("all")) {
            filteredList.addAll(enquiryList);
        } else {
            for (EnquiriesActivity.EnquiryItem item : enquiryList) {
                if (filterType.equals("new") && item.getStatus().equals("New")) {
                    filteredList.add(item);
                } else if (filterType.equals("quoted") && item.getStatus().equals("Quoted")) {
                    filteredList.add(item);
                } else if (filterType.equals("followup") && item.getStatus().equals("Site Visit")) {
                    filteredList.add(item);
                } else if (filterType.equals("resolved") && item.getStatus().equals("Resolved")) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class EnquiryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName, tvEnquiryText, tvTimeAgo, tvStatus, tvDetailInfo, tvAction;
        ImageView ivTileImage, ivDetailIcon;
        androidx.cardview.widget.CardView cardView;

        public EnquiryViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardEnquiry);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvEnquiryText = itemView.findViewById(R.id.tvEnquiryText);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDetailInfo = itemView.findViewById(R.id.tvDetailInfo);
            tvAction = itemView.findViewById(R.id.tvAction);
            ivTileImage = itemView.findViewById(R.id.ivTileImage);
            ivDetailIcon = itemView.findViewById(R.id.ivDetailIcon);
        }
    }
}