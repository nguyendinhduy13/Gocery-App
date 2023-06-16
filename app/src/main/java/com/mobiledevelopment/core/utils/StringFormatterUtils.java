package com.mobiledevelopment.core.utils;

import android.util.Log;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public final class StringFormatterUtils {
    private StringFormatterUtils() {
        throw new AssertionError();
    }

    public static String format(Date date) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.CHINA).format(date);
    }

    public static String toCurrency(Long amount) {
        Locale locale = new Locale.Builder().setLanguage("vi").setRegion("VN").build();
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        return currencyFormatter.format(amount);
    }
    public static String toCurrency(Double amount) {
        Long parseAmount = Math.round(amount);
        Locale locale = new Locale.Builder().setLanguage("vi").setRegion("VN").build();
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
        return currencyFormatter.format(parseAmount);
    }

    public static Long VNDToLong(String amount) {
        String replace = amount.replace("₫", "").replace(".", "").replaceAll("\\s+", "");
        Log.d("test amount", replace);
        return Long.parseLong(replace);
    }

}
