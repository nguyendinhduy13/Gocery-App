package com.mobiledevelopment.core.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class AuthenticationRepository {
    public static String TAG = "AuthenticationRepository";

    public void observeAuthState(OnAuthStateChangeListener onAuthStateChangeListener) {
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null) {
                onAuthStateChangeListener.onUserNotSignedIn();
            } else {
                onAuthStateChangeListener.onUserSignedIn(currentUser.getUid());
            }
        });
    }

    public void observeNewToken(OnTokenChangedListener onTokenChangedListener){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        onTokenChangedListener.onTokenChanged(token);
                    }
                });
    }

    public interface OnAuthStateChangeListener {
        void onUserSignedIn(String currentUserId);

        void onUserNotSignedIn();
    }

    public interface OnTokenChangedListener{
        void onTokenChanged(String newToken);
    }
}
