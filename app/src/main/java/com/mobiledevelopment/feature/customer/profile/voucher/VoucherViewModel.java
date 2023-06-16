package com.mobiledevelopment.feature.customer.profile.voucher;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.ListenerRegistration;
import com.mobiledevelopment.core.models.Voucher;
import com.mobiledevelopment.core.repository.VoucherRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VoucherViewModel extends ViewModel {
    public static final String TAG = "VoucherViewModel";

    private final VoucherRepository voucherRepository;
    private final MutableLiveData<String> _currentUserId = new MutableLiveData<>("");

    private final MutableLiveData<List<Voucher>> _vouchers = new MutableLiveData<>();
    public final LiveData<List<Voucher>> vouchers = _vouchers;

    private final List<ListenerRegistration> snapshotListeners = new ArrayList<>();

    public VoucherViewModel(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    public void setCurrentUserId(String id) {
        if (!Objects.equals(_currentUserId.getValue(), id)) {
            _currentUserId.setValue(id);
            resetVouchers();
        }
        Log.d(
                TAG,
                "setCurrentUserId() - inputId: " + id + "; _currentUserId.value = " + _currentUserId.getValue());
    }

    private void resetVouchers() {
        ListenerRegistration snapshotRegistration = voucherRepository.getVouchersStreamByUserId(
                _currentUserId.getValue(),
                _vouchers::setValue);
        snapshotListeners.add(snapshotRegistration);

        Log.d(
                TAG,
                "resetVouchers() - _vouchers.value = " + _vouchers.getValue());
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

    public static class VoucherViewModelFactory implements ViewModelProvider.Factory {
        private final VoucherRepository voucherRepository;

        public VoucherViewModelFactory(VoucherRepository voucherRepository) {
            this.voucherRepository = voucherRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new VoucherViewModel(voucherRepository);
        }
    }
}