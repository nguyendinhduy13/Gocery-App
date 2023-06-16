package com.mobiledevelopment.feature.admin.product;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.repository.ProductRepository;

import java.util.List;

public class AddProductViewModel extends ViewModel {
    public static final String TAG = "AddProductViewModel";
    private ProductRepository productRepository;
    private final MutableLiveData<AddProductUiState> _uiState = new MutableLiveData<>();
    public final LiveData<AddProductUiState> uiState = _uiState;

    public AddProductViewModel(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public void resetData(){
        _uiState.setValue(new AddProductUiState.Initial());
    }

    public void addProduct(String nameProduct, String description, String categoryName, double price, String unit, double discount, double quantity, List<byte[]> dataImages) {
        _uiState.setValue(new AddProductUiState.Loading());

        Product product = new Product();

        product.setName(nameProduct);
        product.setDiscount(discount);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setUnit(unit);
        product.setIdCategory(categoryName);
        product.setDescription(description);

        productRepository.addProduct(new ProductRepository.OnAddProductListener() {
            @Override
            public void onComplete(String idProduct) {
                _uiState.setValue(new AddProductUiState.AddSuccess(idProduct));
            }

            @Override
            public void onError(String errorMessage) {
                _uiState.setValue(new AddProductUiState.AddFailure(errorMessage));
            }
        },product,dataImages);
    }

    public static class AddEditProductViewModelFactory implements ViewModelProvider.Factory {
        private final ProductRepository productRepository;

        public AddEditProductViewModelFactory(ProductRepository productRepository) {
            this.productRepository = productRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(AddProductViewModel.class)) {
                @SuppressWarnings("unchecked")
                T result = (T) new AddProductViewModel(productRepository);
                return result;
            }
            throw new IllegalArgumentException(
                    "AddEditProductViewModelFactory.create() - Unknown ViewModel class: " + modelClass);
        }
    }
}

abstract class AddProductUiState {
    static class Loading extends AddProductUiState {
    }

    static class Initial extends AddProductUiState {
    }

    static class AddSuccess extends AddProductUiState {
        private final String categoryId;

        public AddSuccess(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getData() {
            return categoryId;
        }
    }

    static class AddFailure extends AddProductUiState {
        private final String errorMessage;

        public AddFailure(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    static class EditSuccess extends AddProductUiState {
    }

    static class EditFailure extends AddProductUiState {
        private final String errorMessage;

        public EditFailure(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    static class DeleteSuccess extends AddProductUiState {
    }

    static class DeleteFailure extends AddProductUiState {
        private final String errorMessage;

        public DeleteFailure(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
