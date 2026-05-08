package com.example.florra_a.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.florra_a.R;
import com.example.florra_a.models.Product;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ProductViewHolder> {

    private List<Product> products;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
        void onActionClick(Product product);
    }

    public InventoryAdapter(List<Product> products, OnItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    public void updateData(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return products != null ? products.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        CardView cardProduct;
        TextView tvProductName, tvProductDetails, tvSku, tvStockCount, tvStockStatus;
        Button btnAction;
        ImageView ivProductImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            cardProduct = itemView.findViewById(R.id.cardProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductDetails = itemView.findViewById(R.id.tvProductDetails);
            tvSku = itemView.findViewById(R.id.tvSku);
            tvStockCount = itemView.findViewById(R.id.tvStockCount);
            tvStockStatus = itemView.findViewById(R.id.tvStockStatus);
            btnAction = itemView.findViewById(R.id.btnAction);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
        }

        public void bind(final Product product, final OnItemClickListener listener) {
            tvProductName.setText(product.getTileName());
            tvProductDetails.setText(product.getSize() + " • " + product.getCategory());
            tvSku.setText("SKU: " + product.getId()); // Using ID as SKU for now
            tvStockCount.setText(String.valueOf(product.getStock()));

            // Load Image
            String imageUrl = product.getImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                if (!imageUrl.startsWith("http")) {
                    imageUrl = com.example.florra_a.network.RetrofitClient.BASE_URL + imageUrl;
                }
                com.bumptech.glide.Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.bg_bar_default) // Using a default drawable as placeholder
                    .error(R.drawable.bg_bar_default)
                    .into(ivProductImage);
            } else {
                ivProductImage.setImageResource(R.drawable.bg_bar_default);
            }
            
            String status = product.getStockStatus();
            if (status == null) status = "In Stock";
            tvStockStatus.setText(status);

            // Button Text Logic
            String btnText = "Update";
            if ("Low Stock".equals(status)) btnText = "Order";
            if ("Empty".equals(status)) btnText = "Restock";
            btnAction.setText(btnText);

            // Visual Styling
            int green700 = android.graphics.Color.parseColor("#047857");
            int amber700 = android.graphics.Color.parseColor("#b45309");
            int amber600 = android.graphics.Color.parseColor("#d97706");
            int red700 = android.graphics.Color.parseColor("#b91c1c");
            int red600 = android.graphics.Color.parseColor("#dc2626");
            int primary = android.graphics.Color.parseColor("#4f46e5"); // Approximate primary
            int white = android.graphics.Color.WHITE;

            switch (status) {
                case "In Stock":
                    tvStockStatus.setBackgroundResource(R.drawable.bg_green_badge);
                    tvStockStatus.setTextColor(green700);
                    tvStockCount.setTextColor(primary);
                    
                    btnAction.setBackgroundResource(R.drawable.bg_outline_button);
                    btnAction.setTextColor(primary);
                    break;
                case "Low Stock":
                    tvStockStatus.setBackgroundResource(R.drawable.bg_amber_badge);
                    tvStockStatus.setTextColor(amber700);
                    tvStockCount.setTextColor(amber600);
                    
                    btnAction.setBackgroundResource(R.drawable.bg_primary_button);
                    btnAction.setTextColor(white);
                    break;
                case "Empty":
                    tvStockStatus.setBackgroundResource(R.drawable.bg_red_badge);
                    tvStockStatus.setTextColor(red700);
                    tvStockCount.setTextColor(red600);
                    
                    btnAction.setBackgroundResource(R.drawable.bg_primary_button);
                    btnAction.setTextColor(white);
                    itemView.setAlpha(0.8f);
                    break;
                default: 
                     // Fallback
                    tvStockStatus.setBackgroundResource(R.drawable.bg_green_badge);
                    tvStockStatus.setTextColor(green700);
                    tvStockCount.setTextColor(primary);
                    itemView.setAlpha(1.0f);
            }

            cardProduct.setOnClickListener(v -> listener.onItemClick(product));
            btnAction.setOnClickListener(v -> listener.onActionClick(product));
        }
    }
}
