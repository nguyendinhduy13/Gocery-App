package com.mobiledevelopment.feature.admin.ui.to_be_removed_one.to_be_removed_three;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.Order;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.utils.StringFormatterUtils;
import com.mobiledevelopment.databinding.BottomSheetOrderDetailBinding;
import com.mobiledevelopment.databinding.BottomSheetTransactionDetailBinding;
import com.mobiledevelopment.feature.admin.ui.to_be_removed_one.model.Dashboard;
import com.mobiledevelopment.feature.customer.order.OrderDetailProductAdapter;

import java.util.List;

public class TransactionDetailBottomSheet extends BottomSheetDialogFragment {
    public static String TAG = "TransactionDetailBottomSheet";
    private Dashboard transaction;
    private BottomSheetTransactionDetailBinding binding;

    public TransactionDetailBottomSheet(Dashboard transaction) {
        this.transaction = transaction;

    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = BottomSheetTransactionDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.textviewUsername.setText(transaction.getUserName());
        binding.textviewDate.setText(transaction.getDate());
        binding.textviewStatus.setText(transaction.getStatus());
        binding.textviewTotalPayment.setText(StringFormatterUtils.toCurrency(transaction.getTotalPayment()));
    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialog;
    }
}
