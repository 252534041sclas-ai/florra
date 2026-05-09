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

import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    private Context context;
    private List<Product> products;

    public RecommendationAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tileName.setText(product.getTileName());
        holder.tileSize.setText(product.getSize());
        holder.tileFinish.setText(product.getFinish());
        holder.tilePrice.setText("₹" + product.getPrice());

        if (product.getStock() > 0) {
            holder.stockBadge.setVisibility(View.VISIBLE);
        } else {
            holder.stockBadge.setVisibility(View.GONE);
        }

        // Load image
        String imageUrl = product.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
             if (!imageUrl.startsWith("http")) {
                 if (imageUrl.startsWith("/")) imageUrl = imageUrl.substring(1);
                 
                 // If the path doesn't start with media/, and it's a relative path from the DB
                 if (!imageUrl.startsWith("media/")) {
                     imageUrl = "media/" + imageUrl;
                 }
                 
                 // Use the standard RetrofitClient.BASE_URL (which is port 8001)
                 imageUrl = RetrofitClient.BASE_URL + imageUrl;
             } else {
                 // Handle absolute URLs if any, replacing localhost/127.0.0.1 with actual IP
                 String baseHost = RetrofitClient.BASE_URL
                         .replace("http://", "")
                         .replace("https://", "")
                         .split(":")[0];
                 imageUrl = imageUrl.replace("127.0.0.1", baseHost)
                                    .replace("localhost", baseHost);
             }
             
            // Log the URL we are trying to load
            android.util.Log.d("RecAdapter", "Loading Image URL: " + imageUrl);

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_tile_placeholder)
                    .error(R.drawable.ic_tile_placeholder)
                    .into(holder.tileImage);
        } else {
            holder.tileImage.setImageResource(R.drawable.ic_tile_placeholder);
        }

        // Show match score if available
        if (product.getSimilarityScore() > 0) {
            holder.stockBadge.setVisibility(View.VISIBLE);
            // Convert e.g. 0.956 to "95% Match"
            int percentage = (int) (product.getSimilarityScore() * 100);
            if (percentage > 100) percentage = 100;
            TextView tvBadgeText = holder.itemView.findViewById(R.id.stockBadgeText);
            if (tvBadgeText != null) {
                tvBadgeText.setText(percentage + "% MATCH");
            }
        } else if (product.getStock() > 0) {
            holder.stockBadge.setVisibility(View.VISIBLE);
            TextView tvBadgeText = holder.itemView.findViewById(R.id.stockBadgeText);
            if (tvBadgeText != null) tvBadgeText.setText("IN STOCK");
        } else {
            holder.stockBadge.setVisibility(View.GONE);
        }
        
        // Debug Logging
        android.util.Log.d("RecAdapter", "Binding product: " + position);
        android.util.Log.d("RecAdapter", "Name: " + product.getTileName());
        android.util.Log.d("RecAdapter", "Image: " + product.getImage());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("product_id", product.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView tileImage;
        TextView tileName, tileSize, tileFinish, tilePrice;
        android.widget.LinearLayout stockBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tileImage = itemView.findViewById(R.id.tileImage);
            tileName = itemView.findViewById(R.id.tileName);
            tileSize = itemView.findViewById(R.id.tileSize);
            tileFinish = itemView.findViewById(R.id.tileFinish);
            tilePrice = itemView.findViewById(R.id.tilePrice);
            stockBadge = itemView.findViewById(R.id.stockBadge);
        }
    }
}
