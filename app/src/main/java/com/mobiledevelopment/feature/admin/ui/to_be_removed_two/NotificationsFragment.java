package com.mobiledevelopment.feature.admin.ui.to_be_removed_two;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.mobiledevelopment.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobiledevelopment.core.repository.UserRepository;
import com.mobiledevelopment.databinding.FragmentNotificationsBinding;
import com.mobiledevelopment.feature.customer.profile.ProfileViewModel;


public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private ProfileViewModel viewModel;

    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        viewModel = new ViewModelProvider(
                this,
                new ProfileViewModel.ProfileViewModelFactory(new UserRepository())).get(
                ProfileViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.logOut();
            }
        });
        binding.btnAddVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_navigation_notifications_to_addVoucherFragment);
            }
        });
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.currentUser.observe(getViewLifecycleOwner(), (user) -> {
            binding.textviewUsername.setText(user.getUsername());
            binding.textviewEmail.setText(user.getEmail());

            Glide
                    .with(this)
                    .load(user.getAvatar())
                    .placeholder(R.drawable.ic_user)
                    .centerCrop()
                    .into(binding.imageviewAvatar);
            //Log.d(TAG, "viewModel.currentUser.observe() - currentUser: " + user);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}