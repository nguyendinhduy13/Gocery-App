package com.mobiledevelopment.feature.authentication.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.mobiledevelopment.BR;
import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.User;
import com.mobiledevelopment.databinding.FragmentRegisterBinding;
import com.mobiledevelopment.feature.authentication.viewmodel.RegisterViewModel;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Register#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Register extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private FragmentRegisterBinding fragmentRegisterBinding;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    public Register() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Register.
     */
    // TODO: Rename and change types and number of parameters
    public static Register newInstance(String param1, String param2) {
        Register fragment = new Register();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater, container, false);
        RegisterViewModel registerViewModel = new RegisterViewModel();
        fragmentRegisterBinding.setRegisterViewModel(registerViewModel);

        registerViewModel.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (propertyId == BR.isActivityDatePicker) {
                    boolean isShowDatePicker = registerViewModel.isActivityDatePicker.get();
                    if (isShowDatePicker) {
                        Calendar calendar = Calendar.getInstance();
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog dialog = new DatePickerDialog(
                                getContext(),
                                android.R.style.Theme_DeviceDefault_Dialog,
                                dateSetListener,
                                year,
                                month,
                                day);
                        dialog.show();
                    }
                }
                if (propertyId == BR.isGoBack) {
                    boolean ischeckGoback = registerViewModel.isGoBack.get();
                    if (ischeckGoback) {
                        Navigation.findNavController(getView())
                                .popBackStack();
                    }
                }
                if (propertyId == BR.isRegister) {
                    boolean ischeckRegister = registerViewModel.isRegister.get();
                    if (ischeckRegister) {
                        registerViewModel.getmAuth().createUserWithEmailAndPassword(
                                        registerViewModel.getEmail(),
                                        registerViewModel.getPassword())
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            Navigation.findNavController(getView())
                                                    .navigate(R.id.action_register_to_login);
                                            User user1 = new User(
                                                    registerViewModel.getmAuth().getCurrentUser()
                                                            .getUid(),
                                                    registerViewModel.getEmail(),
                                                    registerViewModel.getFullName(),
                                                    registerViewModel.getPassword(),
                                                    registerViewModel.getUsername(),
                                                    registerViewModel.getPhone(),
                                                    registerViewModel.getGender(),
                                                    "01/01/2000",
                                                    "1",
                                                    0L,
                                                    "");
                                            registerViewModel.getDb().collection("User")
                                                    .document(registerViewModel.getmAuth()
                                                            .getCurrentUser().getUid())
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
                                                            Log.w(
                                                                    "hello",
                                                                    "Error writing document",
                                                                    e);
                                                        }
                                                    });
                                            Toast.makeText(
                                                    getContext(),
                                                    "Register successfully",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(
                                                    getContext(),
                                                    "Register Failed",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                }
                if (propertyId == BR.isCheckEditText) {
                    EditText edt_email = fragmentRegisterBinding.edtEmail;
                    EditText user_name = fragmentRegisterBinding.edtUsername;
                    EditText full_name = fragmentRegisterBinding.edtFullName;
                    EditText phone_number = fragmentRegisterBinding.edtPhone;
                    EditText pass_word = fragmentRegisterBinding.edtPasswordInside;
                    EditText re_pass_word = fragmentRegisterBinding.edtRePasswordInside;
                    EditText birth_day = fragmentRegisterBinding.edtBirthday;

                    String email = edt_email.getText().toString().trim();
                    String username = user_name.getText().toString().trim();
                    String fullname = full_name.getText().toString().trim();
                    String phonenumber = phone_number.getText().toString().trim();
                    String password = pass_word.getText().toString().trim();
                    String repassword = re_pass_word.getText().toString().trim();
                    String birthday = birth_day.getText().toString().trim();
                    boolean isCheckEditText = registerViewModel.isCheckEditText.get();
                    boolean check_phone = false;
                    boolean check_email = false;
                    boolean check_password = false;
                    boolean check_re_password = false;
                    if (isCheckEditText) {
                        if (TextUtils.isEmpty(email)) {
                            edt_email.setError("Empty");
                            check_email = true;
                            registerViewModel.setCheck(true);
                        }
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() && !check_email) {
                            edt_email.setError("Wrong Format");
                            registerViewModel.setCheck(true);
                        }
                        if (TextUtils.isEmpty(fullname)) {
                            full_name.setError("Empty");
                            registerViewModel.setCheck(true);
                        }
                        if (TextUtils.isEmpty(username)) {
                            user_name.setError("Empty");
                            registerViewModel.setCheck(true);
                        }
                        if (TextUtils.isEmpty(phonenumber)) {
                            phone_number.setError("Empty");
                            check_phone = true;
                            registerViewModel.setCheck(true);
                        }
                        if (TextUtils.isEmpty(birthday)) {
                            birth_day.setError("Empty");
                            registerViewModel.setCheck(true);
                        }
                        if (!Patterns.PHONE.matcher(phonenumber).matches() && !check_phone) {
                            phone_number.setError("Wrong Format");
                            registerViewModel.setCheck(true);
                        }
                        if (TextUtils.isEmpty(password)) {
                            pass_word.setError("Empty");
                            check_password = true;
                            registerViewModel.setCheck(true);
                        }
                        if (password.length() < 6 && !check_password) {
                            pass_word.setError("Password must have more than 5 characters");
                            registerViewModel.setCheck(true);
                        }
                        if (TextUtils.isEmpty(repassword)) {
                            re_pass_word.setError("Empty");
                            check_re_password = true;
                            registerViewModel.setCheck(true);
                        }
                        if (re_pass_word.length() < 6 && !check_re_password) {
                            re_pass_word.setError("Password must have more than 5 characters");
                            registerViewModel.setCheck(true);
                        }
                        if (!password.equals(repassword)) {
                            registerViewModel.messageLogin.set("Passwords do not match");
                        }
                        if (!registerViewModel.getMale() && !registerViewModel.getFemale()) {
                            registerViewModel.messageGender.set("Gender is empty");
                        }
                    }
                }
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = day + "/" + month + "/" + year;
                registerViewModel.setDatetime(date);
                EditText birth_day = fragmentRegisterBinding.edtBirthday;
                birth_day.setError(null);
            }
        };
        return fragmentRegisterBinding.getRoot();
    }

}