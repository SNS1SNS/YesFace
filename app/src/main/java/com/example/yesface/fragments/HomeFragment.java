package com.example.yesface.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.yesface.R;
import com.example.yesface.adapter.CosmeticAdapter;
import com.example.yesface.model.Cosmetic;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private CosmeticAdapter cosmeticAdapter;
    private List<Cosmetic> cosmeticList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_cosmetics);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the list and adapter
        cosmeticList = new ArrayList<>();
        cosmeticAdapter = new CosmeticAdapter(getContext(), cosmeticList);
        recyclerView.setAdapter(cosmeticAdapter);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize ProgressBar
        progressBar = view.findViewById(R.id.progress_bar); // Ensure this is defined in your XML layout

        // Show the ProgressBar while loading data
        progressBar.setVisibility(View.VISIBLE);

        // Load data from Firestore
        loadCosmeticData();
    }

    private void loadCosmeticData() {
        // Fetch data from Firestore's "cosmetic" collection
        db.collection("cosmetic")
                .get()
                .addOnCompleteListener(task -> {
                    // Hide the loading spinner once data is fetched
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            cosmeticList.clear();
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                // Safely get data from Firestore and create a Cosmetic object
                                String name = documentSnapshot.getString("name");
                                String description = documentSnapshot.getString("description");
                                double price = documentSnapshot.contains("price") ? documentSnapshot.getDouble("price") : 0.0;
                                String imageUrl = documentSnapshot.getString("imageUrl");
                                String rating = documentSnapshot.getString("rating");


                                // Check for missing or invalid data
                                if (name != null && description != null && imageUrl != null) {
                                    Cosmetic cosmetic = new Cosmetic(name, description, price, imageUrl, rating);
                                    cosmeticList.add(cosmetic);
                                }
                            }

                            cosmeticAdapter.notifyDataSetChanged();
                        } else {
                        }
                    } else {
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                });
    }



}
