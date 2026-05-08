package com.example.florra_a;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.models.BillItem;
import java.util.List;

public class BillItemAdapter extends RecyclerView.Adapter<BillItemAdapter.ViewHolder> {

    private List<BillItem> items;

    public BillItemAdapter(List<BillItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BillItem item = items.get(position);
        holder.tvItemName.setText(item.getItemName());
        holder.tvItemSize.setText(item.getSize());
        holder.tvItemQty.setText(String.valueOf(item.getQuantity()));
        holder.tvItemRate.setText("₹" + (int)item.getRate());
        holder.tvItemAmount.setText("₹" + (int)item.getAmount());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemSize, tvItemQty, tvItemRate, tvItemAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemSize = itemView.findViewById(R.id.tvItemSize);
            tvItemQty = itemView.findViewById(R.id.tvItemQty);
            tvItemRate = itemView.findViewById(R.id.tvItemRate);
            tvItemAmount = itemView.findViewById(R.id.tvItemAmount);
        }
    }
}
