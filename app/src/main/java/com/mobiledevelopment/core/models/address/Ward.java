package com.mobiledevelopment.core.models.address;

import androidx.annotation.NonNull;

import java.util.List;

public class Ward {
    private final String name;

    private final int code;

    public Ward(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    @NonNull
    @Override
    public String toString() {
        return "name: " + name + "; code: " + code;
    }

    /**
     * Json returned from address API when querying by provinceCode is an object, not array of
     * objects, so this serves as a wrapper
     */
    public static class WardJsonWrapper {
        private final List<Ward> wards;

        public WardJsonWrapper(List<Ward> wards) {
            this.wards = wards;
        }

        public List<Ward> getWards() {
            return wards;
        }
    }
}
