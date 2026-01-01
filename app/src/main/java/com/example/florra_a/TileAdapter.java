package com.example.florra_a;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TileAdapter extends RecyclerView.Adapter<TileAdapter.ViewHolder> {

    private Context context;
    private List<Tile> tileList;

    // NEW: Interface for click listeners
    public interface OnItemClickListener {
        void onItemClick(Tile tile);
        void onBookmarkClick(Tile tile);
        void onAddToCartClick(Tile tile);
    }

    private OnItemClickListener onItemClickListener;

    public TileAdapter(Context context, List<Tile> tileList) {
        this.context = context;
        this.tileList = tileList;
    }

    // NEW: Set click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tile tile = tileList.get(position);

        // Set tile data
        holder.tileName.setText(tile.getName());
        holder.tilePrice.setText("$" + tile.getPrice());
        holder.tileSize.setText(tile.getSize());
        holder.stockBadgeText.setText(tile.getStock());

        // Set stock color
        if (tile.getStock().equals("LOW STOCK")) {
            holder.stockBadgeText.setTextColor(context.getResources().getColor(R.color.orange_600));
            holder.stockBadge.setBackgroundResource(R.drawable.bg_tag_low_stock);
        } else {
            holder.stockBadgeText.setTextColor(context.getResources().getColor(R.color.green_600));
            holder.stockBadge.setBackgroundResource(R.drawable.bg_tag_stock);
        }

        // Set image
        holder.tileImage.setImageResource(tile.getImage());

        // Set finish text
        if (tile.getFinish() != null && !tile.getFinish().isEmpty()) {
            holder.tileFinish.setText(tile.getFinish());
        } else {
            holder.tileFinish.setText("Porcelain"); // Default value
        }

        // Handle item click - OPEN PRODUCT DETAILS
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If listener is set, use it
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(tile);
                } else {
                    // Otherwise use the old method
                    openProductDetails(tile);
                }
            }
        });

        // Handle add to cart button click
        if (holder.btnAddToCart != null) {
            holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If listener is set, use it
                    if (onItemClickListener != null) {
                        onItemClickListener.onAddToCartClick(tile);
                    } else {
                        // Otherwise show toast
                        Toast.makeText(context, "Added to cart: " + tile.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // NEW: Handle bookmark button click
        if (holder.btnBookmark != null) {
            holder.btnBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // If listener is set, use it
                    if (onItemClickListener != null) {
                        onItemClickListener.onBookmarkClick(tile);
                    } else {
                        // Otherwise show toast
                        Toast.makeText(context, "Bookmarked: " + tile.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return tileList.size();
    }

    // Old method for opening product details (for backward compatibility)
    private void openProductDetails(Tile tile) {
        Intent intent = new Intent(context, ProductDetailsActivity.class);

        // Pass ALL required data
        intent.putExtra("productName", tile.getName());
        intent.putExtra("productPrice", "$" + tile.getPrice());
        intent.putExtra("productSize", tile.getSize());
        intent.putExtra("productStock", tile.getStock());
        intent.putExtra("productModel", "FL-ST-" + String.format("%03d", tileList.indexOf(tile) + 1));
        intent.putExtra("productMaterial", tile.getFinish() != null ? tile.getFinish() : "Porcelain");
        intent.putExtra("productFinish", "High Gloss");
        intent.putExtra("productThickness", "9mm");
        intent.putExtra("productCoverage", "1.44m²/box");
        intent.putExtra("productPacking", "2 Pcs / Box");

        context.startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView tileImage;
        TextView tileName;
        TextView tilePrice;
        TextView tileSize;
        TextView tileFinish;
        TextView stockBadgeText;
        LinearLayout stockBadge;
        LinearLayout btnAddToCart;
        LinearLayout btnBookmark; // NEW: Bookmark button

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            tileImage = itemView.findViewById(R.id.tileImage);
            tileName = itemView.findViewById(R.id.tileName);
            tilePrice = itemView.findViewById(R.id.tilePrice);
            tileSize = itemView.findViewById(R.id.tileSize);
            tileFinish = itemView.findViewById(R.id.tileFinish);
            stockBadgeText = itemView.findViewById(R.id.stockBadgeText);
            stockBadge = itemView.findViewById(R.id.stockBadge);
            btnBookmark = itemView.findViewById(R.id.btnBookmark); // NEW: Initialize bookmark button
        }
    }
}