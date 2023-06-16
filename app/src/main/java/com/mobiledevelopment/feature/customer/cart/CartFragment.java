package com.mobiledevelopment.feature.customer.cart;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.Cart;
import com.mobiledevelopment.core.models.CartTest;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.databinding.FragmentCartBinding;
import com.mobiledevelopment.feature.customer.product.viewmodel.CartReviewViewModel;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    public final String TAG = "CartFragment";
    private FragmentCartBinding binding;
    private CartViewModel cartViewModel;

    private CartAdapter.OnButtonCartAdapterClick listener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listener = new CartAdapter.OnButtonCartAdapterClick() {
            @Override
            public void onClick(int quantity, String id) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    cartViewModel.updateQuantity(quantity, id);
                }
            }
        };
        setUpUi();
        observeViewModel();
    }

    private void setUpUi() {
        cartViewModel.getUnpaidCart();
        // set up app bar
        binding.myToolbar.inflateMenu(R.menu.app_bar_cart);
        binding.myToolbar.setTitle("Cart");

        // set up rcv

        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cartViewModel.updateDatabase();
            }
        });
    }


    private void observeViewModel() {
        cartViewModel.getList().observe(getViewLifecycleOwner(), new Observer<CartTest>() {
            @Override
            public void onChanged(CartTest cartTest) {
                CartAdapter cartAdapter = new CartAdapter(getContext(),
                        cartTest.getCart(), listener);
                binding.rcvCart.setAdapter(cartAdapter);
                binding.rcvCart.setLayoutManager(new LinearLayoutManager(getContext()));            }
        });

        cartViewModel.getState().observe(getViewLifecycleOwner(), new Observer<CartState>() {
            @Override
            public void onChanged(CartState cartState) {
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_cartFragment_to_cartReviewFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}