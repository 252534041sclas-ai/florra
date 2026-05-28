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

public class ChatProductAdapter extends RecyclerView.Adapter<ChatProductAdapter.ProductViewHolder> {

    private List<Product> products;

    public ChatProductAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tile, parent, false);
        // Set fixed width for horizontal scrolling
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = (int) (220 * parent.getContext().getResources().getDisplayMetrics().density);
        view.setLayoutParams(layoutParams);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.tvName.setText(product.getTileName() != null ? product.getTileName() : "Unnamed Tile");
        
        String size = product.getSize() != null ? product.getSize() : "Standard";
        String finish = product.getFinish() != null ? product.getFinish() : "Natural";
        
        holder.tvTagSize.setText(size);
        holder.tvTagFinish.setText(finish);
        
        String price = product.getPrice() != null ? product.getPrice() : "0";
        holder.tvPrice.setText("₹" + price);

        String imageUrl = product.getImage();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (!imageUrl.startsWith("http")) {
                imageUrl = RetrofitClient.BASE_URL + "media/" + imageUrl;
            }
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_tile_placeholder)
                    .into(holder.ivImage);
        }
        
        // Handle bookmark (favorite) icon
        ImageView heartIcon = (ImageView) holder.btnFavorite.getChildAt(0);
        if (product.isFavorite()) {
            heartIcon.setImageResource(R.drawable.ic_favorite_filled);
            heartIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.red_600));
        } else {
            heartIcon.setImageResource(R.drawable.ic_favorite_border);
            heartIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.slate_600));
        }

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("productId", product.getId());
            intent.putExtra("tileName", product.getTileName());
            intent.putExtra("tilePrice", String.valueOf(product.getPrice()));
            intent.putExtra("tileSize", product.getSize());
            intent.putExtra("productFinish", product.getFinish());
            intent.putExtra("productCategory", product.getCategory());
            intent.putExtra("productDescription", product.getDescription());
            intent.putExtra("productImage", product.getImage());
            intent.putExtra("stockStatus", product.getStock() > 0 ? "IN STOCK" : "OUT OF STOCK");
            intent.putExtra("productTileNo", product.getTileNo());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvPrice, tvTagSize, tvTagFinish;
        android.widget.LinearLayout btnFavorite;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.tileImage);
            tvName = itemView.findViewById(R.id.tileName);
            tvTagSize = itemView.findViewById(R.id.tileSize);
            tvTagFinish = itemView.findViewById(R.id.tileFinish);
            tvPrice = itemView.findViewById(R.id.tilePrice);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
