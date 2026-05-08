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
                 imageUrl = RetrofitClient.BASE_URL + imageUrl;
             } else {
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
                    .error(R.drawable.ic_tile_placeholder) // Show placeholder on error
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@androidx.annotation.Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            android.util.Log.e("RecAdapter", "Image Load Failed for URL: " + model, e);
                            return false; // Allow calling onLoadFailed on target
                        }

                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            android.util.Log.d("RecAdapter", "Image Loaded Successfully from: " + dataSource);
                            return false;
                        }
                    })
                    .into(holder.tileImage);
        } else {
            holder.tileImage.setImageResource(R.drawable.ic_tile_placeholder);
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
