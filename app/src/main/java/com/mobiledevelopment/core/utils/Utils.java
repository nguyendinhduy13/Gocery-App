package com.mobiledevelopment.core.utils;

import android.content.Context;

import vn.thanguit.toastperfect.ToastPerfect;

public class Utils {
    public static void showToast(Context context, int type, String content){
        ToastPerfect.makeText(context, type, content, ToastPerfect.BOTTOM, ToastPerfect.LENGTH_SHORT).show();
    }
}
