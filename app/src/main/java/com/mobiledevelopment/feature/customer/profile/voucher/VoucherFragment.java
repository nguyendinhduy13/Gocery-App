package com.mobiledevelopment.feature.customer.profile.voucher;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mobiledevelopment.core.models.Voucher;
import com.mobiledevelopment.core.repository.VoucherRepository;
import com.mobiledevelopment.databinding.FragmentVoucherBinding;

public class VoucherFragment extends Fragment {

    public final String TAG = "VoucherFragment";
    private VoucherViewModel viewModel;
    private FragmentVoucherBinding binding;
    private VoucherAdapter voucherAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentVoucherBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(
                this,
                new VoucherViewModel.VoucherViewModelFactory(new VoucherRepository())).get(
                VoucherViewModel.class);

        viewModel.setCurrentUserId(requireArguments().getString("currentUserId"));

        setUpUi();
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.vouchers.observe(getViewLifecycleOwner(), (vouchers) -> {
            voucherAdapter.submitList(vouchers);
            Log.d(
                    TAG,
                    "viewModel.vouchers.observe() - vouchers: " + vouchers);
        });
    }

    private void setUpUi() {
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).popBackStack();
            }
        });
        voucherAdapter = new VoucherAdapter(getContext(), new VoucherAdapter.OnClickListener() {
            @Override
            public void onButtonUseClick() {
                //TODO: Add callback for onButtonUseClick to navigate to Cart screen
            }

            @Override
            public void onWholeItemClick(Voucher voucher) {
                //TODO: Add callback to navigate to bottom dialog
                new VoucherDetailFragment(voucher).show(
                        getChildFragmentManager(),
                        VoucherDetailFragment.TAG);
            }
        });

        binding.recyclerviewVouchers.setAdapter(voucherAdapter);
        binding.recyclerviewVouchers.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        viewModel.clearSnapshotListeners();
    }
}