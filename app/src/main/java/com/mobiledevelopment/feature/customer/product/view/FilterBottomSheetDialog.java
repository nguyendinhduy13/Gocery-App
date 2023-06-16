package com.mobiledevelopment.feature.customer.product.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mobiledevelopment.R;
import com.mobiledevelopment.databinding.FilterProductBottomSheetBinding;


import java.util.Objects;

public class FilterBottomSheetDialog extends BottomSheetDialogFragment {
    private int currentType = -1;
    private FilterProductBottomSheetBinding bindingBottomSheet;
    private FilterBottomSheetDialog.OnActionCompleteListener listener;

    public FilterBottomSheetDialog(FilterBottomSheetDialog.OnActionCompleteListener listener) {
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
        bindingBottomSheet = FilterProductBottomSheetBinding.inflate(inflater, container, false);
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
        bindingBottomSheet.lowTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkLow();
                currentType = 1;
            }
        });

        bindingBottomSheet.mediumTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkMedium();
                currentType = 2;
            }
        });

        bindingBottomSheet.highTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkHigh();
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

    private void checkLow()
    {
        bindingBottomSheet.lowCheck.setVisibility(View.VISIBLE);
        bindingBottomSheet.highCheck.setVisibility(View.INVISIBLE);
        bindingBottomSheet.mediumCheck.setVisibility(View.INVISIBLE);
    }

    private void checkMedium()
    {
        bindingBottomSheet.mediumCheck.setVisibility(View.VISIBLE);
        bindingBottomSheet.lowCheck.setVisibility(View.INVISIBLE);
        bindingBottomSheet.highCheck.setVisibility(View.INVISIBLE);
    }

    private void checkHigh()
    {
        bindingBottomSheet.highCheck.setVisibility(View.VISIBLE);
        bindingBottomSheet.lowCheck.setVisibility(View.INVISIBLE);
        bindingBottomSheet.mediumCheck.setVisibility(View.INVISIBLE);
    }

    interface OnActionCompleteListener {
        void onActionComplete(int type);
    }
}
