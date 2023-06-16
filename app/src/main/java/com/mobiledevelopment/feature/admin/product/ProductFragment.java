package com.mobiledevelopment.feature.admin.product;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.repository.ProductRepository;
import com.mobiledevelopment.core.utils.GridSpacingItemDecoration;
import com.mobiledevelopment.databinding.FragmentProductBinding;

import java.util.List;

public class ProductFragment extends Fragment {
    public static final String TAG = "ProductFragment";

    private FragmentProductBinding binding;
    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;

    public ProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ProductViewModel.ProductViewModelFactory(new ProductRepository())).get(ProductViewModel.class);

        binding = FragmentProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpUI();
        observeViewModel();
        OnChangeSearchView();
    }

    void initRcvProducts() {
        // init rcv products
        productAdapter = new ProductAdapter(getContext(), (idProduct) -> {
            Bundle bundle = new Bundle();
            bundle.putString("idProduct", idProduct);
        });

        binding.rcvProducts.setAdapter(productAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.rcvProducts.setLayoutManager(layoutManager);
        binding.rcvProducts.addItemDecoration(new GridSpacingItemDecoration(2, 0, false));
    }
    void setUpUI(){
        initRcvProducts();

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(),R.id.admin_fragment_container)
                        .navigate(R.id.action_productFragment_to_addProductFragment);
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).popBackStack();
            }
        });
    }

    private void OnChangeSearchView (){
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    void observeViewModel(){
        productViewModel.getProductsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                productAdapter.submitList(products);
                Log.d(
                        TAG,
                        "viewModel.products.observe() - products: " +  products);
            }
        });
        productViewModel.getSearchLiveData().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                productAdapter.submitList(products);
            }
        });
    }
}