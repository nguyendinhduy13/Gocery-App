package com.mobiledevelopment.feature.admin.product;

import android.app.Dialog;
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
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.repository.ProductRepository;
import com.mobiledevelopment.core.utils.Utils;
import com.mobiledevelopment.databinding.DialogDeleteCategoryBinding;
import com.mobiledevelopment.databinding.FragmentDetailProductBinding;

import vn.thanguit.toastperfect.ToastPerfect;

public class DetailProductFragment extends Fragment {
    public static final String TAG = "DetailProductFragment";

    private DetailProductViewModel detailProductViewModel;
    private FragmentDetailProductBinding binding;

    private String idProduct;
    private PhotoAdapter photoAdapter;

    public DetailProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        idProduct = bundle.getString("idProduct");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        detailProductViewModel = new ViewModelProvider(
                requireActivity(),
                new DetailProductViewModel.DetailProductViewModelFactory(new ProductRepository())).get(DetailProductViewModel.class);

        binding =  FragmentDetailProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        detailProductViewModel.resetData();

        setUpUI();
        observeViewModel();
    }

    private void setUpUI() {
        detailProductViewModel.setCurrentProductId(idProduct);

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).popBackStack();
            }
        });

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("idProduct",idProduct);
                Navigation.findNavController(getView()).navigate(R.id.action_detailProductFragment_to_editProductFragment,bundle);
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
                detailProductViewModel.deleteProduct(idProduct);
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

    private void showLoading(){
        binding.wholeLayoutWrapper.setVisibility(View.GONE);
        binding.progressbar.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        binding.wholeLayoutWrapper.setVisibility(View.VISIBLE);
        binding.progressbar.setVisibility(View.GONE);
    }
    private void observeViewModel(){
        detailProductViewModel.uiState.observe(getViewLifecycleOwner(), new Observer<DetailProductUiState>() {
            @Override
            public void onChanged(DetailProductUiState state) {
                if(state instanceof  DetailProductUiState.Initial){
                    showLoading();
                }else if(state instanceof DetailProductUiState.FetchDataSuccess){
                    hideLoading();
                }else if(state instanceof DetailProductUiState.Loading){
                    showLoading();
                }else if(state instanceof DetailProductUiState.DeleteSuccess){
                    hideLoading();
                    Navigation.findNavController(getView()).popBackStack();
                    Utils.showToast(getContext(), ToastPerfect.SUCCESS,"Delete product successfully");
                }else if(state instanceof DetailProductUiState.DeleteFailure){
                    String errorMessage = ((DetailProductUiState.DeleteFailure) state).getErrorMessage();

                    hideLoading();

                    Utils.showToast(getContext(), ToastPerfect.ERROR,errorMessage);
                }
            }
        });

        detailProductViewModel.productLiveData.observe(getViewLifecycleOwner(), new Observer<Product>() {
            @Override
            public void onChanged(Product product) {
                photoAdapter = new PhotoAdapter(getContext(),product.getImages());
                binding.viewPager.setAdapter(photoAdapter);
                binding.circleIndicator.setViewPager(binding.viewPager);
                photoAdapter.registerDataSetObserver(binding.circleIndicator.getDataSetObserver());

                binding.txtName.setText(product.getName());
                binding.txtDescription.setText(product.getDescription());
                binding.txtPrice.setText(Double.toString(product.getPrice()));
                binding.txtUnit.setText(product.getUnit());
                binding.txtDiscount.setText(Double.toString(product.getDiscount()));
            }
        });
    }
}