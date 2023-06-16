package com.mobiledevelopment.feature.customer.product.view;

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

import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.CartTest;
import com.mobiledevelopment.core.utils.StringFormatterUtils;
import com.mobiledevelopment.databinding.FragmentCartReviewBinding;
import com.mobiledevelopment.feature.customer.product.adapter.CartReviewAdapter;
import com.mobiledevelopment.feature.customer.product.viewmodel.CartReviewViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartReviewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentCartReviewBinding binding;
    CartReviewViewModel viewModel;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CartReviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartReviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartReviewFragment newInstance(String param1, String param2) {
        CartReviewFragment fragment = new CartReviewFragment();
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
        viewModel = new ViewModelProvider(this).get(CartReviewViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCartReviewBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeViewModel();
        setListener();
        viewModel.getUnpaidCart();
    }

    private void observeViewModel() {
        viewModel.getList().observe(getViewLifecycleOwner(), new Observer<CartTest>() {
            @Override
            public void onChanged(CartTest cartTest) {
                binding.cartItemRecyclerView.setAdapter(new CartReviewAdapter(cartTest.getCart()));
                binding.totalCostTextView.setText(
                        StringFormatterUtils.toCurrency(
                                ((CartReviewAdapter)
                                        binding.cartItemRecyclerView.getAdapter()).getTotalCost()));
                showListUI();
            }
        });
    }

    private void showListUI() {
        binding.btnCheckout.setVisibility(View.VISIBLE);
        binding.btnNavigateUp2.setVisibility(View.GONE);
    }

    private void setListener()
    {
        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("total_cost", binding.totalCostTextView.getText().toString());
                bundle.putString("docId", viewModel.getDocId());
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_cartReviewFragment_to_checkOut1Fragment, bundle);
            }
        });

        binding.btnNavigateUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(binding.getRoot()).popBackStack();
            }
        });

        binding.btnNavigateUp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(binding.getRoot()).popBackStack();
            }
        });
    }
}