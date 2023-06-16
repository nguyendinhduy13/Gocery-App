package com.mobiledevelopment.feature.customer.shop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mobiledevelopment.R;
import com.mobiledevelopment.core.repository.CategoryRepository;
import com.mobiledevelopment.core.utils.GridSpacingItemDecoration;
import com.mobiledevelopment.databinding.FragmentShopBinding;
import com.mobiledevelopment.feature.admin.product.ProductAdapter;


public class ShopFragment extends Fragment {

    public final String TAG = "ShopFragment";
    private CategoryViewModel categoryViewModel;
    private FragmentShopBinding binding;
    private CategoryAdapter categoryAdapter;

    private ProductAdapter productAdapter;

    public ShopFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        categoryViewModel = new ViewModelProvider(
                requireActivity(),
                new CategoryViewModel.CategoryViewModelFactory(new CategoryRepository())).get(CategoryViewModel.class);

        categoryViewModel.initData();

        binding = FragmentShopBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpUi();
        observeViewModel();
    }

    private void setUpUi() {
        categoryAdapter = new CategoryAdapter(getContext(), (idCategory) -> {
            Bundle bundle = new Bundle();
            bundle.putString("idCategory",idCategory);
            Navigation.findNavController(getView()).navigate(R.id.action_shopFragment_to_categoryDetailFragment,bundle);
        });
        binding.rcvCategories.setAdapter(categoryAdapter);
        binding.rcvCategories.setLayoutManager(new GridLayoutManager(getContext(),4));
        binding.rcvCategories.addItemDecoration(new GridSpacingItemDecoration(4,20,true));
    }

    private void observeViewModel() {
        categoryViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), (categories) -> {
            categoryAdapter.submitList(categories);
            Log.d(
                    TAG,
                    "viewModel.categories.observe() - categories: " +  categories);
        });
    }
}