package com.mobiledevelopment.feature.customer.product.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mobiledevelopment.R;
import com.mobiledevelopment.databinding.SortingProductBottomSheetBinding;

import java.util.Objects;

public class SortingBottomSheetDialog extends BottomSheetDialogFragment {

    private int currentType = -1;
    private SortingProductBottomSheetBinding bindingBottomSheet;
    private OnActionCompleteListener listener;

    public SortingBottomSheetDialog(OnActionCompleteListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.SortingBottomSheet);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bindingBottomSheet = SortingProductBottomSheetBinding.inflate(inflater, container, false);
        return bindingBottomSheet.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setAndExitAnimation();
        setOnClickListener();
    }

    private void setAndExitAnimation()
    {
        Objects.requireNonNull(getDialog()).getWindow().setWindowAnimations(R.style.SortingBottomSheetAnimation);
    }
    private void setOnClickListener()
    {
        bindingBottomSheet.popularityTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPopularity();
                currentType = 1;
            }
        });

        bindingBottomSheet.highestPriceTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkHighestPrice();
                currentType = 2;
            }
        });

        bindingBottomSheet.lowestPriceTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLowestPrice();
                currentType = 3;
            }
        });

        bindingBottomSheet.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onActionComplete(currentType);
                dismiss();
            }
        });
    }

    private void checkPopularity()
    {
        bindingBottomSheet.popularityCheck.setVisibility(View.VISIBLE);
        bindingBottomSheet.highestPriceCheck.setVisibility(View.INVISIBLE);
        bindingBottomSheet.lowestPriceCheck.setVisibility(View.INVISIBLE);
    }

    private void checkLowestPrice()
    {
        bindingBottomSheet.lowestPriceCheck.setVisibility(View.VISIBLE);
        bindingBottomSheet.popularityCheck.setVisibility(View.INVISIBLE);
        bindingBottomSheet.highestPriceCheck.setVisibility(View.INVISIBLE);
    }

    private void checkHighestPrice()
    {
        bindingBottomSheet.highestPriceCheck.setVisibility(View.VISIBLE);
        bindingBottomSheet.popularityCheck.setVisibility(View.INVISIBLE);
        bindingBottomSheet.lowestPriceCheck.setVisibility(View.INVISIBLE);
    }

    interface OnActionCompleteListener {
        void onActionComplete(int type);
    }
}
