package com.mobiledevelopment.feature.customer.checkout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.mobiledevelopment.core.models.Voucher;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ChooseVoucherDialog extends DialogFragment {

    public static String TAG = "ChooseVoucherDialog";
    private final List<Voucher> vouchers;
    private final OnChooseVoucherListener onChooseVoucherListener;

    public ChooseVoucherDialog(
            List<Voucher> vouchers,
            OnChooseVoucherListener onChooseVoucherListener) {
        this.vouchers = vouchers;
        this.onChooseVoucherListener = onChooseVoucherListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AtomicReference<Integer> index = new AtomicReference<>(0);
        CharSequence[] items = vouchers.stream().map(voucher -> {
            String item;
            item = voucher.toFullName();
            return item;
        }).collect(Collectors.toList()).toArray(new CharSequence[vouchers.size()]);
        Log.d(TAG, "items" + Arrays.toString(items));
        Log.d(TAG, "vouchers" + vouchers);

        return new AlertDialog.Builder(requireContext())
                .setTitle("Choose voucher")
                .setSingleChoiceItems(
                        items,
                        index.get(),
                        (dialogInterface, i) -> {
                            index.set(i);
                        })
                .setNegativeButton("Cancel", (dialog, which) -> {this.dismiss();})
                .setPositiveButton(
                        "Confirm",
                        (dialog, which) -> {
                            onChooseVoucherListener.onChooseVoucher(vouchers.get(index.get()));
                        })
                .create();
    }

    public interface OnChooseVoucherListener {
        void onChooseVoucher(Voucher voucher);
    }
}


