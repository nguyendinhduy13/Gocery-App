package com.mobiledevelopment.feature.authentication.viewmodel;


import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.mobiledevelopment.BR;

public class OnboardingViewModel extends BaseObservable {

    @Bindable
    public ObservableField<Boolean> isNavigationtoOnboardingCT=new ObservableField<>();

    public void setIsNavigationtoOnboardingCT(boolean value) {
        isNavigationtoOnboardingCT.set(value);
        notifyPropertyChanged(BR.isNavigationtoOnboardingCT);
    }

    public void OnClickNavigate(){
        setIsNavigationtoOnboardingCT(true);
    }
}
