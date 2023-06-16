package com.mobiledevelopment.feature.customer.order;

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
import com.mobiledevelopment.feature.customer.product.adapter.CartReviewAdapter;

import java.util.List;
import java.util.Map;

public class OrderDetailBottomSheet extends BottomSheetDialogFragment {
    public static String TAG = "OrderDetailDialog";
    private Order order;
    private List<Product.ProductInCart> products;
    private BottomSheetOrderDetailBinding binding;

    public OrderDetailBottomSheet(Order order, List<Product.ProductInCart> products) {
        this.order = order;
        this.products = products;
        Log.d(TAG, "OrderDetailBottomSheet() - order: " + order);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = BottomSheetOrderDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.textviewEmail.setText(order.getEmail());
        binding.textviewCreatedAt.setText(StringFormatterUtils.format(order.getCreatedAt()));
        binding.textviewAddress.setText(order.getAddress());
        binding.textviewStatus.setText(order.getStatus());
        binding.textviewTotalCost.setText(StringFormatterUtils.toCurrency(order.getTotalCost()));
        binding.recyclerviewProducts.setAdapter(new OrderDetailProductAdapter(products));
        Log.d(TAG, "products: " + products);
    }

    @Override
    public int getTheme() {
        return R.style.CustomBottomSheetDialog;
    }
}
