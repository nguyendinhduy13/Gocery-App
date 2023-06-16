package com.mobiledevelopment.feature.customer.profile.voucher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.Voucher;
import com.mobiledevelopment.core.utils.StringFormatterUtils;
import com.mobiledevelopment.databinding.FragmentVoucherDetailBinding;

public class VoucherDetailFragment extends BottomSheetDialogFragment {

    public static final String TAG = "VoucherDetailFragment";
    private final Voucher voucher;

    public VoucherDetailFragment() {
        voucher = Voucher.undefinedVoucher;
    }

    public VoucherDetailFragment(Voucher voucher) {
        this.voucher = voucher;
    }

    private FragmentVoucherDetailBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentVoucherDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.textviewVoucherName.setText(voucher.toFullName());
        binding.textviewDateExpired.setText(StringFormatterUtils.format(voucher.getDateExpired()));
        binding.textviewDescription.setText(voucher.getDescription());
        binding.buttonClose.setOnClickListener((view1 -> this.dismiss()));
    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialog;
    }
}

