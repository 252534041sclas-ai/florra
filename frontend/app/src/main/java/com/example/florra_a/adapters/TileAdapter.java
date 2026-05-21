package com.example.florra_a.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.florra_a.ProductDetailsActivity;
import com.example.florra_a.R;
import com.example.florra_a.models.Product;
import com.example.florra_a.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying product tiles in the Admin Catalog screen.
 * It binds product image, name, size, finish, price and stock badge.
 * Stock badge shows "IN STOCK", "LOW STOCK" or "OUT OF STOCK"
 * with appropriate colors and background drawables.
 */
public class TileAdapter extends RecyclerView.Adapter<TileAdapter.ViewHolder> {

    private final Context context;
    private List<Product> products;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    private final OnItemClickListener listener;

    public TileAdapter(Context context, List<Product> products, OnItemClickListener listener) {
        this.context = context;
        this.products = products != null ? products : new ArrayList<>();
        this.listener = listener;
    }

    public void updateData(List<Product> newProducts) {
        this.products = newProducts != null ? newProducts : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tile, parent, false);
        // Ensure consistent width for grid items
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        lp.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.45);
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvProductName.setText(product.getTileName());
        holder.tvProductSize.setText(product.getSize());
        holder.tvProductPrice.setText("₹" + product.getPrice());
        String finish = product.getFinish();
        if (finish == null) finish = product.getCategory();
        holder.tvProductFinish.setText(finish != null ? finish.toUpperCase() : "PORCELAIN");

        // Stock badge handling
        String stockStatus = product.getStockStatus();
        if (stockStatus == null) {
            stockStatus = product.getStock() > 0 ? "IN STOCK" : "OUT OF STOCK";
        }
        holder.tvStockBadge.setText(stockStatus);
        if ("LOW STOCK".equalsIgnoreCase(stockStatus) || "OUT OF STOCK".equalsIgnoreCase(stockStatus)) {
            holder.tvStockBadge.setTextColor(context.getResources().getColor(R.color.orange_600));
            holder.layoutStockBadge.setBackgroundResource(R.drawable.bg_tag_low_stock);
        } else {
            holder.tvStockBadge.setTextColor(context.getResources().getColor(R.color.green_600));
            holder.layoutStockBadge.setBackgroundResource(R.drawable.bg_tag_stock);
        }

        // Image loading
        String imageUrl = product.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http")) {
                if (imageUrl.startsWith("/")) imageUrl = imageUrl.substring(1);
                if (!imageUrl.startsWith("media/")) imageUrl = "media/" + imageUrl;
                imageUrl = RetrofitClient.BASE_URL + imageUrl;
            }
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.tile_placeholder)
                    .error(R.drawable.tile_placeholder)
                    .centerCrop()
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.tile_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("product_id", product.getId());
            intent.putExtra("product_name", product.getTileName());
            intent.putExtra("product_price", product.getPrice());
            // Additional extras can be added as required
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    /**
     * Filter the adapter's product list and refresh the view.
     * Used by AdminCatalogActivity to apply search and category filters.
     */
    public void filterList(List<Product> filteredList) {
        this.products = filteredList != null ? filteredList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductSize, tvProductPrice, tvProductFinish, tvStockBadge;
        View layoutStockBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.tileImage);
            tvProductName = itemView.findViewById(R.id.tileName);
            tvProductSize = itemView.findViewById(R.id.tileSize);
            tvProductPrice = itemView.findViewById(R.id.tilePrice);
            tvProductFinish = itemView.findViewById(R.id.tileFinish);
            tvStockBadge = itemView.findViewById(R.id.stockBadgeText);
            layoutStockBadge = itemView.findViewById(R.id.stockBadge);
        }
    }
}
