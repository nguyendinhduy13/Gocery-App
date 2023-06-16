package com.mobiledevelopment.feature.authentication.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.mobiledevelopment.BR;
import com.mobiledevelopment.R;
import com.mobiledevelopment.databinding.FragmentOnBoardingCtBinding;
import com.mobiledevelopment.feature.authentication.viewmodel.OnboardingCTViewModel;

public class OnBoardingCT extends Fragment {

    private FragmentOnBoardingCtBinding fragmentOnBoardingCtBinding;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onStart() {
        super.onStart();
        OnboardingCTViewModel onboardingCTViewModel=new OnboardingCTViewModel();
        FirebaseUser currentUser = onboardingCTViewModel.mAuth.getCurrentUser();
        onboardingCTViewModel.updateUIFB(currentUser);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        AppEventsLogger.activateApp(getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentOnBoardingCtBinding= FragmentOnBoardingCtBinding.inflate(inflater,container,false);
        OnboardingCTViewModel onboardingCTViewModel=new OnboardingCTViewModel();
        fragmentOnBoardingCtBinding.setOnboardingCTViewModel(onboardingCTViewModel);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("67540385783-ofci9vd213bkebdrs4bqe6ildlpuh51i.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(getContext(), gso);

        onboardingCTViewModel.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if(propertyId== BR.isActivityGoogleLogin){
                   Intent signInIntent=mGoogleSignInClient.getSignInIntent();
                    boolean isLogin=onboardingCTViewModel.isActivityGoogleLogin.get();
                    if(isLogin){
                       startActivityForResult(signInIntent,onboardingCTViewModel.RC_SIGN_IN);
                    }
                }

                if(propertyId==BR.isNavigationtoLogin){
                    boolean isNavigate=onboardingCTViewModel.isNavigationtoLogin.get();
                    if(isNavigate){
                        Navigation.findNavController(getView()).navigate(R.id.action_onBoardingCT_to_login);
                    }
                }
                if(propertyId==BR.isNavigationtoSignUp){
                    boolean isNavigate=onboardingCTViewModel.isNavigationtoSignUp.get();
                    if(isNavigate){
                        Navigation.findNavController(getView()).navigate(R.id.action_onBoardingCT_to_register);
                    }
                }
                if(propertyId==BR.isUpdateFBUI){
                    boolean isNavigate=onboardingCTViewModel.isUpdateFBUI.get();
                    if(isNavigate){
                        SharedPreferences sharedPreferences= getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("user","true");
                        editor.apply();
                    }
                }
                if(propertyId == BR.isActivityFacebookLogin){
                    boolean isLogin=onboardingCTViewModel.isActivityFacebookLogin.get();
                    if(isLogin){
                        LoginButton btn_login = fragmentOnBoardingCtBinding.loginButton;
                        btn_login.setReadPermissions("email", "public_profile");
                        btn_login.setFragment(OnBoardingCT.this);
                            btn_login.registerCallback(onboardingCTViewModel.callbackManager, new FacebookCallback<LoginResult>() {
                                @Override
                                public void onSuccess(LoginResult loginResult) {
                                    onboardingCTViewModel.handleFacebookAccessToken(getActivity(), loginResult.getAccessToken());
                                }

                                @Override
                                public void onCancel() {

                                }

                                @Override
                                public void onError(@NonNull FacebookException e) {

                                }
                            });
                        }
                    }
            }
        });

        return fragmentOnBoardingCtBinding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            OnboardingCTViewModel onboardingCTViewModel = new OnboardingCTViewModel();
            onboardingCTViewModel.callbackManager.onActivityResult(requestCode, resultCode, data);
            if (requestCode == onboardingCTViewModel.RC_SIGN_IN) {
                Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    onboardingCTViewModel.firebaseAuthWithGoogle(requireActivity(),account.getIdToken(),getView());
                } catch (ApiException e) {
                }
        }
    }
}