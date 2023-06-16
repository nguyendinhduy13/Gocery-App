package com.mobiledevelopment.feature.customer.profile;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.ListenerRegistration;
import com.mobiledevelopment.core.models.User;
import com.mobiledevelopment.core.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileViewModel extends ViewModel {

    public static final String TAG = "ProfileViewModel";
    private final UserRepository userRepository;

    private final MutableLiveData<User> _currentUser = new MutableLiveData<>();
    public final LiveData<User> currentUser = _currentUser;

    private final MutableLiveData<String> _currentUserId = new MutableLiveData<>("");

    private final List<ListenerRegistration> snapshotListeners = new ArrayList<>();

    public ProfileViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setCurrentUserId(String id) {
        if (!Objects.equals(_currentUserId.getValue(), id)) {
            _currentUserId.setValue(id);
            resetCurrentUser();
        }
        //Log.d(
        //        TAG,
        //        "setCurrentUserId() - inputId: " + id + "; _currentUserId.value = " + _currentUserId.getValue());
    }

    private void resetCurrentUser() {
        ListenerRegistration snapshotRegistration = userRepository.getUserStreamById(
                _currentUserId.getValue(),
                _currentUser::setValue);
        snapshotListeners.add(snapshotRegistration);
        Log.d(TAG, "resetCurrentUser() - _currentUser.value = " + _currentUser.getValue());
    }

    public void clearSnapshotListeners() {
        //Log.d(
        //        TAG,
        //        "clearSnapshotListeners() - snapshotListeners.size = " + snapshotListeners.size());
        for (ListenerRegistration listener : snapshotListeners) {
            listener.remove();
        }
        snapshotListeners.clear();
    }

    public void logOut() {
        userRepository.logOut();
    }

    public static class ProfileViewModelFactory implements ViewModelProvider.Factory {
        private final UserRepository userRepository;

        public ProfileViewModelFactory(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
                @SuppressWarnings("unchecked")
                T result = (T) new ProfileViewModel(userRepository);
                return result;
            }
            throw new IllegalArgumentException(
                    "ProfileViewModelFactory.create() - Unknown ViewModel class: " + modelClass);
        }
    }
}