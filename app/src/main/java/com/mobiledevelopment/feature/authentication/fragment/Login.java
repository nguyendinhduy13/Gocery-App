package com.mobiledevelopment.feature.authentication.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mobiledevelopment.R;
import com.mobiledevelopment.databinding.FragmentLoginBinding;
import com.mobiledevelopment.feature.authentication.viewmodel.LoginViewModel;

public class Login extends Fragment {
    private FragmentLoginBinding fragmentLoginBinding;
    public static final String SHARED_PREFS = "sharedPrefs";
    private LoginViewModel loginViewModel;
    public Login() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false);
        loginViewModel = new LoginViewModel();
        fragmentLoginBinding.setLoginViewModel(loginViewModel);


        loginViewModel.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if(propertyId==BR.isGoBack){
                    boolean isCheckGoBack=loginViewModel.isGoBack.get();
                    if(isCheckGoBack){
                        Navigation.findNavController(getView()).popBackStack();
                    }
                }

                if(propertyId==BR.isForgotPassword){
                    AlertDialog.Builder myDialog=new AlertDialog.Builder(getContext());
                    LayoutInflater inflater=LayoutInflater.from(getContext());
                    View myView=inflater.inflate(R.layout.fragment_forgot__password,null);
                    myDialog.setView(myView);
                    final AlertDialog dialog=myDialog.create();
                    dialog.setCancelable(true);
                    dialog.show();
                    final ImageView btn_back=myView.findViewById(R.id.arrow_back);
                    final EditText edt_email=myView.findViewById(R.id.edt_email_forgot);
                    final ConstraintLayout btn_send_email=myView.findViewById(R.id.btn_send_email);

                    btn_back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    btn_send_email.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String edt_email_forgot=edt_email.getText().toString().trim();
                            if(TextUtils.isEmpty(edt_email_forgot)){
                                edt_email.setError("Empty");
                            }
                            else {
                                loginViewModel.getmAuth().sendPasswordResetEmail(edt_email_forgot).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.show();
                }

                if(propertyId==BR.isLogintoHome){
                    boolean isCheckLogin=loginViewModel.isLogintoHome.get();
                    if(isCheckLogin) {
                        loginViewModel.getmAuth().signInWithEmailAndPassword(loginViewModel.getEmail(), loginViewModel.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // save firebase notification toke
                                    //saveFirebaseToken();
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("user", "true");
                                    editor.apply();
                                    Toast.makeText(getContext(), "Login successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }}
                if(propertyId== BR.isCheckEditText){
                    EditText edt_email=fragmentLoginBinding.edtEmail;
                    EditText pass_word=fragmentLoginBinding.edtPasswordInside;
                    String email=edt_email.getText().toString().trim();
                    String password=pass_word.getText().toString().trim();
                    boolean isCheckEditText=loginViewModel.isCheckEditText.get();
                    boolean check_email=false;
                    boolean check_pass_word=false;
                    if(isCheckEditText){
                        if(TextUtils.isEmpty(email)){
                            edt_email.setError("Empty");
                            check_email=true;
                            loginViewModel.setCheck(true);
                        }
                        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()&&!check_email){
                            edt_email.setError("Wrong Format");
                            loginViewModel.setCheck(true);
                        }
                        if(TextUtils.isEmpty(password)){
                            pass_word.setError("Empty");
                            check_pass_word=true;
                            loginViewModel.setCheck(true);
                        }
                        if(password.length()<6&&!check_pass_word){
                            pass_word.setError("Password must have more than 5 characters");
                            loginViewModel.setCheck(true);
                        }
                    }
                }
            }
        });

        return fragmentLoginBinding.getRoot();
    }

    void saveFirebaseToken() {
        String currentId = loginViewModel.getmAuth().getCurrentUser().getUid();
        loginViewModel.getDb().collection("User").document(currentId)
                .update("token","");
    }
}