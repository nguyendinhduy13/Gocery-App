package com.mobiledevelopment.feature.customer.profile.address;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.ListenerRegistration;
import com.mobiledevelopment.core.models.address.Address;
import com.mobiledevelopment.core.repository.AddressRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserAddressViewModel extends ViewModel {
    public static final String TAG = "UserAddressViewModel";

    private final AddressRepository addressRepository;

    private final MutableLiveData<String> _currentUserId = new MutableLiveData<>("");

    private final MutableLiveData<List<Address>> _addresses = new MutableLiveData<>();
    public final LiveData<List<Address>> addresses = _addresses;

    private final List<ListenerRegistration> snapshotListeners = new ArrayList<>();

    public UserAddressViewModel(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public void setCurrentUserId(String id) {
        if (!Objects.equals(_currentUserId.getValue(), id)) {
            _currentUserId.setValue(id);
            resetUserAddresses();
        }
        Log.d(
                TAG,
                "setCurrentUserId() - inputId: " + id + "; _currentUserId.value = " + _currentUserId.getValue());
    }

    public String getCurrentUserId() {
        return _currentUserId.getValue();
    }

    private void resetUserAddresses() {
        ListenerRegistration snapshotRegistration = addressRepository.getAddressesStreamByUserId(
                _currentUserId.getValue(),
                _addresses::setValue);
        snapshotListeners.add(snapshotRegistration);

        Log.d(
                TAG,
                "resetUserAddresses() - _addresses.value = " + _addresses.getValue());
    }

    public void clearSnapshotListeners() {
        Log.d(
                TAG,
                "clearSnapshotListeners() - snapshotListeners.size = " + snapshotListeners.size());
        for (ListenerRegistration listener : snapshotListeners) {
            listener.remove();
        }
        snapshotListeners.clear();
    }

    public static class UserAddressViewModelFactory implements ViewModelProvider.Factory {
        private final AddressRepository addressRepository;

        public UserAddressViewModelFactory(AddressRepository addressRepository) {
            this.addressRepository = addressRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new UserAddressViewModel(addressRepository);
        }
    }
}