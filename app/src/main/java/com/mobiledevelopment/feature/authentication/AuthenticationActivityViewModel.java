package com.mobiledevelopment.feature.authentication;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mobiledevelopment.core.models.User;
import com.mobiledevelopment.core.repository.AuthenticationRepository;
import com.mobiledevelopment.core.repository.UserRepository;

public class AuthenticationActivityViewModel extends ViewModel {
    private final MutableLiveData<UiState> _uiState = new MutableLiveData<>(UiState.LOADING);
    private final MutableLiveData<String> _token = new MutableLiveData<>("");
    public static final String TAG = "AuthenticationActivityViewModel";

    private User currentUser = User.undefinedUser;
    public LiveData<UiState> uiState = _uiState;
    public LiveData<String> token = _token;
    private UserRepository _userRepository;

    public AuthenticationActivityViewModel(
            AuthenticationRepository authenticationRepository,
            UserRepository userRepository) {
        this._userRepository = userRepository;

        authenticationRepository.observeAuthState(new AuthenticationRepository.OnAuthStateChangeListener() {
            @Override
            public void onUserSignedIn(String currentUserId) {
                userRepository.getUserById(
                        currentUserId,
                        user -> {
                            currentUser = user;
                            _uiState.postValue(UiState.USER_SIGNED_IN);
                        });
            }

            @Override
            public void onUserNotSignedIn() {
                _uiState.postValue(UiState.USER_NOT_SIGNED_IN);
            }
        });


        authenticationRepository.observeNewToken(new AuthenticationRepository.OnTokenChangedListener() {
            @Override
            public void onTokenChanged(String newToken) {
                //userRepository.updateUserToken(currentUser,newToken);
                _token.postValue(newToken);
                Log.d("AuthenticationVM",newToken);
            }
        });
    }

    public void updateUserToken(User user, String newToken){
        _userRepository.updateUserToken(user,newToken);
    }

    public enum UiState {
        USER_SIGNED_IN,
        USER_NOT_SIGNED_IN,
        LOADING
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public static class AuthenticationActivityViewModelFactory implements ViewModelProvider.Factory {
        private final AuthenticationRepository authenticationRepository;
        private final UserRepository userRepository;

        public AuthenticationActivityViewModelFactory(
                AuthenticationRepository authenticationRepository,
                UserRepository userRepository) {
            this.authenticationRepository = authenticationRepository;
            this.userRepository = userRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(AuthenticationActivityViewModel.class)) {
                @SuppressWarnings("unchecked")
                T result = (T) new AuthenticationActivityViewModel(
                        authenticationRepository,
                        userRepository);
                return result;
            }
            throw new IllegalArgumentException(
                    "AuthenticationActivityViewModelFactory.create() - Unknown ViewModel class: " + modelClass);
        }
    }
}
