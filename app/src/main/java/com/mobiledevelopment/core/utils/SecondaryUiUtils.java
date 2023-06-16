package com.mobiledevelopment.core.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public final class SecondaryUiUtils {
    private SecondaryUiUtils() {
        throw new AssertionError();
    }

    public static void showBasicDialog(
            Context context,
            String title,
            String message,
            String positiveButtonText,
            String negativeButtonText,
            DialogInterface.OnClickListener onPositiveButtonClick,
            DialogInterface.OnClickListener onNegativeButtonClick
    ) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, onPositiveButtonClick)
                .setNegativeButton(negativeButtonText, onNegativeButtonClick)
                .create()
                .show();
    }

    public static void showToast(Context context, String toastText) {
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
    }
}
