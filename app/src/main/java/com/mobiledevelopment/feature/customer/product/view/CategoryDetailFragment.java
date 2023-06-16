package com.mobiledevelopment.feature.customer.product.view;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.CategoryDetail;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.repository.CategoryRepository;
import com.mobiledevelopment.core.repository.ProductRepository;
import com.mobiledevelopment.core.utils.GridSpacingItemDecoration;
import com.mobiledevelopment.databinding.FragmentCategoryDetailBinding;
import com.mobiledevelopment.feature.admin.product.ProductAdapter;
import com.mobiledevelopment.feature.admin.product.ProductViewModel;
import com.mobiledevelopment.feature.customer.product.adapter.CategoryDetailAdapter;
import com.mobiledevelopment.feature.customer.shop.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryDetailFragment extends Fragment {

    private String idCategory;
    private FragmentCategoryDetailBinding _binding;
    SortingBottomSheetDialog dialog;
    FilterBottomSheetDialog filterDialog;

    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;

    public CategoryDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        idCategory = bundle.getString("idCategory","");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ProductViewModel.ProductViewModelFactory(new ProductRepository())).get(ProductViewModel.class);

        productViewModel.getProducts(idCategory);

        _binding = FragmentCategoryDetailBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initRcvProducts();
        setupSortingBottomSheet();
        setupFilterBottomSheet();
        setOnClickListener();
        observeViewModel();
        OnChangeSearchView();
    }


    private void OnChangeSearchView (){
        _binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String lowercaseText = newText.toLowerCase();
                productViewModel.searchProduct(lowercaseText);
                return true;
            }
        });
    }

    private void setOnClickListener()
    {
        _binding.btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show(getParentFragmentManager(),null);
            }
        });

        _binding.btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterDialog.show(getParentFragmentManager(),null);
            }
        });

        _binding.btnNavigateUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).popBackStack();
            }
        });
    }

    public void initRcvProducts()
    {
        // init rcv products
        productAdapter = new ProductAdapter(getContext(), (idProduct) -> {
            Bundle bundle = new Bundle();
            bundle.putString("idProduct", idProduct);

            Navigation.findNavController(requireActivity(), R.id.customer_fragment_container)
                    .navigate(R.id.action_categoryDetailFragment_to_itemDetailFragment,bundle);
        });

        _binding.rcvProducts.setAdapter(productAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        _binding.rcvProducts.setLayoutManager(layoutManager);
        _binding.rcvProducts.addItemDecoration(new GridSpacingItemDecoration(2, 0, false));
    }

    private void setupSortingBottomSheet() {
        SortingBottomSheetDialog.OnActionCompleteListener listener = new SortingBottomSheetDialog.OnActionCompleteListener() {
            @Override
            public void onActionComplete(int type) {
                // highest 2; lowest 3
                if(type == 2){
                    productViewModel.sortProducts(2);
                }
                else if(type == 3){
                    productViewModel.sortProducts(3);
                }
            }
        };

        dialog = new SortingBottomSheetDialog(listener);
    }

    private void setupFilterBottomSheet(){
        FilterBottomSheetDialog.OnActionCompleteListener listener=new FilterBottomSheetDialog.OnActionCompleteListener() {
            @Override
            public void onActionComplete(int type) {
                productViewModel.filterProduct(type);
            }
        };
        filterDialog=new FilterBottomSheetDialog(listener);
    }

    private void observeViewModel(){
        productViewModel.getProductsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                productAdapter.submitList(null);
                productAdapter.submitList(products);
            }
        });

        productViewModel.getSearchLiveData().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                productAdapter.submitList(products);
            }
        });
        productViewModel.getFilterLiveData().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                productAdapter.submitList(products);
            }
        });
    }
}