package com.example.florra_a.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.florra_a.ProductDetailsActivity;
import com.example.florra_a.R;
import com.example.florra_a.models.Product;
// FavoritesManager removed
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private Context context;
    private List<Product> favoritesList;
    private OnFavoriteActionListener listener;

    public interface OnFavoriteActionListener {
        void onRemoveFavorite(Product product, int position);
    }

    public FavoritesAdapter(Context context, List<Product> favoritesList, OnFavoriteActionListener listener) {
        this.context = context;
        this.favoritesList = favoritesList;
        this.listener = listener;
        // favoritesManager removed
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use item_tile.xml instead of deleted item_product_card.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_tile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = favoritesList.get(position);

        holder.productName.setText(product.getTileName());
        holder.productPrice.setText("₹" + product.getPrice()); // Update currency
        holder.productSize.setText(product.getSize());
        
        // Bind Finish if available (item_tile has this)
         if (product.getFinish() != null && !product.getFinish().isEmpty()) {
            holder.productFinish.setText(product.getFinish());
            holder.productFinish.setVisibility(View.VISIBLE);
        } else {
            holder.productFinish.setVisibility(View.GONE);
        }

        // Load image (Same as before)
        String imageUrl = product.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http")) {
                 if (imageUrl.startsWith("/")) imageUrl = imageUrl.substring(1);
                 imageUrl = com.example.florra_a.network.RetrofitClient.BASE_URL + imageUrl;
             } else {
                 String baseHost = com.example.florra_a.network.RetrofitClient.BASE_URL
                         .replace("http://", "")
                         .replace("https://", "")
                         .split(":")[0];
                 imageUrl = imageUrl.replace("127.0.0.1", baseHost)
                                    .replace("localhost", baseHost);
             }
             
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_tile_placeholder) // Updated placeholder name
                .error(R.drawable.ic_tile_placeholder)
                .centerCrop()
                .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.ic_tile_placeholder);
        }

        // Handle item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            // Updated keys to match ProductDetailsActivity expectations
            intent.putExtra("productId", product.getId()); 
            intent.putExtra("tileName", product.getTileName());
            intent.putExtra("tilePrice", String.valueOf(product.getPrice()));
            intent.putExtra("tileSize", product.getSize());
            intent.putExtra("productFinish", product.getFinish());
            intent.putExtra("productMaterial", product.getCategory());
            intent.putExtra("productDescription", product.getDescription());
            intent.putExtra("productImage", product.getImage());
            intent.putExtra("stockStatus", product.getStock() > 0 ? "IN STOCK" : "OUT OF STOCK");
            context.startActivity(intent);
        });
        
        // Handle favorite click (Remove)
        // In item_tile.xml, btnFavorite is a LinearLayout container with an ImageView inside
        // But we can set click listener on the container
        // We need to access the ImageView inside to change color? 
        // structure: LinearLayout(id=btnFavorite) -> ImageView
        // Ideally we should bind the ImageView if we want to change tint,
        // or just rely on the container.
        // Let's assume we can find the ImageView if needed, or just set OnClickListener on container.
        
        // Note: item_tile.xml has a LinearLayout with id btnFavorite.
        // Inside it there is an ImageView.
        
        holder.btnFavorite.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onRemoveFavorite(product, pos);
            }
        });
        
        // Make the favorite icon red to indicate it is favorited
        // In item_tile, the ImageView inside btnFavorite is what we want to tint.
        // Since we only have the container ID in ViewHolder currently (based on my previous HomeProductAdapter edit),
        // we might leave it as is or try to access child.
        // Actually, let's update ViewHolder to find the ImageView inside if possible or just tint the container?
        // No, tinting container is bad. 
        // Let's modify ViewHolder to get the ImageView.
        if (holder.btnFavorite instanceof ViewGroup) {
            View child = ((ViewGroup) holder.btnFavorite).getChildAt(0);
             if (child instanceof ImageView) {
                ((ImageView) child).setImageResource(R.drawable.ic_favorite_filled);
                ((ImageView) child).setColorFilter(context.getResources().getColor(R.color.red_600));
            }
        }
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        View btnFavorite; // Changed to View/ViewGroup
        TextView productName, productSize, productPrice, productFinish;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Mapping details from item_tile.xml
            productImage = itemView.findViewById(R.id.tileImage);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            productName = itemView.findViewById(R.id.tileName);
            productSize = itemView.findViewById(R.id.tileSize);
            productPrice = itemView.findViewById(R.id.tilePrice);
            productFinish = itemView.findViewById(R.id.tileFinish);
        }
    }
}
