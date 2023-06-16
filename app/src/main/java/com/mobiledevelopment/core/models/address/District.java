package com.mobiledevelopment.core.models.address;

import androidx.annotation.NonNull;

import java.util.List;

public class District {
    private final String name;

    private final int code;

    public District(String name, int code) {
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
    public static class DistrictJsonWrapper {
        private final List<District> districts;

        public DistrictJsonWrapper(List<District> districts) {
            this.districts = districts;
        }

        public List<District> getDistricts() {
            return districts;
        }
    }
}