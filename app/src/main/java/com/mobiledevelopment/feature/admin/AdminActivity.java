package com.mobiledevelopment.feature.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobiledevelopment.R;
import com.mobiledevelopment.databinding.ActivityAdminBinding;
import com.mobiledevelopment.feature.authentication.AuthenticationActivity;
import com.mobiledevelopment.feature.customer.CustomerActivityViewModel;

import java.util.Objects;


public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CustomerActivityViewModel viewModel = new ViewModelProvider(this).get(
                CustomerActivityViewModel.class);

        viewModel.IsUserSignedIn().observe(this, isUserSignedIn -> {
            if (!isUserSignedIn) {
                startActivity(new Intent(this, AuthenticationActivity.class));
                finish();
            }
        });

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(
                R.id.admin_fragment_container);
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();

        // Setup App bar
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_category, R.id.navigation_dashboard, R.id.navigation_notifications,R.id.navigation_statistic)
                .build();

        // Setup Bottom Nav
        BottomNavigationView bottomNavigationView = binding.navView;
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (appBarConfiguration.getTopLevelDestinations().contains(navDestination.getId())) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            } else {
                bottomNavigationView.setVisibility(View.GONE);
            }
        });
    }
}