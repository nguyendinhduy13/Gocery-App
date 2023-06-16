package com.mobiledevelopment.feature.admin.product;

import android.Manifest;
import android.net.Uri;
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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.repository.ProductRepository;
import com.mobiledevelopment.core.utils.Utils;
import com.mobiledevelopment.databinding.FragmentEditProductBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import vn.thanguit.toastperfect.ToastPerfect;

public class EditProductFragment extends Fragment {
    public static final String TAG = "EditProductFragment";

    private FragmentEditProductBinding binding;
    private EditProductViewModel editProductViewModel;

    private String idProduct;

    private List<byte[]> dataImages = new ArrayList<>();

    private ImageAdapter imageAdapter;

    private List<Uri> uris = new ArrayList<>();

    private List<Uri> productUris = new ArrayList<>();

    public EditProductFragment() {
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
        editProductViewModel = new ViewModelProvider(requireActivity(), new EditProductViewModel.EditProductViewModelFactory(new ProductRepository())).get(EditProductViewModel.class);
        binding = FragmentEditProductBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpUI();
        observeViewModel();
    }

    private void setUpUI(){
        editProductViewModel.setCurrentProductId(idProduct);

        initRcvImages();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).popBackStack();
            }
        });

        binding.btnEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProductUiState state = editProductViewModel.uiState.getValue();
                Product product = ((EditProductUiState.FetchDataSuccess) state).getData();

                String name = binding.edtNameProduct.getText().toString().trim();
                String description = binding.edtDescription.getText().toString().trim();
                double price = Double.parseDouble(binding.edtPrice.getText().toString().trim());
                String unit = binding.edtUnit.getText().toString().trim();
                double discount = Double.parseDouble(binding.edtDiscount.getText().toString().trim());
                double quantity = Double.parseDouble(binding.edtQuantity.getText().toString().trim());

                product.setName(name);
                product.setDescription(description);
                product.setPrice(price);
                product.setDiscount(discount);
                product.setUnit(unit);
                product.setQuantity(quantity);

                try {
                    for (Uri uri:uris) {
                        InputStream iStream =  getContext().getContentResolver().openInputStream(uri);

                        byte[] inputData = getBytes(iStream);

                        dataImages.add(inputData);
                    }

                } catch (IOException e) {
                    Log.d(TAG,e.toString());
                    throw new RuntimeException(e);
                }

                editProductViewModel.updateProduct(product, dataImages);
            }
        });
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
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

                            for (Uri uri: productUris) {
                                uris.add(uri);
                            }

                            imageAdapter.submitList(uris);
                        }
                    }
                });
    }

    void showLoading(){
        binding.pbLoading.setVisibility(View.VISIBLE);
        binding.btnEditProduct.setVisibility(View.GONE);
    }

    void hideLoading(){
        binding.pbLoading.setVisibility(View.GONE);
        binding.btnEditProduct.setVisibility(View.VISIBLE);
    }

    private void observeViewModel(){
        editProductViewModel.uiState.observe(getViewLifecycleOwner(), new Observer<EditProductUiState>() {
            @Override
            public void onChanged(EditProductUiState state) {
                if(state instanceof EditProductUiState.Initial || state instanceof EditProductUiState.Loading){
                    showLoading();
                }else if(state instanceof EditProductUiState.EditSuccess){
                    hideLoading();
                    Navigation.findNavController(getView()).popBackStack();
                    Utils.showToast(getContext(), ToastPerfect.SUCCESS,"Add product successfully");
                }else if(state instanceof EditProductUiState.EditFailure){
                    hideLoading();
                    String errorMessage = ((EditProductUiState.EditFailure) state).getErrorMessage();
                    Utils.showToast(getContext(), ToastPerfect.ERROR,errorMessage);
                }else if(state instanceof EditProductUiState.FetchDataSuccess){
                    hideLoading();

                    Product product = ((EditProductUiState.FetchDataSuccess) state).getData();

                    binding.edtNameProduct.setText(product.getName());
                    binding.edtDescription.setText(product.getDescription());
                    binding.edtPrice.setText(Double.toString(product.getPrice()));
                    binding.edtUnit.setText(product.getUnit());
                    binding.edtDiscount.setText(Double.toString(product.getDiscount()));
                    binding.edtQuantity.setText(Double.toString(product.getQuantity()));


                    for (String url: product.getImages()) {
                        Uri uri =  Uri.parse( url );
                        productUris.add(uri);
                    }

                    imageAdapter.submitList(productUris);
                }
            }
        });
    }
}