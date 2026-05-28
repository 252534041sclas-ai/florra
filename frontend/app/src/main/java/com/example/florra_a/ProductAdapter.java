package com.example.florra_a;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.florra_a.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.textName.setText(product.getTileName());
        holder.textDetails.setText(product.getCategory() + " • " + product.getSize());
        
        // Stock logic
        int stock = product.getStock();
        String stockText;
        if (stock == 0) {
            stockText = "Out of Stock";
        } else if (stock < 20) {
            stockText = "Low Stock (" + stock + ")";
        } else {
            stockText = "In Stock (" + stock + ")";
        }
        holder.textStock.setText(stockText);
        
        if (stock == 0) {
           holder.stockContainer.setBackgroundResource(R.drawable.bg_stock_out);
           holder.stockDot.setBackgroundResource(R.drawable.bg_gray_dot);
           holder.textStock.setTextColor(context.getResources().getColor(R.color.zinc_500));
        } else if (stock < 20) {
           holder.stockContainer.setBackgroundResource(R.drawable.bg_stock_low);
            holder.stockDot.setBackgroundResource(R.drawable.bg_amber_dot);
            holder.textStock.setTextColor(context.getResources().getColor(R.color.amber_700));
        } else {
            holder.stockContainer.setBackgroundResource(R.drawable.bg_stock_in);
            holder.stockDot.setBackgroundResource(R.drawable.bg_green_dot);
            holder.textStock.setTextColor(context.getResources().getColor(R.color.emerald_700));
        }

        // Active/Inactive status
        if (product.isActive()) {
            holder.textStatus.setText("Active");
            holder.textStatus.setTextColor(context.getResources().getColor(R.color.emerald_700));
        } else {
            holder.textStatus.setText("Freeze");
            holder.textStatus.setTextColor(context.getResources().getColor(R.color.zinc_400));
        }

        holder.textPrice.setText("₹" + product.getPrice());

        // Load image using Glide
        if (product.getImage() != null && !product.getImage().isEmpty()) {
             String imageUrl = product.getImage();
             
             // If URL is relative (e.g. /media/products/img.jpg), prepend base URL
             if (!imageUrl.startsWith("http")) {
                 // Remove leading slash if present
                 if (imageUrl.startsWith("/")) {
                     imageUrl = imageUrl.substring(1);
                 }
                 imageUrl = com.example.florra_a.network.RetrofitClient.BASE_URL + imageUrl;
             } 
             // If URL contains localhost or 127.0.0.1, replace with the IP from Base URL
             else {
                 // Extract IP from BASE_URL for replacement (simple logic)
                 String baseHost = com.example.florra_a.network.RetrofitClient.BASE_URL
                         .replace("http://", "")
                         .replace("https://", "")
                         .split(":")[0];
                         
                 imageUrl = imageUrl.replace("127.0.0.1", baseHost)
                                    .replace("localhost", baseHost);
             }
             
             // Debug log
             android.util.Log.d("ProductAdapter", "Loading Image: " + imageUrl);
            
            // Clear tint so the actual image colors are shown
            holder.imageProduct.setImageTintList(null);
            
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_tile_placeholder)
                    .error(R.drawable.ic_tile_placeholder)
                    .into(holder.imageProduct);
        } else {
            holder.imageProduct.setImageResource(R.drawable.ic_tile_placeholder);
            // Restore tint for placeholder if needed, or keep it null if placeholder has color
            holder.imageProduct.setColorFilter(context.getResources().getColor(R.color.zinc_300));
        }

        holder.itemView.setOnClickListener(v -> {
            // Open Admin Product Details
            Intent intent = new Intent(context, AdminProductDetailsActivity.class);
            // Pass necessary data
            intent.putExtra("product_id", product.getId());
            intent.putExtra("product_name", product.getTileName());
            intent.putExtra("product_tile_no", product.getTileNo()); // New
            intent.putExtra("product_brand", product.getBrandName()); // New
            intent.putExtra("product_category", product.getCategory());
            intent.putExtra("product_size", product.getSize());
            intent.putExtra("product_finish", product.getFinish()); // New
            intent.putExtra("product_color", product.getColor()); // New
            intent.putExtra("product_price", product.getPrice());
            intent.putExtra("product_stock", String.valueOf(product.getStock()));
            int stockQty = product.getStock();
            String derivedStatus;
            if (stockQty <= 0) derivedStatus = "Out of Stock";
            else if (stockQty < 20) derivedStatus = "Low Stock";
            else derivedStatus = "In Stock";
            intent.putExtra("product_status", derivedStatus);
            intent.putExtra("product_description", product.getDescription());
            intent.putExtra("product_image", product.getImage());
            intent.putExtra("product_is_active", product.isActive()); // New
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    public List<Product> getOriginalList() {
        return productList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textDetails, textStock, textPrice, textStatus;
        ImageView imageProduct;
        View stockContainer, stockDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textDetails = itemView.findViewById(R.id.textDetails);
            textStock = itemView.findViewById(R.id.textStock);
            textPrice = itemView.findViewById(R.id.textPrice);
            textStatus = itemView.findViewById(R.id.textStatus);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            stockContainer = itemView.findViewById(R.id.stockContainer);
            stockDot = itemView.findViewById(R.id.stockDot);
        }
    }
}
