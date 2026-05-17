package com.example.florra_a.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.R;
import com.example.florra_a.models.CustomerListItem;
import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.ViewHolder> {

    private List<CustomerListItem> customers;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(CustomerListItem customer);
    }

    public CustomerAdapter(List<CustomerListItem> customers, OnItemClickListener listener) {
        this.customers = customers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CustomerListItem customer = customers.get(position);
        holder.tvName.setText(customer.getName());
        holder.tvPhone.setText(customer.getPhone());
        
        String stats = customer.getBillCount() + " Bills • " + customer.getEnquiryCount() + " Enquiries";
        holder.tvStats.setText(stats);

        // Load Letter Avatar
        String avatarUrl = "https://ui-avatars.com/api/?name=" + customer.getName() + "&background=random&size=128";
        com.bumptech.glide.Glide.with(holder.itemView.getContext())
                .load(avatarUrl)
                .circleCrop()
                .into(holder.ivProfile);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(customer));
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public void updateList(List<CustomerListItem> newList) {
        this.customers = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvStats;
        android.widget.ImageView ivProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCustomerName);
            tvPhone = itemView.findViewById(R.id.tvCustomerPhone);
            tvStats = itemView.findViewById(R.id.tvCustomerStats);
            ivProfile = itemView.findViewById(R.id.ivCustomerProfile);
        }
    }
}
