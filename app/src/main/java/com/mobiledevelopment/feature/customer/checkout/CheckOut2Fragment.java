package com.mobiledevelopment.feature.customer.checkout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.mobiledevelopment.R;
import com.mobiledevelopment.databinding.FragmentCheckOut2Binding;

public class CheckOut2Fragment extends Fragment {
    public final String TAG = "Checkout2Fragment";
    private FragmentCheckOut2Binding binding;

    public CheckOut2Fragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentCheckOut2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnClick();
    }

    private void setOnClick() {
        binding.btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_checkOut2Fragment_to_shopFragment);
            }
        });
    }

}