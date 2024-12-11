package com.example.yesface.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yesface.R;
import com.example.yesface.model.MyCartModel;
import com.example.yesface.adapter.MyCartAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BuyFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;

    RecyclerView recyclerView;
    MyCartAdapter cartAdapter;
    List<MyCartModel> cartModelList;
    TextView overTotalAmount;
    Button buyNow;

    public BuyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_buy, container, false);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        buyNow = root.findViewById(R.id.but_now);
        recyclerView = root.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        overTotalAmount = root.findViewById(R.id.textView4);
        cartModelList = new ArrayList<>();
        cartAdapter = new MyCartAdapter(getActivity(), cartModelList);
        recyclerView.setAdapter(cartAdapter);

        // Fetch cart items from Firestore
        db.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("AddToCart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                String documentId = documentSnapshot.getId();
                                MyCartModel cartModel = documentSnapshot.toObject(MyCartModel.class);

                                if (cartModel != null) {
                                    cartModel.setDocumentId(documentId);
                                    cartModelList.add(cartModel);
                                }
                            }
                            cartAdapter.notifyDataSetChanged();
                            calculateTotalAmount(cartModelList);
                        }
                    }
                });

        // Handle "Buy Now" button click
        buyNow.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Заказ успешно выполнен", Toast.LENGTH_SHORT).show();

            // Move cart items to orders collection
            for (MyCartModel cartModel : cartModelList) {
                final HashMap<String, Object> cartMap = new HashMap<>();
                cartMap.put("productName", cartModel.getProductName());
                cartMap.put("productPrice", cartModel.getProductPrice());
                cartMap.put("currentDate", cartModel.getCurrentDate());
                cartMap.put("currentTime", cartModel.getCurrentTime());
                cartMap.put("totalQuantity", cartModel.getTotalQuantity());
                cartMap.put("totalPrice", cartModel.getTotalPrice());

                db.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("MyOrder").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Твой заказ был сделан", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                // Delete items from the cart after the order is placed
                db.collection("CurrentUser")
                        .document(auth.getCurrentUser().getUid())
                        .collection("AddToCart")
                        .document(cartModel.getDocumentId())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    cartModelList.remove(cartModel); // Remove item from cart list
                                    cartAdapter.notifyDataSetChanged(); // Notify adapter about data change
                                }
                            }
                        });
            }
        });

        return root; // Return the root view
    }

    // Calculate total amount in cart
    public void calculateTotalAmount(List<MyCartModel> cartModelList) {
        double totalAmount = 0.0;
        for (MyCartModel myCartModel : cartModelList) {
            totalAmount += myCartModel.getTotalPrice();
        }
        overTotalAmount.setText("Общая сумма: " + totalAmount);
    }
}
