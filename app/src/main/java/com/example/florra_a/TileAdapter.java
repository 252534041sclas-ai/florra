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
import java.util.ArrayList;
import java.util.List;

public class TileAdapter extends RecyclerView.Adapter<TileAdapter.ViewHolder> {

    private Context context;
    private List<Tile> tileList;
    private List<Tile> tileListFull; // For filtering

    // Interface for click listeners
    public interface OnItemClickListener {
        void onItemClick(Tile tile);
        void onBookmarkClick(Tile tile);
        void onAddToCartClick(Tile tile);
    }

    private OnItemClickListener onItemClickListener;

    public TileAdapter(Context context, List<Tile> tileList) {
        this.context = context;
        this.tileList = tileList;
        this.tileListFull = new ArrayList<>(tileList);
    }

    // Set click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Filter method
    public void filterList(List<Tile> filteredList) {
        tileList = filteredList;
        notifyDataSetChanged();
    }

    // Filter by category
    public void filterByCategory(String category) {
        List<Tile> filteredList = new ArrayList<>();

        if (category.equals("all") || category.equals("all_tiles")) {
            tileList = new ArrayList<>(tileListFull);
        } else {
            for (Tile tile : tileListFull) {
                // Check if tile has category field and it matches
                if (tile.getCategory() != null && tile.getCategory().equals(category)) {
                    filteredList.add(tile);
                }
            }
            tileList = filteredList;
        }

        notifyDataSetChanged();
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

        // Set tile data - FIXED: Using correct getter methods
        holder.tileName.setText(tile.getName());
        holder.tilePrice.setText(tile.getPrice());
        holder.tileSize.setText(tile.getSize());

        // FIX: Use getStockStatus() instead of getStock()
        holder.stockBadgeText.setText(tile.getStockStatus());

        // Set stock color - FIXED: Check stock status correctly
        if (tile.getStockStatus().equals("LOW STOCK")) {
            holder.stockBadgeText.setTextColor(context.getResources().getColor(R.color.orange_600));
            holder.stockBadge.setBackgroundResource(R.drawable.bg_tag_low_stock);
        } else {
            holder.stockBadgeText.setTextColor(context.getResources().getColor(R.color.green_600));
            holder.stockBadge.setBackgroundResource(R.drawable.bg_tag_stock);
        }

        // Set image - FIXED: Use getImageResource() or getImage()
        holder.tileImage.setImageResource(tile.getImageResource());

        // Set finish text
        if (tile.getFinish() != null && !tile.getFinish().isEmpty()) {
            holder.tileFinish.setText(tile.getFinish());
        } else {
            holder.tileFinish.setText("Porcelain");
        }

        // Handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(tile);
                } else {
                    openProductDetails(tile);
                }
            }
        });

        // Handle add to cart button click
        if (holder.btnAddToCart != null) {
            holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onAddToCartClick(tile);
                    } else {
                        Toast.makeText(context, "Added to cart: " + tile.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Handle bookmark button click
        if (holder.btnBookmark != null) {
            holder.btnBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onBookmarkClick(tile);
                    } else {
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

    private void openProductDetails(Tile tile) {
        try {
            Intent intent = new Intent(context, ProductDetailsActivity.class);

            // Pass data - FIXED: Using correct getter methods
            intent.putExtra("productName", tile.getName());
            intent.putExtra("productPrice", tile.getPrice());
            intent.putExtra("productSize", tile.getSize());
            intent.putExtra("productStock", tile.getStockStatus());
            intent.putExtra("productModel", "FL-ST-" + String.format("%03d", tileList.indexOf(tile) + 1));
            intent.putExtra("productMaterial", tile.getFinish() != null ? tile.getFinish() : "Porcelain");
            intent.putExtra("productFinish", "High Gloss");
            intent.putExtra("productThickness", "9mm");
            intent.putExtra("productCoverage", "1.44m²/box");
            intent.putExtra("productPacking", "2 Pcs / Box");
            intent.putExtra("productCategory", tile.getCategory());

            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Cannot open product details", Toast.LENGTH_SHORT).show();
        }
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
        LinearLayout btnBookmark;

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
            //btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
            btnBookmark = itemView.findViewById(R.id.btnFavorite);
        }
    }
}