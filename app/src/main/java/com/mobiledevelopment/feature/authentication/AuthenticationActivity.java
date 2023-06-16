package com.mobiledevelopment.feature.authentication;

import static com.mobiledevelopment.feature.authentication.AuthenticationActivityViewModel.UiState.LOADING;
import static com.mobiledevelopment.feature.authentication.AuthenticationActivityViewModel.UiState.USER_SIGNED_IN;
import static com.mobiledevelopment.feature.customer.CustomerActivity.EXTRA_CURRENT_USER_ID;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.mobiledevelopment.core.models.User;
import com.mobiledevelopment.core.repository.AuthenticationRepository;
import com.mobiledevelopment.core.repository.UserRepository;
import com.mobiledevelopment.databinding.ActivityAuthenticationBinding;
import com.mobiledevelopment.feature.admin.AdminActivity;
import com.mobiledevelopment.feature.customer.CustomerActivity;

public class AuthenticationActivity extends AppCompatActivity {

    private static final String TAG = "AuthenticationActivity";
    private AuthenticationActivityViewModel viewModel;
    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Configure offline persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);

        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> isLoading);

        super.onCreate(savedInstanceState);
        FirebaseFirestore.getInstance().setFirestoreSettings(
                new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build());

        ActivityAuthenticationBinding binding = ActivityAuthenticationBinding.inflate(
                getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(
                this,
                new AuthenticationActivityViewModel.AuthenticationActivityViewModelFactory(
                        new AuthenticationRepository(),
                        new UserRepository())).get(AuthenticationActivityViewModel.class);

        viewModel.uiState.observe(this, uiState -> {
            if (uiState == USER_SIGNED_IN) {
                User currentUser = viewModel.getCurrentUser();

                viewModel.updateUserToken(currentUser,viewModel.token.getValue());

                switch (currentUser.getRoleId()) {
                    case User.ROLE_ID_CUSTOMER:
                        Intent intent = new Intent(this, CustomerActivity.class);
                        intent.putExtra(
                                EXTRA_CURRENT_USER_ID,
                                currentUser.getId());
                        startActivity(intent);
                        finish();
                        return;

                    case User.ROLE_ID_ADMIN:
                        startActivity(new Intent(this, AdminActivity.class));
                        finish();
                        return;

                    default:
                        Log.e(TAG, "viewModel.isUserSignedIn.observe() - Unknown User role:" +
                                viewModel.getCurrentUser().getRoleId());
                }
            }
            this.isLoading = uiState == LOADING;
        });

        viewModel.token.observe(this, newToken -> {
        });
    }
}