package com.mobiledevelopment.feature.admin.category;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobiledevelopment.core.repository.CategoryRepository;
import com.mobiledevelopment.core.utils.Utils;
import com.mobiledevelopment.databinding.FragmentAddCategoryBinding;
import com.mobiledevelopment.databinding.ItemAddImageBinding;
import com.mobiledevelopment.databinding.ItemImageBinding;

import java.io.ByteArrayOutputStream;

import vn.thanguit.toastperfect.ToastPerfect;


public class AddCategoryFragment extends Fragment {

    public static final String TAG = "AddCategoryFragment";
    private FragmentAddCategoryBinding binding;
    private DetailCategoryViewModel detailCategoryViewModel;
    private final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 1;
    private byte[] dataImage;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "on Activity Result");

                    if(result.getResultCode() == Activity.RESULT_OK){
                        // there are no request codes
                        Intent data = result.getData();

                        if(data == null) return;
                        Uri uri = data.getData();
                        detailCategoryViewModel.setUri(uri);
                    }
                }
            }

    );
    public AddCategoryFragment() {
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
        detailCategoryViewModel = new ViewModelProvider(
                requireActivity(),
                new DetailCategoryViewModel.DetailCategoryViewModelFactory(new CategoryRepository())).get(DetailCategoryViewModel.class);
        binding =  FragmentAddCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        detailCategoryViewModel.resetData();

        setUpUI();
        observeViewModel();
    }

    void setUpUI(){
        binding.btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameCategory = binding.edtNameCategory.getText().toString();
                Uri uri = detailCategoryViewModel.getUriLiveData().getValue();

                if(nameCategory.isEmpty() || uri == null ){
                    Utils.showToast(getContext(), ToastPerfect.ERROR,"Name or image can't be empty");
                    return;
                }

                detailCategoryViewModel.addCategory(nameCategory,dataImage);
            }
        });

        binding.btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).popBackStack();
            }
        });
    }

    void pickImage(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            openGallery();
            return;
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        }else{
            String[]permissions={Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(getActivity(), permissions,REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }

    void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent,"Select Picture"));
    }

    void observeViewModel(){
        detailCategoryViewModel.getUriLiveData().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                if(uri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        dataImage = baos.toByteArray();

                        ItemImageBinding itemImageBinding = binding.layoutItemImage;
                        itemImageBinding.imgCategory.setImageBitmap(bitmap);
                        itemImageBinding.wrapper.setVisibility(View.VISIBLE);
                        itemImageBinding.btnRemoveImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                detailCategoryViewModel.removeImage();
                            }
                        });

                        // hide btn add image
                        ItemAddImageBinding itemAddImageBinding = binding.layoutItemAddImage;
                        itemAddImageBinding.wrapper.setVisibility(View.GONE);
                    } catch (Exception e) {
                        Utils.showToast(getContext(), ToastPerfect.ERROR, e.getMessage());
                    }
                }else{
                    ItemAddImageBinding itemAddImageBinding = binding.layoutItemAddImage;
                    itemAddImageBinding.wrapper.setVisibility(View.VISIBLE);

                    ItemImageBinding itemImageBinding = binding.layoutItemImage;
                    itemImageBinding.wrapper.setVisibility(View.GONE);
                }
            }
        });

        detailCategoryViewModel.uiState.observe(getViewLifecycleOwner(), new Observer<DetailCategoryUiState>() {
            @Override
            public void onChanged(DetailCategoryUiState state) {
                if(state instanceof DetailCategoryUiState.Loading){
                    showLoading();
                }else if(state instanceof DetailCategoryUiState.AddSuccess){
                    hideLoading();
                    Utils.showToast(getContext(),ToastPerfect.SUCCESS,"Add category successfully");
                }else if(state instanceof DetailCategoryUiState.AddFailure){
                    String errorMessage =  ((DetailCategoryUiState.AddFailure)state).getErrorMessage();
                    hideLoading();
                    Utils.showToast(getContext(),ToastPerfect.ERROR,errorMessage);
                }
            }
        });
    }

    void showLoading(){
        binding.pbLoading.setVisibility(View.VISIBLE);
        binding.btnAddCategory.setVisibility(View.GONE);
    }

    void hideLoading(){
        binding.pbLoading.setVisibility(View.GONE);
        binding.btnAddCategory.setVisibility(View.VISIBLE);
    }
}