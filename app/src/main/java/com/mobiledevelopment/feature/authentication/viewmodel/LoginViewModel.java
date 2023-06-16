package com.mobiledevelopment.feature.authentication.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobiledevelopment.BR;

public class LoginViewModel extends BaseObservable {
    private String email;
    private String password;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Boolean check;

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    @Bindable
    public ObservableField<Boolean> isCheckEditText=new ObservableField<>();

    @Bindable
    public ObservableField<Boolean> isGoBack=new ObservableField<>();

    @Bindable
    public ObservableField<Boolean> isLogintoHome=new ObservableField<>();

    @Bindable
    public ObservableField<Boolean> isForgotPassword=new ObservableField<>();

    @Bindable
    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
        notifyPropertyChanged(BR.check);
    }

    public ObservableField<Boolean> isShowPassWord=new ObservableField<>();


    public LoginViewModel() {
        this.mAuth=FirebaseAuth.getInstance();
        isShowPassWord.set(true);
        this.check=false;
    }

    public void setIsCheckEditText(boolean value) {
        isCheckEditText.set(value);
        notifyPropertyChanged(BR.isCheckEditText);
    }

    public void setIsGoBack(boolean value) {
        isGoBack.set(value);
        notifyPropertyChanged(BR.isGoBack);
    }

    public void setIsForgotPassword(boolean value) {
        isForgotPassword.set(value);
        notifyPropertyChanged(BR.isForgotPassword);
    }

    public void setIsLogintoHome (boolean value){
        isLogintoHome.set(value);
        notifyPropertyChanged(BR.isLogintoHome);
    }


    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public void onClickGoBack(){
       setIsGoBack(true);
    }

    public void OnClickStatusPassWord(){
        isShowPassWord.set(!isShowPassWord.get());
    }

    public void OnClickForgot(){
        setIsForgotPassword(true);
    }

    public void onClickLogin(){
        setCheck(false);
        setIsCheckEditText(true);
        if(check){
            return;
        }
        else {
               setIsLogintoHome(true);
        }
    }
}
