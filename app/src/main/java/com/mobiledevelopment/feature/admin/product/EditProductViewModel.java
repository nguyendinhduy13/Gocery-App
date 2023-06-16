package com.mobiledevelopment.feature.admin.product;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.repository.ProductRepository;

import java.util.List;

public class EditProductViewModel extends ViewModel {
    public static final String TAG = "EditProductViewModel";
    private ProductRepository productRepository;
    private final MutableLiveData<EditProductUiState> _uiState = new MutableLiveData<>();
    public final LiveData<EditProductUiState> uiState = _uiState;

    private MutableLiveData<Product> _product = new MutableLiveData<>();
    public LiveData<Product> productLiveData = _product;

    private final MutableLiveData<String> _currentProductId = new MutableLiveData<>("");

    public EditProductViewModel(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void resetData() {
        _uiState.setValue(new EditProductUiState.Initial());
        _currentProductId.setValue("");
    }

    public void setCurrentProductId(String id) {
        _uiState.setValue(new EditProductUiState.Loading());

        _currentProductId.setValue(id);

        getCurrentProduct();
    }

    private void getCurrentProduct() {
        productRepository.getProduct(
                _currentProductId.getValue(), new ProductRepository.OnGetProductListener() {
                    @Override
                    public void onComplete(Product product) {
                        _product.setValue(product);
                        _uiState.setValue(new EditProductUiState.FetchDataSuccess(product));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        _uiState.setValue(new EditProductUiState.FetchDataFailure(errorMessage));
                    }
                });

        Log.d(
                TAG,
                "resetCurrentCategory() - _currentCategory.value = " + _uiState.getValue());
    }

    public void updateProduct(Product newProduct, List<byte[]> dataImages) {
        _uiState.setValue(new EditProductUiState.Loading());

        if (dataImages == null) {
            productRepository.updateProduct(new ProductRepository.OnUpdateProductListener() {
                @Override
                public void onComplete() {
                    _uiState.setValue(new EditProductUiState.EditSuccess());
                }

                @Override
                public void onError(String errorMessage) {
                    _uiState.setValue(new EditProductUiState.EditFailure(errorMessage));
                }
            }, newProduct);
        }else{
            productRepository.updateProductWithNewImage(new ProductRepository.OnUpdateProductListener() {
                @Override
                public void onComplete() {
                    _uiState.setValue(new EditProductUiState.EditSuccess());
                }

                @Override
                public void onError(String errorMessage) {
                    _uiState.setValue(new EditProductUiState.EditFailure(errorMessage));
                }
            }, newProduct,dataImages);
        }
    }

    public static class EditProductViewModelFactory implements ViewModelProvider.Factory {
        private final ProductRepository productRepository;

        public EditProductViewModelFactory(ProductRepository productRepository) {
            this.productRepository = productRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(EditProductViewModel.class)) {
                @SuppressWarnings("unchecked")
                T result = (T) new EditProductViewModel(productRepository);
                return result;
            }
            throw new IllegalArgumentException(
                    "AddEditAddressViewModelFactory.create() - Unknown ViewModel class: " + modelClass);
        }
    }
}

abstract class EditProductUiState {
    static class Loading extends EditProductUiState {
    }

    static class Initial extends EditProductUiState {
    }

    static class EditSuccess extends EditProductUiState {
    }

    static class EditFailure extends EditProductUiState {
        private final String errorMessage;

        public EditFailure(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    static class FetchDataSuccess extends EditProductUiState {
        private final Product data;

        public FetchDataSuccess(Product data) {
            this.data = data;
        }

        public Product getData() {
            return data;
        }
    }

    static class FetchDataFailure extends EditProductUiState {
        private final String errorMessage;

        public FetchDataFailure(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}