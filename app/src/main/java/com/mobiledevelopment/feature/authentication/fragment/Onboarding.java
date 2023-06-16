package com.mobiledevelopment.feature.authentication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.mobiledevelopment.BR;
import com.mobiledevelopment.R;
import com.mobiledevelopment.databinding.FragmentOnboardingBinding;
import com.mobiledevelopment.feature.authentication.viewmodel.OnboardingViewModel;

public class Onboarding extends Fragment {

    private FragmentOnboardingBinding fragmentOnboardingBinding;
    public static final String SHARED_PREFS="sharedPrefs";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentOnboardingBinding = FragmentOnboardingBinding.inflate(inflater, container, false);
        OnboardingViewModel onboardingViewModel = new OnboardingViewModel();
        fragmentOnboardingBinding.setOnBoardingViewModel(onboardingViewModel);
        onboardingViewModel.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.isNavigationtoOnboardingCT) {
                    boolean isNavigate = onboardingViewModel.isNavigationtoOnboardingCT.get();
                    if (isNavigate) {
                        Navigation.findNavController(requireView()).navigate(R.id.action_onboarding_to_onBoardingCT);
                    }
                }
            }
        });
        return fragmentOnboardingBinding.getRoot();
    }
}