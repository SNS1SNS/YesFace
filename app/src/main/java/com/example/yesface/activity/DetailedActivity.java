package com.example.yesface.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yesface.R;
import com.example.yesface.model.Cosmetic;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailedActivity extends AppCompatActivity {

    private TextView quantity;
    private int totalQuantity = 1;
    private double totalPrice = 0;
    private ImageView detailedImg;
    private TextView price, rating, description;
    private ImageView addItem, removeItem;
    private Cosmetic cosmetic = null;
    private AppCompatButton addToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        // Receive the Cosmetic object passed from the adapter
        cosmetic = (Cosmetic) getIntent().getSerializableExtra("cosmetic_data");

        // Initialize views
        quantity = findViewById(R.id.quantity);
        detailedImg = findViewById(R.id.detailed_img);
        addItem = findViewById(R.id.add_item);
        removeItem = findViewById(R.id.remove_item);
        price = findViewById(R.id.detailed_price);
        rating = findViewById(R.id.detailed_rating);
        description = findViewById(R.id.detailed_des);

        // Set the cosmetic data to the views
        if (cosmetic != null) {
            Glide.with(getApplicationContext()).load(cosmetic.getImageUrl()).into(detailedImg);
            rating.setText(cosmetic.getRating());
            description.setText(cosmetic.getDescription());
            price.setText("Цена: " + cosmetic.getPrice() + " тг");

            totalPrice = cosmetic.getPrice() * totalQuantity;
        }

        // Handle add and remove quantity actions
        addItem.setOnClickListener(v -> {
            if (totalQuantity < 10) {
                totalQuantity++;
                quantity.setText(String.valueOf(totalQuantity));
                totalPrice = cosmetic.getPrice() * totalQuantity;
            }
        });

        removeItem.setOnClickListener(v -> {
            if (totalQuantity > 1) {
                totalQuantity--;
                quantity.setText(String.valueOf(totalQuantity));
                totalPrice = cosmetic.getPrice() * totalQuantity;
            }
        });
        addToCart = findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addedToCart();
            }
        });
    }

    private void addedToCart() {
        String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("productName", cosmetic.getName());
        cartMap.put("productPrice", price.getText().toString());
        cartMap.put("currentDate", saveCurrentDate);
        cartMap.put("currentTime", saveCurrentTime);
        cartMap.put("totalQuantity", quantity.getText().toString());
        cartMap.put("totalPrice", totalPrice);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("AddToCart").add(cartMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(DetailedActivity.this, "Добавлено в корзину", Toast.LENGTH_SHORT).show();
                        finish();  // Close the activity after adding to cart
                    }
                });
    }
}
