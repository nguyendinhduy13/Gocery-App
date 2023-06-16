package com.mobiledevelopment.feature.customer.profile.edit;

import android.net.Uri;

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

public class EditProfileViewModel extends ViewModel {
    public static final String TAG = "EditProfileViewModel";

    private final UserRepository userRepository;

    private final MutableLiveData<EditProfileUiState> _uiState = new MutableLiveData<>(
            new EditProfileUiState.Loading());
    public final LiveData<EditProfileUiState> uiState = _uiState;
    private final MutableLiveData<String> _currentUserId = new MutableLiveData<>("");

    private final List<ListenerRegistration> snapshotListeners = new ArrayList<>();

    public EditProfileViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setCurrentUserId(String id) {
        if (!Objects.equals(
                _currentUserId.getValue(),
                id)) {
            _currentUserId.setValue(id);
            resetCurrentUser();
        }
        //Log.d(
        //        TAG,
        //        "setCurrentUserId() - inputId: " + id + "; _currentUserId.value = " + _currentUserId.getValue());
    }

    public void updateUser(User user, Uri avatarUri) {

        //Log.d(TAG, "saveUser() - id: " + user.getId());

        _uiState.setValue(new EditProfileUiState.Loading());

        if (avatarUri.toString().equals(Uri.parse(user.getAvatar()).toString())) {
            userRepository.updateUser(
                    user,
                    () -> _uiState.setValue(new EditProfileUiState.EditSuccess()));
        }
        else {
            userRepository.uploadAvatarImage(
                    user.getId(),
                    avatarUri,
                    resultAvatarUrl -> userRepository.updateUser(
                            new User(
                                    user.getId(),
                                    user.getEmail(),
                                    user.getFullName(),
                                    user.getPassword(),
                                    user.getUsername(),
                                    user.getPhone(),
                                    user.getGender(),
                                    user.getAge(),
                                    user.getRoleId(),
                                    user.getMembershipPoint(),
                                    resultAvatarUrl),
                            () -> _uiState.setValue(new EditProfileUiState.EditSuccess())));
        }

    }

    private void resetCurrentUser() {
        ListenerRegistration snapshotRegistration = userRepository.getUserStreamById(
                _currentUserId.getValue(),
                (user) -> _uiState.setValue(new EditProfileUiState.FetchDataSuccess(user)));
        snapshotListeners.add(snapshotRegistration);
        //Log.d(
        //        TAG,
        //        "resetCurrentUser() - _currentUser.value = " + _currentUser.getValue());
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

    public static class EditProfileViewModelFactory implements ViewModelProvider.Factory {
        private final UserRepository userRepository;

        public EditProfileViewModelFactory(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(EditProfileViewModel.class)) {
                @SuppressWarnings("unchecked")
                T result = (T) new EditProfileViewModel(userRepository);
                return result;
            }
            throw new IllegalArgumentException(
                    "EditProfileViewModelFactory.create() - Unknown ViewModel class: " + modelClass);
        }
    }
}

abstract class EditProfileUiState {
    static class Loading extends EditProfileUiState {}

    static class FetchDataSuccess extends EditProfileUiState {
        private final User data;

        public FetchDataSuccess(User data) {
            this.data = data;
        }

        public User getData() {
            return data;
        }
    }

    static class EditSuccess extends EditProfileUiState {}
}
