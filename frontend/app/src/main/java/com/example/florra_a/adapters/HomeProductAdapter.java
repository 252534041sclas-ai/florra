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

public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> products;

    public HomeProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    public void updateData(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tile, parent, false);
        // Important: Set a fixed width for horizontal items so they don't take full width
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.45); // 45% of screen width
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvProductName.setText(product.getTileName());
        holder.tvProductSize.setText(product.getSize());
        // For item_tile, price is just the number usually, but let's stick to format or adapt
        // item_tile.xml has "₹3.50" in tilePrice and "/sq.ft" in tileDetails
        holder.tvProductPrice.setText("₹" + product.getPrice());
        
        // Bind Finish if available
        if (product.getFinish() != null && !product.getFinish().isEmpty()) {
            holder.tvProductFinish.setText(product.getFinish());
            holder.tvProductFinish.setVisibility(View.VISIBLE);
        } else {
            holder.tvProductFinish.setVisibility(View.GONE);
        }

        // Load image (standard logic)
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
             
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_tile_placeholder)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_tile_placeholder);
        }
        
        // Show and Handle Favorite Button
        if (holder.btnFavorite != null) {
            holder.btnFavorite.setVisibility(View.VISIBLE);
            
            // Check if user is logged in
            android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

            // Set initial state
            // Note: product.isFavorite() might need to be populated from API response.
            // Home API response might not have 'is_favorite' set correctly if not authenticated or if API doesn't return it.
            // Assuming Product model has isFavorite() and it's populated.
            
            // We need to access the ImageView inside the container (btnFavorite is a View/ViewGroup in my previous edit)
            ImageView heartIcon = null;
            if (holder.btnFavorite instanceof ViewGroup) {
                View child = ((ViewGroup) holder.btnFavorite).getChildAt(0);
                if (child instanceof ImageView) {
                    heartIcon = (ImageView) child;
                }
            } else if (holder.btnFavorite instanceof ImageView) {
                heartIcon = (ImageView) holder.btnFavorite;
            }

            if (heartIcon != null) {
                if (product.isFavorite()) {
                    heartIcon.setImageResource(R.drawable.ic_favorite_filled);
                    heartIcon.setColorFilter(context.getResources().getColor(R.color.red_600));
                } else {
                    heartIcon.setImageResource(R.drawable.ic_favorite_border);
                    heartIcon.setColorFilter(context.getResources().getColor(R.color.slate_600));
                }
            }
            
            ImageView finalHeartIcon = heartIcon;
            holder.btnFavorite.setOnClickListener(v -> {
                if (!isLoggedIn) {
                    android.widget.Toast.makeText(context, "Please login to add favorites", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }

                // Toggle state locally first for responsiveness
                boolean newState = !product.isFavorite();
                product.setFavorite(newState);
                
                if (finalHeartIcon != null) {
                    if (newState) {
                        finalHeartIcon.setImageResource(R.drawable.ic_favorite_filled);
                        finalHeartIcon.setColorFilter(context.getResources().getColor(R.color.red_600));
                    } else {
                        finalHeartIcon.setImageResource(R.drawable.ic_favorite_border);
                        finalHeartIcon.setColorFilter(context.getResources().getColor(R.color.slate_600));
                    }
                }

                // Call API
                com.example.florra_a.network.ApiService apiService = RetrofitClient.getApiService();
                if (newState) {
                    // Add
                    java.util.Map<String, Integer> map = new java.util.HashMap<>();
                    map.put("product_id", product.getId());
                    apiService.addToFavorites(map).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                        @Override
                        public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                            if (response.isSuccessful()) {
                                android.widget.Toast.makeText(context, "Added to Favorites", android.widget.Toast.LENGTH_SHORT).show();
                            } else {
                                // Revert on failure
                                product.setFavorite(!newState);
                                if (finalHeartIcon != null) {
                                     finalHeartIcon.setImageResource(R.drawable.ic_favorite_border);
                                     finalHeartIcon.setColorFilter(context.getResources().getColor(R.color.slate_600));
                                }
                                android.widget.Toast.makeText(context, "Failed to add", android.widget.Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                             // Revert
                             product.setFavorite(!newState);
                             if (finalHeartIcon != null) {
                                 finalHeartIcon.setImageResource(R.drawable.ic_favorite_border);
                                 finalHeartIcon.setColorFilter(context.getResources().getColor(R.color.slate_600));
                             }
                             android.widget.Toast.makeText(context, "Network Error", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Remove
                    apiService.removeFromFavorites(product.getId()).enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                        @Override
                        public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                            if (response.isSuccessful()) {
                                android.widget.Toast.makeText(context, "Removed from Favorites", android.widget.Toast.LENGTH_SHORT).show();
                            } else {
                                // Revert
                                product.setFavorite(!newState);
                                if (finalHeartIcon != null) {
                                     finalHeartIcon.setImageResource(R.drawable.ic_favorite_filled);
                                     finalHeartIcon.setColorFilter(context.getResources().getColor(R.color.red_600));
                                }
                                android.widget.Toast.makeText(context, "Failed to remove", android.widget.Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                             // Revert
                             product.setFavorite(!newState);
                             if (finalHeartIcon != null) {
                                 finalHeartIcon.setImageResource(R.drawable.ic_favorite_filled);
                                 finalHeartIcon.setColorFilter(context.getResources().getColor(R.color.red_600));
                             }
                             android.widget.Toast.makeText(context, "Network Error", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("productId", product.getId());
            intent.putExtra("tileName", product.getTileName());
            intent.putExtra("tilePrice", String.valueOf(product.getPrice()));
            intent.putExtra("tileSize", product.getSize());
            intent.putExtra("productFinish", product.getFinish()); // mapped to finish
            intent.putExtra("productMaterial", product.getCategory()); // mapped to category
            intent.putExtra("productDescription", product.getDescription());
            intent.putExtra("productImage", product.getImage()); 
            intent.putExtra("stockStatus", product.getStock() > 0 ? "IN STOCK" : "OUT OF STOCK"); // Add stock status support
            
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        View btnFavorite; // Changed to View because it's a LinearLayout in item_tile
        TextView tvProductName, tvProductSize, tvProductPrice, tvProductFinish;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.tileImage);
            tvProductName = itemView.findViewById(R.id.tileName);
            tvProductSize = itemView.findViewById(R.id.tileSize);
            tvProductPrice = itemView.findViewById(R.id.tilePrice);
            tvProductFinish = itemView.findViewById(R.id.tileFinish);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
