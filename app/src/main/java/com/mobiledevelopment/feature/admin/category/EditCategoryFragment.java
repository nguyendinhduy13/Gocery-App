package com.mobiledevelopment.feature.admin.category;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import com.bumptech.glide.Glide;
import com.mobiledevelopment.core.models.Category;
import com.mobiledevelopment.core.repository.CategoryRepository;
import com.mobiledevelopment.core.utils.Utils;
import com.mobiledevelopment.databinding.FragmentEditCategoryBinding;
import com.mobiledevelopment.databinding.ItemAddImageBinding;
import com.mobiledevelopment.databinding.ItemImageBinding;

import java.io.ByteArrayOutputStream;

import vn.thanguit.toastperfect.ToastPerfect;

public class EditCategoryFragment extends Fragment {

    public static final String TAG = "EditCategoryFragment";
    private FragmentEditCategoryBinding binding;
    private String idCategory;
    private DetailCategoryViewModel detailCategoryViewModel;
    private final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 1;
    private byte[] dataImage;

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e(TAG, "on Activity Result");

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // there are no request codes
                        Intent data = result.getData();

                        if (data == null) return;
                        Uri uri = data.getData();
                        detailCategoryViewModel.setUri(uri);
                    }
                }
            }

    );

    public EditCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            idCategory = bundle.getString("idCategory");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        detailCategoryViewModel = new ViewModelProvider(
                requireActivity(),
                new DetailCategoryViewModel.DetailCategoryViewModelFactory(new CategoryRepository())).get(DetailCategoryViewModel.class);
        binding = FragmentEditCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpUI();
        observeViewModel();
    }

    void pickImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    void setUpUI() {
        detailCategoryViewModel.setCurrentCategoryId(idCategory);

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).popBackStack();
            }
        });

        binding.btnUpdateCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailCategoryUiState state = detailCategoryViewModel.uiState.getValue();
                Category category = ((DetailCategoryUiState.FetchDataSuccess) state).getData();
                String name = binding.edtNameCategory.getText().toString();

                category.setName(name);
                detailCategoryViewModel.updateCategory(category, dataImage);
            }
        });

        binding.btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
    }

    void showLoading() {
        binding.pbLoading.setVisibility(View.VISIBLE);
        binding.btnUpdateCategory.setVisibility(View.GONE);
    }

    void hideLoading() {
        binding.pbLoading.setVisibility(View.GONE);
        binding.btnUpdateCategory.setVisibility(View.VISIBLE);
    }

    void observeViewModel() {
        detailCategoryViewModel.uiState.observe(getViewLifecycleOwner(), new Observer<DetailCategoryUiState>() {
            @Override
            public void onChanged(DetailCategoryUiState state) {
                if (state instanceof DetailCategoryUiState.FetchDataSuccess) {
                    hideLoading();

                    Category category = ((DetailCategoryUiState.FetchDataSuccess) state).getData();

                    ItemImageBinding itemImageBinding = binding.layoutItemImage;

                    binding.edtNameCategory.setText(category.getName());

                    Glide
                            .with(getContext())
                            .load(category.getImage())
                            .centerCrop()
                            .into(itemImageBinding.imgCategory);

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

                } else if (state instanceof DetailCategoryUiState.FetchDataFailure) {
                    hideLoading();

                    String errorMessage = ((DetailCategoryUiState.FetchDataFailure) state).getErrorMessage();

                    Utils.showToast(getContext(), ToastPerfect.ERROR, errorMessage);
                } else if (state instanceof DetailCategoryUiState.Loading) {
                    showLoading();
                } else if (state instanceof DetailCategoryUiState.EditSuccess) {
                    Navigation.findNavController(getView()).popBackStack();
                    Utils.showToast(getContext(), ToastPerfect.SUCCESS, "Edit category successfully");
                } else if (state instanceof DetailCategoryUiState.EditFailure) {
                    hideLoading();
                    String errorMessage = ((DetailCategoryUiState.EditFailure) state).getErrorMessage();
                    Utils.showToast(getContext(), ToastPerfect.ERROR, errorMessage);
                }
            }
        });

        detailCategoryViewModel.getUriLiveData().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                if (uri != null) {
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
                } else {
                    ItemAddImageBinding itemAddImageBinding = binding.layoutItemAddImage;
                    itemAddImageBinding.wrapper.setVisibility(View.VISIBLE);

                    ItemImageBinding itemImageBinding = binding.layoutItemImage;
                    itemImageBinding.wrapper.setVisibility(View.GONE);
                }
            }
        });
    }
}