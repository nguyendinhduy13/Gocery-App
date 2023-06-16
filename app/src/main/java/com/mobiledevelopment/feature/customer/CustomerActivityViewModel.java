package com.mobiledevelopment.feature.customer;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.ListenerRegistration;
import com.mobiledevelopment.core.models.CartTest;
import com.mobiledevelopment.core.repository.AuthenticationRepository;
import com.mobiledevelopment.core.repository.CartRepository;

import java.util.ArrayList;
import java.util.List;

public class CustomerActivityViewModel extends ViewModel {
    private static final String TAG = "CustomerActivityVModel";

    private final MutableLiveData<Boolean> isUserSignedIn = new MutableLiveData<>(true);
    private final CartRepository cartRepository = new CartRepository();
    private final MutableLiveData<CartTest> _currentCart = new MutableLiveData<>();
    final LiveData<CartTest> currentCart = _currentCart;
    private final MutableLiveData<String> currentUserId = new MutableLiveData<>();

    private final List<ListenerRegistration> snapshotListeners = new ArrayList<>();

    public CustomerActivityViewModel() {
        new AuthenticationRepository().observeAuthState(new AuthenticationRepository.OnAuthStateChangeListener() {
            @Override
            public void onUserSignedIn(String currentUserId) {
                isUserSignedIn.postValue(true);
                ListenerRegistration snapshotRegistration = cartRepository.getCartStreamByUserId(
                        currentUserId,
                        _currentCart::postValue);
                snapshotListeners.add(snapshotRegistration);
            }

            @Override
            public void onUserNotSignedIn() {
                isUserSignedIn.postValue(false);
            }
        });
    }

    public LiveData<String> getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String userId) {
        currentUserId.postValue(userId);
    }

    public LiveData<Boolean> IsUserSignedIn() {
        return isUserSignedIn;
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
}