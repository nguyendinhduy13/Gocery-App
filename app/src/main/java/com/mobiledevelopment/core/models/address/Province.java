package com.mobiledevelopment.core.models.address;

import androidx.annotation.NonNull;

public class Province {
    private final String name;

    private final int code;

    public Province(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name
                .replace("Thành phố", "")
                .replace("Tỉnh", "")
                .trim();
    }

    public int getCode() {
        return code;
    }

    @NonNull
    @Override
    public String toString() {
        return "name: " + name + "; code: " + code;
    }
}
