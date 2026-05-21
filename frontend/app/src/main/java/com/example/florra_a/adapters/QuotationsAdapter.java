package com.example.florra_a.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.R;
import com.example.florra_a.models.Enquiry;
import java.util.List;

public class QuotationsAdapter extends RecyclerView.Adapter<QuotationsAdapter.ViewHolder> {

    private Context context;
    private List<Enquiry> enquiryList;

    public QuotationsAdapter(Context context, List<Enquiry> enquiryList) {
        this.context = context;
        this.enquiryList = enquiryList;
    }

    public void updateData(List<Enquiry> newEnquiryList) {
        this.enquiryList = newEnquiryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We'll inflate a simple item view layout. 
        // Since we don't have a separate item xml, we can create one or reuse logic.
        // Ideally we should create 'item_quotation.xml', but for now I'll use a new file creation step if needed.
        // Actually, let's assume we need to create 'item_quotation.xml' first or use a dynamic layout.
        // Wait, I can't create multiple files in one step properly if they depend on each other.
        // I'll create the adapter to point to 'R.layout.item_quotation' and I'll create that file in the next step.
        View view = LayoutInflater.from(context).inflate(R.layout.item_quotation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Enquiry enquiry = enquiryList.get(position);

        holder.tvId.setText("ID #" + (enquiry.getReference() != null ? enquiry.getReference() : enquiry.getId()));
        
        // Date
        if (enquiry.getCreatedAt() != null) {
            holder.tvDate.setText(enquiry.getCreatedAt().split("T")[0]); // Simple date formatting
        } else {
            holder.tvDate.setText("-");
        }

        // Status
        String status = enquiry.getStatus();
        holder.tvStatus.setText(status);

        // Styling based on status
        if (status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("quoted") || status.equalsIgnoreCase("responded")) {
            holder.statusBadge.setBackgroundResource(R.drawable.bg_status_approved);
            holder.tvStatus.setTextColor(Color.WHITE);
            holder.icon.setImageResource(R.drawable.ic_verified);
            holder.icon.setColorFilter(Color.WHITE);
            holder.iconBg.setBackgroundResource(R.drawable.bg_black_circle);
        } else if (status.equalsIgnoreCase("Rejected") || status.equalsIgnoreCase("Cancelled")) {
            holder.statusBadge.setBackgroundResource(R.drawable.bg_status_rejected);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.slate_500));
            holder.icon.setImageResource(R.drawable.ic_block);
            holder.icon.setColorFilter(context.getResources().getColor(R.color.slate_400));
            holder.iconBg.setBackgroundResource(R.drawable.bg_grey_circle);
        } else {
            // Pending / New
            holder.statusBadge.setBackgroundResource(R.drawable.bg_status_pending);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.slate_600));
            holder.icon.setImageResource(R.drawable.ic_pending);
            holder.icon.setColorFilter(context.getResources().getColor(R.color.slate_600));
            holder.iconBg.setBackgroundResource(R.drawable.bg_grey_circle);
        }

        // Amount (placeholder as it comes from admin response usually, or hidden)
        // For now, show specific Product Name if available in message or something, 
        // but typically "Enquiry" implies a price check.
        // Let's just show "Enquiry" or parse message.
        holder.tvAmount.setText("Enquiry"); 

        holder.btnViewDetails.setOnClickListener(v -> {
            // Toast.makeText(context, "Navigating to details...", Toast.LENGTH_SHORT).show(); 
            android.content.Intent intent = new android.content.Intent(context, com.example.florra_a.QuotationDetailsActivity.class);
            intent.putExtra("enquiry_data", enquiry);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK); // Add this just in case context is not activity
            context.startActivity(intent);
        });
        
        // Also make the whole item clickable
        holder.itemView.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(context, com.example.florra_a.QuotationDetailsActivity.class);
            intent.putExtra("enquiry_data", enquiry);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return enquiryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvAmount, tvStatus, tvDate;
        LinearLayout statusBadge, btnViewDetails;
        ImageView icon;
        View iconBg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            icon = itemView.findViewById(R.id.icon);
            iconBg = itemView.findViewById(R.id.iconBg);
        }
    }
}
