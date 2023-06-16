package com.mobiledevelopment.feature.customer.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.mobiledevelopment.R;
import com.mobiledevelopment.core.repository.UserRepository;
import com.mobiledevelopment.databinding.FragmentProfileBinding;
import com.mobiledevelopment.feature.customer.CustomerActivityViewModel;

public class ProfileFragment extends Fragment {

    public final String TAG = "ProfileFragment";
    private ProfileViewModel viewModel;
    private FragmentProfileBinding binding;
    private String currentUserId;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(
                this,
                new ProfileViewModel.ProfileViewModelFactory(new UserRepository())).get(
                ProfileViewModel.class);

        CustomerActivityViewModel customerActivityViewModel =
                new ViewModelProvider(requireActivity()).get(CustomerActivityViewModel.class);

        customerActivityViewModel.getCurrentUserId()
                .observe(getViewLifecycleOwner(), userId -> viewModel.setCurrentUserId(userId));

        setUpUi();
        observeViewModel();
    }

    private void setUpUi() {
        binding.itemProfileAddress.setOnClickListener((view ->
                Navigation
                        .findNavController(view)
                        .navigate(ProfileFragmentDirections.actionProfileFragmentToUserAddressFragment(
                                currentUserId))));
        binding.itemProfilePromos.setOnClickListener((view -> {
            //TODO: Navigate to Promos Screen
        }));
        binding.itemProfileVouchers.setOnClickListener((view ->
                Navigation
                        .findNavController(view)
                        .navigate(ProfileFragmentDirections.actionProfileFragmentToVoucherFragment(
                                currentUserId))));
        binding.buttonEditProfile.setOnClickListener((view ->
                Navigation
                        .findNavController(view)
                        .navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(
                                currentUserId))));

        binding.textviewLogout.setOnClickListener((view) -> viewModel.logOut());
    }

    private void observeViewModel() {
        viewModel.currentUser.observe(getViewLifecycleOwner(), (user) -> {
            currentUserId = user.getId();
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
        viewModel.clearSnapshotListeners();
    }
}