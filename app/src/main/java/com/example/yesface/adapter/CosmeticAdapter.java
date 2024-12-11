package com.example.yesface.adapter;

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
import com.example.yesface.R;
import com.example.yesface.model.Cosmetic;
import com.example.yesface.activity.DetailedActivity;  // Ensure you import your DetailedActivity

import java.io.Serializable;
import java.util.List;

public class CosmeticAdapter extends RecyclerView.Adapter<CosmeticAdapter.CosmeticViewHolder> {

    private Context context;
    private List<Cosmetic> cosmeticList;

    public CosmeticAdapter(Context context, List<Cosmetic> cosmeticList) {
        this.context = context;
        this.cosmeticList = cosmeticList;
    }

    @NonNull
    @Override
    public CosmeticViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cosmetic, parent, false);
        return new CosmeticViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CosmeticViewHolder holder, int position) {
        Cosmetic cosmetic = cosmeticList.get(position);

        holder.cosmeticName.setText(cosmetic.getName());
        holder.cosmeticDescription.setText(cosmetic.getDescription());
        holder.cosmeticPrice.setText(String.format("$%.2f", cosmetic.getPrice()));
        holder.cosmeticRating.setText( cosmetic.getRating());

        Glide.with(holder.itemView.getContext())
                .load(cosmetic.getImageUrl())
                .into(holder.cosmeticImage);

        // Set OnClickListener on the itemView to open DetailedActivity
        holder.itemView.setOnClickListener(v -> {
            // Create an Intent to navigate to DetailedActivity
            Intent intent = new Intent(context, DetailedActivity.class);

            // Pass the selected Cosmetic object to DetailedActivity
            intent.putExtra("cosmetic_data", cosmetic);  // Passing the Cosmetic object

            // Start DetailedActivity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cosmeticList.size();
    }

    public static class CosmeticViewHolder extends RecyclerView.ViewHolder {

        TextView cosmeticName, cosmeticDescription, cosmeticPrice, cosmeticRating;
        ImageView cosmeticImage;

        public CosmeticViewHolder(@NonNull View itemView) {
            super(itemView);
            cosmeticName = itemView.findViewById(R.id.cosmetic_name);
            cosmeticDescription = itemView.findViewById(R.id.cosmetic_description);
            cosmeticPrice = itemView.findViewById(R.id.cosmetic_price);
            cosmeticRating = itemView.findViewById(R.id.cosmetic_rating);
            cosmeticImage = itemView.findViewById(R.id.cosmetic_image);
        }
    }
}
