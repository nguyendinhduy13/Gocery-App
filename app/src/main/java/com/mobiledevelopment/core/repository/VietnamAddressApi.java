package com.mobiledevelopment.core.repository;

import com.mobiledevelopment.core.models.address.District;
import com.mobiledevelopment.core.models.address.Province;
import com.mobiledevelopment.core.models.address.Ward;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface VietnamAddressApi {
    @GET("p")
    Call<List<Province>> getAllProvinces();

    @GET("p/{provinceCode}?depth=2")
    Call<District.DistrictJsonWrapper> getDistrictsByProvinceCode(@Path("provinceCode") Integer provinceCode);

    @GET("d/{districtCode}?depth=2")
    Call<Ward.WardJsonWrapper> getWardsByDistrictCode(@Path("districtCode") Integer districtCode);
}

