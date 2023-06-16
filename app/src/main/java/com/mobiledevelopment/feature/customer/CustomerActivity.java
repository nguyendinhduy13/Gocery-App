package com.mobiledevelopment.feature.customer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobiledevelopment.R;
import com.mobiledevelopment.databinding.ActivityCustomerBinding;
import com.mobiledevelopment.feature.authentication.AuthenticationActivity;
import com.mobiledevelopment.feature.customer.checkout.CheckOut1Fragment;

import java.util.Objects;

public class CustomerActivity extends AppCompatActivity {
    public static final String EXTRA_CURRENT_USER_ID = "EXTRA_CURRENT_USER_ID";
    private CustomerActivityViewModel viewModel;
    private final AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
            R.id.shopFragment, R.id.cartFragment, R.id.orderFragment, R.id.profileFragment)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(
                CustomerActivityViewModel.class);

        String currentUserId = getIntent().getStringExtra(EXTRA_CURRENT_USER_ID);
        Log.d("CustomerActivity", "currentUserId: " + currentUserId);
        viewModel.setCurrentUserId(currentUserId);
        viewModel.IsUserSignedIn().observe(this, isUserSignedIn -> {
            if (!isUserSignedIn) {
                startActivity(new Intent(this, AuthenticationActivity.class));
                finish();
            }
        });

        ActivityCustomerBinding binding = ActivityCustomerBinding.inflate(
                getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(
                R.id.customer_fragment_container);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        BottomNavigationView bottomNavigationView = binding.bottomNavView;
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        viewModel.currentCart.observe(this, cart -> {
            if (cart.getCart().isEmpty()) {
                bottomNavigationView.removeBadge(R.id.cartFragment);
            }
            else {
                bottomNavigationView.getOrCreateBadge(R.id.cartFragment)
                        .setNumber(cart.getCart().size());
            }
        });

        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (appBarConfiguration.getTopLevelDestinations().contains(navDestination.getId())) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
            else {
                bottomNavigationView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.clearSnapshotListeners();
    }
}