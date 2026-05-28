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

    // Interface for click listeners
    public interface OnItemClickListener {
        void onItemClick(Product product);
        void onItemLongClick(Product product);
        void onBookmarkClick(Product product);
        void onAddToCartClick(Product product);
    }

    private OnItemClickListener onItemClickListener;
    private boolean isPickerMode = false;

    public void setPickerMode(boolean isPickerMode) {
        this.isPickerMode = isPickerMode;
        notifyDataSetChanged();
    }

    public TileAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList);
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

    // Filter by query and category across multiple fields
    public void filterByQuery(String query, String category) {
        List<Product> filteredList = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();

        for (Product product : productListFull) {
            // 1. Category Filter
            boolean matchesCategory = category.equalsIgnoreCase("all") || category.equalsIgnoreCase("all_tiles") ||
                                     (product.getCategory() != null && product.getCategory().equalsIgnoreCase(category));
            
            if (!matchesCategory) continue;

            // 2. Query Filter (Name, No, Category, Finish, Color)
            if (lowerQuery.isEmpty()) {
                filteredList.add(product);
                continue;
            }

            boolean matchesQuery = false;
            if (product.getTileName() != null && product.getTileName().toLowerCase().contains(lowerQuery)) matchesQuery = true;
            if (product.getTileNo() != null && product.getTileNo().toLowerCase().contains(lowerQuery)) matchesQuery = true;
            if (product.getCategory() != null && product.getCategory().toLowerCase().contains(lowerQuery)) matchesQuery = true;
            if (product.getFinish() != null && product.getFinish().toLowerCase().contains(lowerQuery)) matchesQuery = true;
            if (product.getColor() != null && product.getColor().toLowerCase().contains(lowerQuery)) matchesQuery = true;

            if (matchesQuery) {
                filteredList.add(product);
            }
        }
        productList = filteredList;
        notifyDataSetChanged();
    }

    // Filter by category
    public void filterByCategory(String category) {
        filterByQuery("", category);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set product data
        holder.tileName.setText(product.getTileName());
        holder.tilePrice.setText("₹" + product.getPrice());
        // Size might need to be fetched from backend or if missing, use placeholder/calc
        // Bind Real Data
        // Size / Model
        String size = product.getSize();
        String tileNo = product.getTileNo();
        if (tileNo != null && !tileNo.isEmpty()) {
             holder.tileSize.setText(tileNo); // User wants tileNo shown
        } else {
             holder.tileSize.setText(size != null ? size : "60x60 cm");
        }

        // Category / Finish
        String category = product.getCategory();
        if (category == null) category = product.getFinish();
        holder.tileFinish.setText(category != null ? category.toUpperCase() : "PORCELAIN");

        // Stock logic based on quantity
        int stock = product.getStock();
        String stockText;
        if (stock == 0) {
            stockText = "Out of Stock";
            holder.stockBadgeText.setTextColor(context.getResources().getColor(R.color.red_600));
            holder.stockBadge.setBackgroundResource(R.drawable.bg_tag_low_stock);
        } else if (stock < 10) {
            stockText = "Low Stock";
            holder.stockBadgeText.setTextColor(context.getResources().getColor(R.color.orange_600));
            holder.stockBadge.setBackgroundResource(R.drawable.bg_tag_low_stock);
        } else {
            stockText = "In Stock";
            holder.stockBadgeText.setTextColor(context.getResources().getColor(R.color.emerald_700));
            holder.stockBadge.setBackgroundResource(R.drawable.bg_tag_stock);
        }
        holder.stockBadgeText.setText(stockText);

        // Load image using Glide
        String imageUrl = product.getImage();
        
        // Debug removed as requested

        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http")) {
                if (imageUrl.startsWith("/")) imageUrl = imageUrl.substring(1);
                
                // Ensure media/ prefix is present for Django backend
                if (!imageUrl.startsWith("media/")) {
                    imageUrl = "media/" + imageUrl;
                }
                
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
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.tile_placeholder)
                .error(R.drawable.tile_placeholder)
                .centerCrop()
                .into(holder.tileImage);
        } else {
            holder.tileImage.setImageResource(R.drawable.tile_placeholder);
        }

        // Handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(product);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemLongClick(product);
                    return true; // Consume the long click
                }
                return false;
            }
        });



        // Handle add to cart button click
        if (holder.btnAddToCart != null) {
            holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onAddToCartClick(product);
                    } else {
                        Toast.makeText(context, "Added to cart: " + product.getTileName(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // Handle bookmark button click
        if (holder.btnBookmark != null) {
            ImageView heartIcon = (ImageView) holder.btnBookmark.getChildAt(0);
            boolean isAdmin = context instanceof AdminCatalogActivity;

            if (isAdmin) {
                // Admin mode: replace heart icon with share icon
                heartIcon.setImageResource(R.drawable.ic_share);
                heartIcon.setColorFilter(context.getResources().getColor(R.color.slate_600));

                holder.btnBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shareProductSpecifications(product);
                    }
                });
            } else {
                // Customer mode: normal favorite toggling
                if (product.isFavorite()) {
                    heartIcon.setImageResource(R.drawable.ic_favorite_filled);
                    heartIcon.setColorFilter(context.getResources().getColor(R.color.red_600));
                } else {
                    heartIcon.setImageResource(R.drawable.ic_favorite_border);
                    heartIcon.setColorFilter(context.getResources().getColor(R.color.slate_600));
                }

                holder.btnBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toggle state
                        boolean newState = !product.isFavorite();
                        product.setFavorite(newState);
                        
                        // Update UI immediately
                        if (newState) {
                            heartIcon.setImageResource(R.drawable.ic_favorite_filled);
                            heartIcon.setColorFilter(context.getResources().getColor(R.color.red_600));
                        } else {
                            heartIcon.setImageResource(R.drawable.ic_favorite_border);
                            heartIcon.setColorFilter(context.getResources().getColor(R.color.slate_600));
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
        
        // Handle select button click
        if (holder.btnSelectProduct != null) {
            holder.btnSelectProduct.setVisibility(isPickerMode ? View.VISIBLE : View.GONE);
            holder.btnSelectProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemLongClick(product); // Reuse the select logic
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void openProductDetails(Product product) {
        try {
            Intent intent = new Intent(context, ProductDetailsActivity.class);

            // Pass data
            intent.putExtra("productName", product.getTileName());
            intent.putExtra("productPrice", String.valueOf(product.getPrice()));
            intent.putExtra("productStock", product.getStockStatus());
            intent.putExtra("productCategory", product.getCategory());
            intent.putExtra("productTileNo", product.getTileNo());
            // Add other fields as needed

            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Cannot open product details", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareProductSpecifications(Product product) {
        StringBuilder sb = new StringBuilder();
        sb.append("✨ *").append(product.getTileName()).append("* Spec Sheet ✨\n\n");
        
        if (product.getTileNo() != null && !product.getTileNo().isEmpty()) {
            sb.append("🔹 *Tile No:* ").append(product.getTileNo()).append("\n");
        }
        if (product.getBrandName() != null && !product.getBrandName().isEmpty()) {
            sb.append("🔹 *Brand:* ").append(product.getBrandName()).append("\n");
        }
        if (product.getCategory() != null && !product.getCategory().isEmpty()) {
            sb.append("🔹 *Category:* ").append(product.getCategory()).append("\n");
        }
        if (product.getSize() != null && !product.getSize().isEmpty()) {
            sb.append("🔹 *Size:* ").append(product.getSize()).append("\n");
        }
        if (product.getFinish() != null && !product.getFinish().isEmpty()) {
            sb.append("🔹 *Finish:* ").append(product.getFinish()).append("\n");
        }
        if (product.getColor() != null && !product.getColor().isEmpty()) {
            sb.append("🔹 *Color:* ").append(product.getColor()).append("\n");
        }
        sb.append("💵 *Price:* ₹").append(product.getPrice()).append("/sq.ft\n");
        
        int stock = product.getStock();
        String stockStatus = stock == 0 ? "Out of Stock" : (stock < 10 ? "Low Stock (" + stock + " boxes)" : "In Stock (" + stock + " boxes)");
        sb.append("📦 *Stock Status:* ").append(stockStatus).append("\n\n");
        
        if (product.getDescription() != null && !product.getDescription().isEmpty()) {
            sb.append("📝 *Description:* ").append(product.getDescription()).append("\n\n");
        }

        String imageUrl = product.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http")) {
                if (imageUrl.startsWith("/")) imageUrl = imageUrl.substring(1);
                if (!imageUrl.startsWith("media/")) {
                    imageUrl = "media/" + imageUrl;
                }
                imageUrl = com.example.florra_a.network.RetrofitClient.BASE_URL + imageUrl;
            } else {
                // Handle absolute URLs if any, replacing localhost/127.0.0.1 with actual IP
                String baseHost = com.example.florra_a.network.RetrofitClient.BASE_URL
                        .replace("http://", "")
                        .replace("https://", "")
                        .split(":")[0];
                imageUrl = imageUrl.replace("127.0.0.1", baseHost)
                                   .replace("localhost", baseHost);
            }
            sb.append("🖼️ *Product Image:* ").append(imageUrl).append("\n\n");
        }

        sb.append("🔗 *Open in App:* florra://product/").append(product.getId()).append("\n");
        sb.append("🌐 *Web Link:* https://florra.com/product/").append(product.getId()).append("\n\n");

        sb.append("Shared via Florra Admin Catalog");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, product.getTileName() + " Specs");
        shareIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        context.startActivity(Intent.createChooser(shareIntent, "Share Tile Specifications"));
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
        TextView btnSelectProduct;

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
            btnSelectProduct = itemView.findViewById(R.id.btnSelectProduct);
        }
    }
}