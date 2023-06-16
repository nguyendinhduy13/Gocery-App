package com.mobiledevelopment.feature.customer.profile.address;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mobiledevelopment.core.models.address.Address;
import com.mobiledevelopment.core.models.address.District;
import com.mobiledevelopment.core.models.address.Province;
import com.mobiledevelopment.core.models.address.Ward;
import com.mobiledevelopment.core.repository.AddressRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddEditAddressViewModel extends ViewModel {
    public static final String TAG = "AddEditAddressViewModel";

    /**
     * When used as a nav arg, imply that user is adding a new address
     */
    public static final String NON_EXISTENT_ADDRESS_ID = "NON_EXISTENT_ADDRESS_ID";

    private final AddressRepository addressRepository;

    private final MutableLiveData<AddEditAddressUiState> _uiState = new MutableLiveData<>(new AddEditAddressUiState.Loading());
    public final LiveData<AddEditAddressUiState> uiState = _uiState;
    private final MutableLiveData<String> _currentAddressId = new MutableLiveData<>("");

    private String _currentUserId = "";

    EditMode currentEditMode = EditMode.ADD_ADDRESS;

    private final MutableLiveData<List<Province>> _allProvinces = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<District>> _districts = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Ward>> _wards = new MutableLiveData<>(new ArrayList<>());
    LiveData<List<Province>> allProvinces = _allProvinces;
    LiveData<List<District>> districts = _districts;
    LiveData<List<Ward>> wards = _wards;

    public AddEditAddressViewModel(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
        addressRepository.getAllProvinces().enqueue(new Callback<List<Province>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(
                    @NonNull Call<List<Province>> call,
                    @NonNull Response<List<Province>> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    List<Province> responseProvinces = response.body();
                    responseProvinces.sort(Comparator.comparing(Province::getName));

                    _allProvinces.postValue(responseProvinces);
                    Log.d(TAG, "AddEditAddressViewModel() - getAllProvinces(): " + response.body());
                }
                else {
                    try {
                        assert response.errorBody() != null;
                        Log.d(
                                TAG,
                                "AddEditAddressViewModel() - getAllProvinces() - Error: " + response.errorBody()
                                        .string());
                    } catch (IOException e) {
                        Log.d(
                                TAG,
                                "AddEditAddressViewModel() - getAllProvinces() - Error: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Province>> call, @NonNull Throwable t) {
                Log.d(
                        TAG,
                        "AddEditAddressViewModel() - getAllProvinces() - Failure: " + t.getMessage());
            }
        });
    }

    public void selectProvince(Integer provinceCode) {
        Log.d(TAG, "selectProvince() - provinceCode:" + provinceCode);
        addressRepository.getDistrictsByProvinceCode(provinceCode)
                .enqueue(new Callback<District.DistrictJsonWrapper>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<District.DistrictJsonWrapper> call,
                            @NonNull Response<District.DistrictJsonWrapper> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            _districts.postValue(response.body().getDistricts());
                            Log.d(
                                    TAG,
                                    "AddEditAddressViewModel() - getDistrictsByProvinceCode(): " + response.body()
                                            .toString());
                        }
                        else {
                            try {
                                assert response.errorBody() != null;
                                Log.d(
                                        TAG,
                                        "AddEditAddressViewModel() - getDistrictsByProvinceCode() - Error: " + response.errorBody()
                                                .string());
                            } catch (IOException e) {
                                Log.d(
                                        TAG,
                                        "AddEditAddressViewModel() - getDistrictsByProvinceCode() - Error: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<District.DistrictJsonWrapper> call,
                            @NonNull Throwable t) {
                        Log.d(
                                TAG,
                                "AddEditAddressViewModel() - getDistrictsByProvinceCode() - Failure: " + t.getMessage());
                    }
                });
    }

    public void selectWard(Integer districtCode) {
        Log.d(TAG, "selectWard() - districtCode:" + districtCode);
        addressRepository.getWardsByDistrictCode(districtCode)
                .enqueue(new Callback<Ward.WardJsonWrapper>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<Ward.WardJsonWrapper> call,
                            @NonNull Response<Ward.WardJsonWrapper> response) {
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            _wards.postValue(response.body().getWards());
                            Log.d(
                                    TAG,
                                    "AddEditAddressViewModel() - getWardsByDistrictCode(): " + response.body()
                                            .toString());
                        }
                        else {
                            try {
                                assert response.errorBody() != null;
                                Log.d(
                                        TAG,
                                        "AddEditAddressViewModel() - getWardsByDistrictCode() - Error: " + response.errorBody()
                                                .string());
                            } catch (IOException e) {
                                Log.d(
                                        TAG,
                                        "AddEditAddressViewModel() - getWardsByDistrictCode() - Error: " + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<Ward.WardJsonWrapper> call,
                            @NonNull Throwable t) {
                        Log.d(
                                TAG,
                                "AddEditAddressViewModel() - getWardsByDistrictCode() - Failure: " + t.getMessage());
                    }
                });
    }

    public void setCurrentAddressId(String id) {
        if (!Objects.equals(_currentAddressId.getValue(), id)) {
            if (Objects.equals(id, NON_EXISTENT_ADDRESS_ID)) {
                currentEditMode = EditMode.ADD_ADDRESS;
                _uiState.setValue(new AddEditAddressUiState.InAddMode());
            }
            else {
                _currentAddressId.setValue(id);
                resetCurrentAddress();
                currentEditMode = EditMode.EDIT_ADDRESS;
            }
        }
        Log.d(
                TAG,
                "setCurrentAddressId() - inputId: " + id + "; _currentAddressId.value = " + _currentAddressId.getValue() + "; currentEditMode: " + currentEditMode.name());
    }

    public void setCurrentUserId(String id) {
        _currentUserId = id;
    }

    public void addOrUpdateAddress(Address address) {
        _uiState.setValue(new AddEditAddressUiState.Loading());
        Log.d(TAG, "addOrUpdateAddress() - id: " + address.getId());

        if (currentEditMode == EditMode.ADD_ADDRESS) {
            addressRepository.addAddress(
                    new Address(
                            address.getId(),
                            _currentUserId,
                            address.getDetailAddress(),
                            address.getLabel(),
                            address.isPrimary()),
                    (addressId) -> _uiState.setValue(new AddEditAddressUiState.AddSuccess(addressId)));
        }
        else if (currentEditMode == EditMode.EDIT_ADDRESS) {
            addressRepository.updateAddress(
                    address,
                    () -> _uiState.setValue(new AddEditAddressUiState.EditSuccess()));
        }
    }

    private void resetCurrentAddress() {
        addressRepository.getAddressById(
                _currentAddressId.getValue(),
                (address) -> _uiState.setValue(new AddEditAddressUiState.FetchDataSuccess(new Address.DetailAddress(
                        address))));
        Log.d(
                TAG,
                "resetCurrentAddress() - _currentAddress.value = " + _uiState.getValue());
    }

    enum EditMode {
        EDIT_ADDRESS,
        ADD_ADDRESS
    }

    public static class AddEditAddressViewModelFactory implements ViewModelProvider.Factory {
        private final AddressRepository addressRepository;

        public AddEditAddressViewModelFactory(AddressRepository addressRepository) {
            this.addressRepository = addressRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(AddEditAddressViewModel.class)) {
                @SuppressWarnings("unchecked")
                T result = (T) new AddEditAddressViewModel(addressRepository);
                return result;
            }
            throw new IllegalArgumentException(
                    "AddEditAddressViewModelFactory.create() - Unknown ViewModel class: " + modelClass);
        }
    }
}

abstract class AddEditAddressUiState {
    static class Loading extends AddEditAddressUiState {}

    static class InAddMode extends AddEditAddressUiState {}

    static class FetchDataSuccess extends AddEditAddressUiState {
        private final Address data;

        public FetchDataSuccess(Address data) {
            this.data = data;
        }

        public Address getData() {
            return data;
        }
    }

    static class AddSuccess extends AddEditAddressUiState {
        private final String addressId;

        public AddSuccess(String addressId) {
            this.addressId = addressId;
        }

        public String getData() {
            return addressId;
        }
    }

    static class EditSuccess extends AddEditAddressUiState {}

}
