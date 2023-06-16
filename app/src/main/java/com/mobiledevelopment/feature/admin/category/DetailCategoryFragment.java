package com.mobiledevelopment.feature.admin.category;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.mobiledevelopment.R;
import com.mobiledevelopment.core.models.Category;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.repository.CategoryRepository;
import com.mobiledevelopment.core.repository.ProductRepository;
import com.mobiledevelopment.core.utils.GridSpacingItemDecoration;
import com.mobiledevelopment.core.utils.Utils;
import com.mobiledevelopment.databinding.DialogDeleteCategoryBinding;
import com.mobiledevelopment.databinding.FragmentDetailCategoryBinding;
import com.mobiledevelopment.feature.admin.product.ProductAdapter;
import com.mobiledevelopment.feature.admin.product.ProductViewModel;

import java.util.List;

import vn.thanguit.toastperfect.ToastPerfect;


public class DetailCategoryFragment extends Fragment {
    public static String TAG = "FragmentDetailCategory";
    private FragmentDetailCategoryBinding binding;
    private DetailCategoryViewModel detailCategoryViewModel;
    private ProductViewModel productViewModel;
    private String idCategory;
    private ProductAdapter productAdapter;

    public DetailCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        idCategory = bundle.getString("idCategory");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        detailCategoryViewModel = new ViewModelProvider(
                requireActivity(),
                new DetailCategoryViewModel.DetailCategoryViewModelFactory(new CategoryRepository())).get(DetailCategoryViewModel.class);

        productViewModel = new ViewModelProvider(
                requireActivity(),
                new ProductViewModel.ProductViewModelFactory(new ProductRepository())).get(ProductViewModel.class);

        binding = FragmentDetailCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        detailCategoryViewModel.resetData();

        setUpUi();
        observeViewModel();
    }

    void initRcvProducts() {
        // init rcv products
        productAdapter = new ProductAdapter(getContext(), (idProduct) -> {
            Bundle bundle = new Bundle();
            bundle.putString("idProduct", idProduct);

            Navigation.findNavController(getActivity(), R.id.admin_fragment_container)
                    .navigate(R.id.action_detailCategoryFragment_to_detailProductFragment,bundle);
        });

        binding.rcvProducts.setAdapter(productAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        binding.rcvProducts.setLayoutManager(layoutManager);
        binding.rcvProducts.addItemDecoration(new GridSpacingItemDecoration(2, 0, false));
    }

    void setUpUi() {
        detailCategoryViewModel.setCurrentCategoryId(idCategory);

        initRcvProducts();

        binding.btnViewMoreProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getActivity(), R.id.admin_fragment_container)
                        .navigate(R.id.action_detailCategoryFragment_to_productFragment);
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).popBackStack();
            }
        });

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();
            }
        });

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("idCategory", idCategory);
                Navigation.findNavController(getActivity(), R.id.admin_fragment_container)
                        .navigate(R.id.action_detailCategoryFragment_to_editCategoryFragment,bundle);
            }
        });
    }

    void showLoading() {
        binding.wholeLayoutWrapper.setVisibility(View.GONE);
        binding.progressbar.setVisibility(View.VISIBLE);
    }

    void hideLoading() {
        binding.wholeLayoutWrapper.setVisibility(View.VISIBLE);
        binding.progressbar.setVisibility(View.GONE);
    }

    void observeViewModel() {
        detailCategoryViewModel.uiState.observe(getViewLifecycleOwner(), new Observer<DetailCategoryUiState>() {
            @Override
            public void onChanged(DetailCategoryUiState state) {
                if (state instanceof DetailCategoryUiState.FetchDataSuccess) {
                    hideLoading();

                    Category category = ((DetailCategoryUiState.FetchDataSuccess) state).getData();

                    productViewModel.getProducts(category.getId());

                    binding.txtName.setText(category.getName());

                    Glide
                            .with(getContext())
                            .load(category.getImage())
                            .centerCrop()
                            .into(binding.imgCategory);
                } else if (state instanceof DetailCategoryUiState.FetchDataFailure) {
                    hideLoading();

                    String errorMessage = ((DetailCategoryUiState.FetchDataFailure) state).getErrorMessage();

                    Utils.showToast(getContext(), ToastPerfect.ERROR, errorMessage);
                } else if (state instanceof DetailCategoryUiState.Loading) {
                    showLoading();
                } else if (state instanceof DetailCategoryUiState.DeleteSuccess) {
                    Navigation.findNavController(getView()).popBackStack();
                    Utils.showToast(getContext(), ToastPerfect.SUCCESS, "Delete category successfully");
                } else if (state instanceof DetailCategoryUiState.DeleteFailure) {
                    String errorMessage = ((DetailCategoryUiState.DeleteFailure) state).getErrorMessage();
                    Utils.showToast(getContext(), ToastPerfect.ERROR, errorMessage);
                }
            }
        });

        productViewModel.getProductsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Product>>() {
            @Override
            public void onChanged(List<Product> products) {
                productAdapter.submitList(products);
                Log.d(
                        TAG,
                        "viewModel.products.observe() - products: " +  products);
            }
        });
    }

    private void showDeleteDialog() {
        DialogDeleteCategoryBinding dialogBinding = DialogDeleteCategoryBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(dialogBinding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_background);

        dialogBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailCategoryViewModel.deleteCategory(idCategory);
                dialog.dismiss();
            }
        });

        dialogBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}