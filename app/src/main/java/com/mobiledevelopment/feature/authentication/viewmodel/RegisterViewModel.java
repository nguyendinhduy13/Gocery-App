package com.mobiledevelopment.feature.authentication.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mobiledevelopment.BR;

public class RegisterViewModel extends BaseObservable {
    private String email;
    private String password;
    private String re_password;
    private String datetime;
    private String username;
    private String fullName;
    private Boolean male;
    private Boolean female;
    private String gender;
    private String phone;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Boolean check;

    public FirebaseFirestore getDb() {
        return db;
    }

    @Bindable
    public ObservableField<String> messageLogin=new ObservableField<>();

    @Bindable
    public ObservableField<Boolean> isShowMessage=new ObservableField<Boolean>();

    @Bindable
    public ObservableField<Boolean> isRegister=new ObservableField<Boolean>();

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    @Bindable
    public ObservableField<String>messageGender=new ObservableField<>();

    @Bindable
    public ObservableField<Boolean> isShowMessageGender=new ObservableField<Boolean>();

    @Bindable
    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
        notifyPropertyChanged(BR.check);
    }

    public void setActivityMessage(boolean value) {
        isShowMessage.set(value);
        notifyPropertyChanged(BR.isShowMessage);
    }

    public void setActivityMessageGender(boolean value) {
        isShowMessageGender.set(value);
        notifyPropertyChanged(BR.isShowMessageGender);
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
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        notifyPropertyChanged(BR.gender);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getRe_password() {
        return re_password;
    }

    public void setRe_password(String re_password) {
        this.re_password = re_password;
        notifyPropertyChanged(BR.re_password);
    }

    @Bindable
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        notifyPropertyChanged(BR.fullName);
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    @Bindable
    public Boolean getMale() {
        return male;
    }

    public void setMale(Boolean male) {
        this.male = male;
        notifyPropertyChanged(BR.male);
    }

    @Bindable
    public Boolean getFemale() {
        return female;
    }

    public void setFemale(Boolean female) {
        this.female = female;
        notifyPropertyChanged(BR.female);
    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }


    @Bindable
    public ObservableField<Boolean> isActivityDatePicker=new ObservableField<>();

    public ObservableField<Boolean> isShowPassWord=new ObservableField<>();

    public ObservableField<Boolean> isShowRePassWord=new ObservableField<>();

    @Bindable
    public ObservableField<Boolean> isCheckEditText=new ObservableField<>();

    @Bindable
    public ObservableField<Boolean> isGoBack=new ObservableField<>();

    public RegisterViewModel() {
        isShowPassWord.set(true);
        isShowRePassWord.set(true);
        this.mAuth=FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.male=false;
        this.female=false;
        this.check=false;
    }

    @Bindable
    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
        notifyPropertyChanged(BR.datetime);
    }

    public void setIsActivityDatePicker(boolean value) {
        isActivityDatePicker.set(value);
        notifyPropertyChanged(BR.isActivityDatePicker);
    }

    public void setIsCheckEditText(boolean value) {
        isCheckEditText.set(value);
        notifyPropertyChanged(BR.isCheckEditText);
    }

    public void setIsGoBack(boolean value) {
        isGoBack.set(value);
        notifyPropertyChanged(BR.isGoBack);
    }

    public void setIsRegister(boolean value) {
        isRegister.set(value);
        notifyPropertyChanged(BR.isRegister);
    }


    public void OnClickGoBack(){
        setIsGoBack(true);
    }

    public void OnClickCalender(){
        setIsActivityDatePicker(true);
    }

    public void OnClickStatusPassWord(){
        isShowPassWord.set(!isShowPassWord.get());
    }

    public void OnClickStatusRePassword(){
        isShowRePassWord.set(!isShowRePassWord.get());
    }

    public void OnClickStatusMale(){
        setMale(!male);
        if(male){
            setFemale(false);
        }
    }

    public void OnClickStatusFeMale(){
        setFemale(!female);
        if(female){
            setMale(false);
        }
    }

    public void OnClickRegister(){
        setCheck(false);
        messageGender.set("");
        messageLogin.set("");
        String gender=getMale()?"Male":"Female";
        setGender(gender);
        setIsCheckEditText(true);
        setActivityMessage(true);
        setActivityMessageGender(true);
        if(check){
            return;
        }
        else {
           setIsRegister(true);
        }
    }
}
