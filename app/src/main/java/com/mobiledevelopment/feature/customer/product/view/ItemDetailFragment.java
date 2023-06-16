package com.mobiledevelopment.feature.customer.product.view;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.mobiledevelopment.R;
import com.mobiledevelopment.core.repository.ProductRepository;
import com.mobiledevelopment.core.utils.StringFormatterUtils;
import com.mobiledevelopment.core.utils.Utils;
import com.mobiledevelopment.databinding.FragmentItemDetailBinding;
import com.mobiledevelopment.feature.admin.product.PhotoAdapter;
import com.mobiledevelopment.feature.customer.CustomerActivityViewModel;

import vn.thanguit.toastperfect.ToastPerfect;

public class ItemDetailFragment extends Fragment {
    private FragmentItemDetailBinding binding;
    public static final String TAG = "ItemDetailFragment";
    private DetailProductViewModel detailProductViewModel;
    private String idProduct;
    private PhotoAdapter photoAdapter;

    public ItemDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        assert bundle != null;
        idProduct = bundle.getString("idProduct");
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        detailProductViewModel = new ViewModelProvider(
                requireActivity(),
                new DetailProductViewModel.DetailProductViewModelFactory(new ProductRepository())).get(
                DetailProductViewModel.class);

        binding = FragmentItemDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CustomerActivityViewModel customerActivityViewModel =
                new ViewModelProvider(requireActivity()).get(CustomerActivityViewModel.class);

        customerActivityViewModel.getCurrentUserId()
                .observe(
                        getViewLifecycleOwner(),
                        userId -> detailProductViewModel.setCurrentUserId(userId));

        setUpUI();
        observeViewModel();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUpUI() {
        detailProductViewModel.setCurrentProductId(idProduct);
        binding.bthAddToCart.setOnClickListener(view -> detailProductViewModel.addProductToCart(() -> {
            Utils.showToast(requireContext(), ToastPerfect.SUCCESS, "Product added to cart!");
        }));
        binding.btnNavigateUp.setOnClickListener(view -> Navigation.findNavController(requireView())
                .popBackStack());

        binding.btnAddCount.setOnClickListener(view -> detailProductViewModel.incrementProductCount());
        binding.btnMinusCount.setOnClickListener(view -> detailProductViewModel.decrementProductCount());
        binding.bthBuyNow.setOnClickListener(view -> detailProductViewModel.addProductToCart(() -> {
            Navigation.findNavController(requireView())
                    .navigate(ItemDetailFragmentDirections.actionItemDetailFragmentToCartReviewFragment());

        }));
    }

    @SuppressLint("SetTextI18n")
    private void observeViewModel() {
        detailProductViewModel.productLiveData.observe(
                getViewLifecycleOwner(),
                product -> {
                    photoAdapter = new PhotoAdapter(getContext(), product.getImages());
                    binding.viewPager.setAdapter(photoAdapter);
                    binding.circleIndicator.setViewPager(binding.viewPager);
                    photoAdapter.registerDataSetObserver(binding.circleIndicator.getDataSetObserver());
                    binding.itemNameTextView.setText(product.getName());
                    binding.descriptionTextView.setText(product.getDescription());
                    binding.priceTextView.setText(StringFormatterUtils.toCurrency(product.getPrice()));
                    binding.salePriceTextView.setText(StringFormatterUtils.toCurrency(product.getPrice() * (100 - product.getDiscount()) * 0.01));

                    //binding.txtUnit.setText(product.getUnit());
                    binding.salePercentTextView.setText(product.getDiscount() * 100 + "%");

                    if (product.getDiscount() != 0) {
                        binding.priceTextView.setPaintFlags(binding.priceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    else {
                        binding.salePriceTextView.setVisibility(View.GONE);
                    }
                });

        detailProductViewModel.productCount.observe(
                getViewLifecycleOwner(),
                count -> binding.textViewQuantity.setText(count.toString()));
    }
}