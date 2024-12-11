package com.example.yesface.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.yesface.R;
import com.example.yesface.fragments.HomeFragment;
import com.example.yesface.fragments.BuyFragment;
import com.example.yesface.fragments.BasketFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set default fragment
        loadFragment(new HomeFragment());

        // Set listener for navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_buy) {
                selectedFragment = new BuyFragment();
            } else if (itemId == R.id.nav_basket) {
                selectedFragment = new BasketFragment();
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }

            return true;
        });
    }

    // Function to replace fragment
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
