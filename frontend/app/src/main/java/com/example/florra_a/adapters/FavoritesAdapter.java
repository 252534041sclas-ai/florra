package com.example.florra_a.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.florra_a.ProductDetailsActivity;
import com.example.florra_a.R;
import com.example.florra_a.models.Product;
import com.example.florra_a.network.RetrofitClient;
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
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = favoritesList.get(position);

        // ── Text fields ───────────────────────────────────────────────────────
        holder.productName.setText(product.getTileName());
        holder.productPrice.setText("₹" + product.getPrice());

        // Size / TileNo
        String tileNo = product.getTileNo();
        String size   = product.getSize();
        holder.productSize.setText(
            (tileNo != null && !tileNo.isEmpty()) ? tileNo : (size != null ? size : "60x60 cm")
        );

        // Finish / Category fallback
        String finish = product.getFinish();
        if (finish == null) finish = product.getCategory();
        holder.productFinish.setText(finish != null ? finish.toUpperCase() : "PORCELAIN");

        // ── Stock badge ───────────────────────────────────────────────────────
        String stockStatus = product.getStockStatus();
        if (stockStatus == null) {
            stockStatus = product.getStock() > 0 ? "IN STOCK" : "OUT OF STOCK";
        }
        holder.tvStockBadge.setText(stockStatus);

        if ("LOW STOCK".equalsIgnoreCase(stockStatus) || "OUT OF STOCK".equalsIgnoreCase(stockStatus)) {
            holder.tvStockBadge.setTextColor(ContextCompat.getColor(context, R.color.orange_600));
            holder.layoutStockBadge.setBackgroundResource(R.drawable.bg_tag_low_stock);
        } else {
            holder.tvStockBadge.setTextColor(ContextCompat.getColor(context, R.color.green_600));
            holder.layoutStockBadge.setBackgroundResource(R.drawable.bg_tag_stock);
        }

        // ── Product image ─────────────────────────────────────────────────────
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
                .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.tile_placeholder);
        }

        // ── Heart icon — always filled red (this IS the favourites list) ──────
        if (holder.heartIcon != null) {
            holder.heartIcon.setImageResource(R.drawable.ic_favorite_filled);
            holder.heartIcon.setColorFilter(ContextCompat.getColor(context, R.color.red_600));
        }

        // ── Tap heart → remove from favourites ────────────────────────────────
        holder.btnFavorite.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener != null) {
                listener.onRemoveFavorite(product, pos);
            }
        });

        // ── Tap card → product details ────────────────────────────────────────
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("productId",          product.getId());
            intent.putExtra("tileName",           product.getTileName());
            intent.putExtra("tilePrice",          String.valueOf(product.getPrice()));
            intent.putExtra("tileSize",           product.getSize());
            intent.putExtra("productFinish",      product.getFinish());
            intent.putExtra("productMaterial",    product.getCategory());
            intent.putExtra("productDescription", product.getDescription());
            intent.putExtra("productImage",       product.getImage());
            intent.putExtra("stockStatus",        product.getStock() > 0 ? "IN STOCK" : "OUT OF STOCK");
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView   productImage;
        LinearLayout btnFavorite;
        ImageView   heartIcon;
        TextView    productName, productSize, productPrice, productFinish, tvStockBadge;
        LinearLayout layoutStockBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage     = itemView.findViewById(R.id.tileImage);
            productName      = itemView.findViewById(R.id.tileName);
            productSize      = itemView.findViewById(R.id.tileSize);
            productPrice     = itemView.findViewById(R.id.tilePrice);
            productFinish    = itemView.findViewById(R.id.tileFinish);
            btnFavorite      = itemView.findViewById(R.id.btnFavorite);
            heartIcon        = btnFavorite != null
                               ? (ImageView) btnFavorite.getChildAt(0)
                               : null;
            tvStockBadge     = itemView.findViewById(R.id.stockBadgeText);
            layoutStockBadge = itemView.findViewById(R.id.stockBadge);
        }
    }
}
