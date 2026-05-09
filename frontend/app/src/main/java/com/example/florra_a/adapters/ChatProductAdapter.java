package com.example.florra_a.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.florra_a.R;
import com.example.florra_a.models.Product;
import com.example.florra_a.network.RetrofitClient;

import java.util.List;

public class ChatProductAdapter extends RecyclerView.Adapter<ChatProductAdapter.ProductViewHolder> {

    private List<Product> products;

    public ChatProductAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvName.setText(product.getTileName());
        holder.tvDetails.setText(product.getCategory() + " | " + product.getSize() + " | " + product.getFinish());
        holder.tvPrice.setText("₹" + product.getPrice() + "/sqft");

        String imageUrl = product.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http")) {
                imageUrl = RetrofitClient.BASE_URL + "media/" + imageUrl;
            }
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_tile)
                    .into(holder.ivImage);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvDetails, tvPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvDetails = itemView.findViewById(R.id.tvProductDetails);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }
}
