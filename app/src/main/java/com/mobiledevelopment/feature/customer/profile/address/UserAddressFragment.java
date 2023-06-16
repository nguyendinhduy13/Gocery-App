package com.mobiledevelopment.feature.customer.profile.address;

import static com.mobiledevelopment.feature.customer.profile.address.AddEditAddressViewModel.NON_EXISTENT_ADDRESS_ID;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mobiledevelopment.R;
import com.mobiledevelopment.core.repository.AddressRepository;
import com.mobiledevelopment.databinding.FragmentUserAddressBinding;

public class UserAddressFragment extends Fragment {

    public final String TAG = "UserAddressFragment";
    private UserAddressViewModel viewModel;
    private FragmentUserAddressBinding binding;
    private AddressAdapter addressAdapter;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentUserAddressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(
                this,
                new UserAddressViewModel.UserAddressViewModelFactory(new AddressRepository())).get(
                UserAddressViewModel.class);

        viewModel.setCurrentUserId(requireArguments().getString("currentUserId"));

        setUpUi();
        observeViewModel();
    }

    private void setUpUi() {
        addressAdapter = new AddressAdapter(getContext(), (addressId) -> Navigation
                .findNavController(requireView())
                .navigate(UserAddressFragmentDirections.actionUserAddressFragmentToAddEditAddressFragment(
                        addressId, viewModel.getCurrentUserId())));
        binding.recyclerviewAddresses.setAdapter(addressAdapter);
        binding.recyclerviewAddresses.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).popBackStack();
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation
                        .findNavController(requireView())
                        .navigate(UserAddressFragmentDirections.actionUserAddressFragmentToAddEditAddressFragment(
                                NON_EXISTENT_ADDRESS_ID, viewModel.getCurrentUserId()));
            }
        });

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.app_bar_user_address, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.menu_item_add_address) {
                    Navigation
                            .findNavController(requireView())
                            .navigate(UserAddressFragmentDirections.actionUserAddressFragmentToAddEditAddressFragment(
                                    NON_EXISTENT_ADDRESS_ID, viewModel.getCurrentUserId()));

                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

    }

    private void observeViewModel() {
        viewModel.addresses.observe(getViewLifecycleOwner(), (addresses) -> {
            addressAdapter.submitList(null);
            addressAdapter.submitList(addresses);
            Log.d(
                    TAG,
                    "viewModel.userAddresses.observe() - addresses: " + addresses);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
        viewModel.clearSnapshotListeners();
    }
}