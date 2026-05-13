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
            
            // Consistent "No:" label
            String tileNo = product.getTileNo();
            tvSku.setText("No: " + (tileNo != null ? tileNo : product.getId()));
            
            tvStockCount.setText(String.valueOf(product.getStock()));

            // Load Image
            String imageUrl = product.getImage();
            ivProductImage.setImageTintList(null); // Clear any existing tint for actual image
            
            if (imageUrl != null && !imageUrl.isEmpty()) {
                if (!imageUrl.startsWith("http")) {
                    imageUrl = com.example.florra_a.network.RetrofitClient.BASE_URL + imageUrl;
                }
                com.bumptech.glide.Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_tile_placeholder)
                    .error(R.drawable.ic_tile_placeholder)
                    .into(ivProductImage);
            } else {
                ivProductImage.setImageResource(R.drawable.ic_tile_placeholder);
                ivProductImage.setImageTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#D4D4D8"))); // zinc_300 for placeholder
            }
            
            String status = product.getStockStatus();
            if (status == null) status = "In Stock";
            tvStockStatus.setText(status.toUpperCase());

            // Button Text Logic
            String btnText = "UPDATE";
            if ("Low Stock".equals(status)) btnText = "ORDER";
            if ("Empty".equalsIgnoreCase(status) || "Out of Stock".equalsIgnoreCase(status)) btnText = "RESTOCK";
            btnAction.setText(btnText);

            // Visual Styling and Frozen Status
            int green700 = android.graphics.Color.parseColor("#047857");
            int orange500 = android.graphics.Color.parseColor("#F97316");
            int red700 = android.graphics.Color.parseColor("#b91c1c");
            int zinc600 = android.graphics.Color.parseColor("#52525b");

            if (!product.isActive()) {
                tvStockStatus.setText("FROZEN");
                tvStockStatus.setBackgroundResource(R.drawable.bg_zinc_badge); // Assuming this exists or using a generic one
                tvStockStatus.setTextColor(zinc600);
            } else if ("In Stock".equalsIgnoreCase(status)) {
                tvStockStatus.setBackgroundResource(R.drawable.bg_green_badge);
                tvStockStatus.setTextColor(green700);
            } else if ("Low Stock".equalsIgnoreCase(status)) {
                tvStockStatus.setBackgroundResource(R.drawable.bg_amber_badge);
                tvStockStatus.setTextColor(orange500);
            } else {
                tvStockStatus.setBackgroundResource(R.drawable.bg_red_badge);
                tvStockStatus.setTextColor(red700);
            }

            cardProduct.setOnClickListener(v -> listener.onItemClick(product));
            btnAction.setOnClickListener(v -> listener.onActionClick(product));
        }
    }
}
