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
import com.bumptech.glide.Glide;
import com.example.florra_a.models.Product;
import com.example.florra_a.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;

public class TileAdapter extends RecyclerView.Adapter<TileAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private List<Product> productListFull; // For filtering
    private boolean isHorizontal = false;

    // Interface for click listeners
    public interface OnItemClickListener {
        void onItemClick(Product product);
        void onItemLongClick(Product product);
        void onBookmarkClick(Product product);
        void onAddToCartClick(Product product);
    }

    private OnItemClickListener onItemClickListener;

    public TileAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList);
    }

    public TileAdapter(Context context, List<Product> productList, boolean isHorizontal) {
        this.context = context;
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList);
        this.isHorizontal = isHorizontal;
    }

    // Set click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // Update data method
    public void updateData(List<Product> newProducts) {
        this.productList = newProducts;
        this.productListFull = new ArrayList<>(newProducts);
        notifyDataSetChanged();
    }

    // Filter method
    public void filterList(List<Product> filteredList) {
        productList = filteredList;
        notifyDataSetChanged();
    }

    // Filter by category
    public void filterByCategory(String category) {
        List<Product> filteredList = new ArrayList<>();

        if (category.equals("all") || category.equals("all_tiles")) {
            filteredList.addAll(productListFull);
        } else {
            for (Product product : productListFull) {
                // Check if product has category field and it matches
                // Assuming getCategory() exists in Product, otherwise match by logic
                if (product.getCategory() != null && product.getCategory().equalsIgnoreCase(category)) {
                    filteredList.add(product);
                }
            }
        }
        productList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tile, parent, false);
        
        if (isHorizontal) {
            // Set fixed width for horizontal items
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(
                        (int) (context.getResources().getDisplayMetrics().widthPixels * 0.45),
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.45);
            }
            view.setLayoutParams(layoutParams);
        }
        
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set product data
        holder.tileName.setText(product.getTileName());
        holder.tilePrice.setText("₹" + product.getPrice());
        
        // Size / Model
        String size = product.getSize();
        String tileNo = product.getTileNo();
        if (tileNo != null && !tileNo.isEmpty()) {
             holder.tileSize.setText(tileNo); 
        } else {
             holder.tileSize.setText(size != null ? size : "60x60 cm");
        }

        // Category / Finish
        String category = product.getCategory();
        String finish = product.getFinish();
        if (finish != null && !finish.isEmpty()) {
            holder.tileFinish.setText(finish.toUpperCase());
        } else if (category != null && !category.isEmpty()) {
            holder.tileFinish.setText(category.toUpperCase());
        } else {
            holder.tileFinish.setText("PORCELAIN");
        }

        // Stock Status
        String stockStatus = product.getStockStatus();
        if (stockStatus == null) {
            stockStatus = product.getStock() > 0 ? "IN STOCK" : "OUT OF STOCK";
        }
        holder.stockBadgeText.setText(stockStatus);

        // Set stock color
        if ("LOW STOCK".equalsIgnoreCase(stockStatus) || "OUT OF STOCK".equalsIgnoreCase(stockStatus)) {
            holder.stockBadgeText.setTextColor(context.getResources().getColor(R.color.orange_600));
            holder.stockBadge.setBackgroundResource(R.drawable.bg_tag_low_stock);
        } else {
            holder.stockBadgeText.setTextColor(context.getResources().getColor(R.color.green_600));
            holder.stockBadge.setBackgroundResource(R.drawable.bg_tag_stock);
        }

        // Load image using Glide
        String imageUrl = product.getImage();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http")) {
                // Ensure it starts with media/ if it's a relative path from Django
                if (imageUrl.startsWith("/")) imageUrl = imageUrl.substring(1);
                
                if (!imageUrl.startsWith("media/")) {
                    imageUrl = "media/" + imageUrl;
                }
                
                imageUrl = RetrofitClient.BASE_URL + imageUrl;
            } else {
                // Fix potential localhost issues
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
                .error(R.drawable.ic_tile_placeholder)
                .centerCrop()
                .into(holder.tileImage);
        } else {
            holder.tileImage.setImageResource(R.drawable.ic_tile_placeholder);
        }

        // Handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(product);
                } else {
                    openProductDetails(product);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemLongClick(product);
                    return true;
                }
                return false;
            }
        });

        // Handle bookmark button click
        if (holder.btnBookmark != null) {
            // Update UI based on state
            ImageView heartIcon = null;
            if (holder.btnBookmark instanceof ViewGroup && ((ViewGroup) holder.btnBookmark).getChildCount() > 0) {
                View child = ((ViewGroup) holder.btnBookmark).getChildAt(0);
                if (child instanceof ImageView) {
                    heartIcon = (ImageView) child;
                }
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

            final ImageView finalHeartIcon = heartIcon;
            holder.btnBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toggle state
                    boolean newState = !product.isFavorite();
                    product.setFavorite(newState);
                    
                    // Update UI immediately
                    if (finalHeartIcon != null) {
                        if (newState) {
                            finalHeartIcon.setImageResource(R.drawable.ic_favorite_filled);
                            finalHeartIcon.setColorFilter(context.getResources().getColor(R.color.red_600));
                        } else {
                            finalHeartIcon.setImageResource(R.drawable.ic_favorite_border);
                            finalHeartIcon.setColorFilter(context.getResources().getColor(R.color.slate_600));
                        }
                    }

                    if (onItemClickListener != null) {
                        onItemClickListener.onBookmarkClick(product);
                    } else {
                        Toast.makeText(context, newState ? "Added to Favorites" : "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    private void openProductDetails(Product product) {
        try {
            Intent intent = new Intent(context, ProductDetailsActivity.class);

            // Pass data using consistent keys
            intent.putExtra("productId", product.getId());
            intent.putExtra("productName", product.getTileName());
            intent.putExtra("tileName", product.getTileName());
            intent.putExtra("productPrice", String.valueOf(product.getPrice()));
            intent.putExtra("tilePrice", String.valueOf(product.getPrice()));
            intent.putExtra("productStock", product.getStockStatus());
            intent.putExtra("stockStatus", product.getStock() > 0 ? "IN STOCK" : "OUT OF STOCK");
            intent.putExtra("productCategory", product.getCategory());
            intent.putExtra("productMaterial", product.getCategory());
            intent.putExtra("productTileNo", product.getTileNo());
            intent.putExtra("tileSize", product.getSize());
            intent.putExtra("productFinish", product.getFinish());
            intent.putExtra("productDescription", product.getDescription());
            intent.putExtra("productImage", product.getImage());

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
        View btnBookmark; // Corrected name to match logic

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
            btnBookmark = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
