package com.mobiledevelopment.feature.authentication.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;
import androidx.navigation.Navigation;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobiledevelopment.BR;
import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.User;

public class OnboardingCTViewModel extends BaseObservable {

    @Bindable
    public CallbackManager callbackManager;

    private FirebaseFirestore db;

    @Bindable
    public static final int RC_SIGN_IN = 9001;

    @Bindable
    public ObservableField<Boolean> isActivityGoogleLogin = new ObservableField<>();

    @Bindable
    public ObservableField<Boolean> isActivityFacebookLogin = new ObservableField<>();

    @Bindable
    public ObservableField<Boolean> isNavigationtoLogin = new ObservableField<>();

    @Bindable
    public ObservableField<Boolean> isNavigationtoSignUp = new ObservableField<>();

    @Bindable
    public ObservableField<Boolean> isUpdateFBUI = new ObservableField<>();


    @Bindable
    public FirebaseAuth mAuth;


    public OnboardingCTViewModel() {
        this.mAuth = FirebaseAuth.getInstance();
        this.callbackManager = CallbackManager.Factory.create();
        this.db = FirebaseFirestore.getInstance();
    }


    public void setActivityGoogleLogin(boolean value) {
        isActivityGoogleLogin.set(value);
        notifyPropertyChanged(BR.isActivityGoogleLogin);
    }

    public void setIsNavigationtoLogin(boolean value) {
        isNavigationtoLogin.set(value);
        notifyPropertyChanged(BR.isNavigationtoLogin);
    }

    public void setIsNavigationtoSignUp(boolean value) {
        isNavigationtoSignUp.set(value);
        notifyPropertyChanged(BR.isNavigationtoSignUp);
    }

    public void setIsUpdateFBUI(boolean value) {
        isUpdateFBUI.set(value);
        notifyPropertyChanged(BR.isUpdateFBUI);
    }

    public void setIsActivityFacebookLogin(boolean value) {
        isActivityFacebookLogin.set(value);
        notifyPropertyChanged(BR.isActivityFacebookLogin);
    }

    public void OnClickSignInGoogle() {
        setActivityGoogleLogin(true);
    }

    public void OnClickNavigateToLogin() {
        setIsNavigationtoLogin(true);
    }

    public void OnClickNavigateToSignUp() {
        setIsNavigationtoSignUp(true);
    }


    public void updateUIFB(FirebaseUser user) {
        if (user != null) {
            setIsUpdateFBUI(true);
        } else {
            return;
        }
    }

    public void OnClickLoginFacebook() {
        setIsActivityFacebookLogin(true);
    }

    public void firebaseAuthWithGoogle(Activity activity, String idToken, View getView) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user.isEmailVerified()) {
                                Navigation.findNavController(getView)
                                        .navigate(R.id.action_onBoardingCT_to_shopFragment);
                                SharedPreferences sharedPreferences = activity.getSharedPreferences(
                                        "sharedPrefs",
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("user", "true");
                                editor.apply();
                            } else {
                                return;
                            }
                            User user1 = new User(user.getUid(), user.getEmail(),
                                    user.getDisplayName(),
                                    "",
                                    user.getDisplayName(),
                                    user.getPhoneNumber(),
                                    "",
                                    "01/01/2000",
                                    "1",
                                    0L,
                                    user.getPhotoUrl().toString());
                            db.collection("User").document(mAuth.getCurrentUser().getUid())
                                    .set(user1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(
                                                    "hello",
                                                    "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("hello", "Error writing document", e);
                                        }
                                    });
                        } else {
                            return;
                        }
                    }
                });
    }

    public void handleFacebookAccessToken(Activity activity, AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUIFB(user);
                            User user1 = new User(user.getUid(), user.getEmail(),
                                    user.getDisplayName(),
                                    "",
                                    user.getDisplayName(),
                                    user.getPhoneNumber(),
                                    "",
                                    "01/01/2000",
                                    "1",
                                    0L,
                                    user.getPhotoUrl().toString());
                            db.collection("User").document(mAuth.getCurrentUser().getUid())
                                    .set(user1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(
                                                    "hello",
                                                    "DocumentSnapshot successfully written!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("hello", "Error writing document", e);
                                        }
                                    });
                        } else {
                            updateUIFB(null);
                        }
                    }
                });
    }
}
