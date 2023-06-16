package com.mobiledevelopment.feature.admin.product;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.repository.ProductRepository;

public class DetailProductViewModel extends ViewModel {
    public static final String TAG = "DetailProductViewModel";
    private ProductRepository productRepository;
    private final MutableLiveData<DetailProductUiState> _uiState = new MutableLiveData<>();
    public final LiveData<DetailProductUiState> uiState = _uiState;

    private MutableLiveData<Product> _product = new MutableLiveData<>();
    public LiveData<Product> productLiveData = _product;

    private final MutableLiveData<String> _currentProductId = new MutableLiveData<>("");

    public DetailProductViewModel(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void resetData() {
        _uiState.setValue(new DetailProductUiState.Initial());
        _currentProductId.setValue("");
    }

    public void setCurrentProductId(String id) {
        _uiState.setValue(new DetailProductUiState.Loading());

        _currentProductId.setValue(id);

        getCurrentProduct();
    }

    private void getCurrentProduct() {
        productRepository.getProduct(
                _currentProductId.getValue(), new ProductRepository.OnGetProductListener() {
                    @Override
                    public void onComplete(Product product) {
                        _product.setValue(product);
                        _uiState.setValue(new DetailProductUiState.FetchDataSuccess(product));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        _uiState.setValue(new DetailProductUiState.FetchDataFailure(errorMessage));
                    }
                });

        Log.d(
                TAG,
                "resetCurrentCategory() - _currentCategory.value = " + _uiState.getValue());
    }

    public void deleteProduct(String idProduct) {
        productRepository.deleteProduct(new ProductRepository.OnDeleteProductListener() {
            @Override
            public void onComplete() {
                _uiState.setValue(new DetailProductUiState.DeleteSuccess());
            }

            @Override
            public void onError(String errorMessage) {
                _uiState.setValue(new DetailProductUiState.DeleteFailure(errorMessage));
            }
        }, idProduct);
    }

    public static class DetailProductViewModelFactory implements ViewModelProvider.Factory {
        private final ProductRepository productRepository;

        public DetailProductViewModelFactory(ProductRepository productRepository) {
            this.productRepository = productRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(DetailProductViewModel.class)) {
                @SuppressWarnings("unchecked")
                T result = (T) new DetailProductViewModel(productRepository);
                return result;
            }
            throw new IllegalArgumentException(
                    "AddEditAddressViewModelFactory.create() - Unknown ViewModel class: " + modelClass);
        }
    }
}

abstract class DetailProductUiState {
    static class Loading extends DetailProductUiState {
    }

    static class Initial extends DetailProductUiState {
    }

    static class DeleteSuccess extends DetailProductUiState {
    }

    static class DeleteFailure extends DetailProductUiState {
        private final String errorMessage;

        public DeleteFailure(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    static class FetchDataSuccess extends DetailProductUiState {
        private final Product data;

        public FetchDataSuccess(Product data) {
            this.data = data;
        }

        public Product getData() {
            return data;
        }
    }

    static class FetchDataFailure extends DetailProductUiState {
        private final String errorMessage;

        public FetchDataFailure(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}