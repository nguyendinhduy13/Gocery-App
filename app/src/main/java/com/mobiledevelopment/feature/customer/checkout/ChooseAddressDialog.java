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

import com.mobiledevelopment.core.models.address.Address;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ChooseAddressDialog extends DialogFragment {

    public static String TAG = "ChooseAddressDialog";
    private final List<Address> addresses;
    private final OnChooseAddressListener onChooseAddressListener;

    public ChooseAddressDialog(
            List<Address> addresses,
            OnChooseAddressListener onChooseAddressListener) {
        this.addresses = addresses;
        this.onChooseAddressListener = onChooseAddressListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Address primaryAddress = addresses
                .stream().filter(Address::isPrimary)
                .collect(Collectors.toList()).get(0);
        AtomicReference<Integer> index = new AtomicReference<>(addresses.indexOf(primaryAddress));
        CharSequence[] items = addresses.stream().map(address -> {
            String item;
            item = address.getLabel() + "\n" + address.toNormalizedString();
            if (address.isPrimary()) {
                item += " (Primary)";
            }
            return item;
        }).collect(Collectors.toList()).toArray(new CharSequence[addresses.size()]);
        Log.d(TAG, "items" + Arrays.toString(items));
        Log.d(TAG, "addresses" + addresses);

        return new AlertDialog.Builder(requireContext())
                .setTitle("Choose address")
                .setSingleChoiceItems(
                        items,
                        index.get(),
                        (dialogInterface, i) -> {
                            index.set(i);
                            onChooseAddressListener.onChooseAddress(addresses.get(i));
                        })
                .setNegativeButton("Cancel", (dialog, which) -> {this.dismiss();})
                .setPositiveButton("Confirm", (dialog, which) -> {
                    onChooseAddressListener.onChooseAddress(addresses.get(index.get()));
                })
                .create();
    }

    public interface OnChooseAddressListener {
        void onChooseAddress(Address address);
    }
}


