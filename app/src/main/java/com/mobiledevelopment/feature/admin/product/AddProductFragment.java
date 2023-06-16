package com.mobiledevelopment.feature.admin.product;

import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.mobiledevelopment.core.models.Category;
import com.mobiledevelopment.core.repository.CategoryRepository;
import com.mobiledevelopment.core.repository.ProductRepository;
import com.mobiledevelopment.core.utils.Utils;
import com.mobiledevelopment.databinding.FragmentAddProductBinding;
import com.mobiledevelopment.feature.admin.category.DetailCategoryViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import vn.thanguit.toastperfect.ToastPerfect;

public class AddProductFragment extends Fragment {
    public static final String TAG = "AddProductFragment";
    private FragmentAddProductBinding binding;
    private ImageAdapter imageAdapter;
    private DetailCategoryViewModel detailCategoryViewModel;
    private AddProductViewModel addEditProductViewModel;

    private List<Uri> uris = new ArrayList<>();

    public AddProductFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        detailCategoryViewModel = new ViewModelProvider(
                requireActivity(),
                new DetailCategoryViewModel.DetailCategoryViewModelFactory(new CategoryRepository())).get(DetailCategoryViewModel.class);

        addEditProductViewModel = new ViewModelProvider(
                requireActivity(),
                new AddProductViewModel.AddEditProductViewModelFactory(new ProductRepository())).get(AddProductViewModel.class);

        binding = FragmentAddProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addEditProductViewModel.resetData();

        setUpUI();
        observeViewModel();
    }

    void initRcvImages() {
        imageAdapter = new ImageAdapter(getContext(), new ImageAdapter.OnItemRemoveImageClickListener() {
            @Override
            public void onClick(int index) {
                imageAdapter.removeIndex(index);
                //requestPermission();
            }
        }, new ImageAdapter.OnItemAddImageClickListener() {
            @Override
            public void onClick() {
                requestPermission();
            }
        });

        binding.rcvImages.setAdapter(imageAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.rcvImages.setLayoutManager(layoutManager);
    }

    void setUpUI() {
        Category category = detailCategoryViewModel.categoryLiveData.getValue();
        binding.edtNameCategory.setText(category.getName());

        initRcvImages();

        binding.btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValidInput()) {
                    Utils.showToast(getContext(), ToastPerfect.ERROR, "Please fill full information");
                    return;
                }

                List<byte[]> dataImages = new ArrayList<>();

                for (Uri uri : uris) {
                    Bitmap image = null;

                    try {
                        image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        dataImages.add(baos.toByteArray());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                String nameProduct = binding.edtNameProduct.getText().toString();
                String description = binding.edtDescription.getText().toString();
                double price = Double.parseDouble(binding.edtPrice.getText().toString());
                String unit = binding.edtUnit.getText().toString();
                double discount = Double.parseDouble(binding.edtDiscount.getText().toString());
                double quantity = Double.parseDouble(binding.edtQuantity.getText().toString());

                addEditProductViewModel.addProduct(nameProduct,description,category.getId(),price,unit,discount,quantity,dataImages);
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).popBackStack();
            }
        });
    }

    boolean isValidInput() {
        String nameProduct = binding.edtNameProduct.getText().toString();
        String description = binding.edtDescription.getText().toString();
        String price = binding.edtPrice.getText().toString();
        String unit = binding.edtUnit.getText().toString();
        String discount = binding.edtDiscount.getText().toString();
        String quantity = binding.edtQuantity.getText().toString();

        if (nameProduct.isEmpty() || description.isEmpty() ||
                price.isEmpty() || unit.isEmpty() || discount.isEmpty() || quantity.isEmpty() || uris.isEmpty()) {
            return false;
        }
        return true;
    }

    void requestPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                pickImagesFromGallery();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

            }
        };

        TedPermission.create()
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    void pickImagesFromGallery() {
        TedBottomPicker.with(requireActivity())
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("No Select")
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {
                        if(uriList != null && !uriList.isEmpty()){
                            uris = uriList;
                            imageAdapter.submitList(uris);
                        }
                    }
                });
    }

    void observeViewModel() {
        addEditProductViewModel.uiState.observe(getViewLifecycleOwner(), new Observer<AddProductUiState>() {
            @Override
            public void onChanged(AddProductUiState state) {
                if(state instanceof AddProductUiState.Loading){
                    showLoading();
                }else if(state instanceof AddProductUiState.AddSuccess){
                    hideLoading();
                    Utils.showToast(getContext(), ToastPerfect.SUCCESS,"Add product successfully");
                }else if(state instanceof AddProductUiState.AddFailure){
                    String errorMessage =  ((AddProductUiState.AddFailure)state).getErrorMessage();
                    hideLoading();
                    Utils.showToast(getContext(),ToastPerfect.ERROR,errorMessage);
                }
            }
        });
    }

    void showLoading(){
        binding.pbLoading.setVisibility(View.VISIBLE);
        binding.btnAddProduct.setVisibility(View.GONE);
    }

    void hideLoading(){
        binding.pbLoading.setVisibility(View.GONE);
        binding.btnAddProduct.setVisibility(View.VISIBLE);
    }

}