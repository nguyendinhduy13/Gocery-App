package com.mobiledevelopment.feature.admin.category;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.Category;
import com.mobiledevelopment.core.repository.CategoryRepository;
import com.mobiledevelopment.core.utils.GridSpacingItemDecoration;
import com.mobiledevelopment.databinding.FragmentCategoryBinding;
import com.mobiledevelopment.feature.customer.shop.CategoryAdapter;
import com.mobiledevelopment.feature.customer.shop.CategoryState;
import com.mobiledevelopment.feature.customer.shop.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    public static final String TAG = "CategoryFragment";
    private FragmentCategoryBinding binding;
    private CategoryViewModel categoryViewModel;
    private AdminCategoryAdapter categoryAdapter;

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryViewModel = new ViewModelProvider(
                requireActivity(),
                new CategoryViewModel.CategoryViewModelFactory(new CategoryRepository())).get(CategoryViewModel.class);
        // get categories
        categoryViewModel.initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpUi();
        observeViewModel();
    }

    private void setUpUi() {
        // set up app bar
        binding.myToolbar.inflateMenu(R.menu.app_bar_cart);
        binding.myToolbar.setTitle("Cart");

        // init rcv categories
        categoryAdapter = new AdminCategoryAdapter(getContext(), (idCategory) -> {
            Bundle bundle = new Bundle();
            bundle.putString("idCategory",idCategory);

            Navigation.findNavController(getActivity(), R.id.admin_fragment_container)
                    .navigate(R.id.action_navigation_category_to_detailCategoryFragment,bundle);
        },()->{
            Navigation.findNavController(getActivity(), R.id.admin_fragment_container)
                    .navigate(R.id.action_navigation_category_to_addCategoryFragment);
        });

        binding.rcvCategories.setAdapter(categoryAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        binding.rcvCategories.setLayoutManager(layoutManager);
        binding.rcvCategories.addItemDecoration(new GridSpacingItemDecoration(4,20,true));
    }

    private void observeViewModel(){
        categoryViewModel.getCategoriesLiveData().observe(getViewLifecycleOwner(), (categories) -> {
            categoryAdapter.submitList(categories);
            Log.d(
                    TAG,
                    "viewModel.categories.observe() - categories: " +  categories);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}