package com.mobiledevelopment.feature.customer.product.view;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.repository.CartRepository;
import com.mobiledevelopment.core.repository.ProductRepository;

import java.util.Objects;

class DetailProductViewModel extends ViewModel {
    public static final String TAG = "DetailProductViewModel";
    private final ProductRepository productRepository;
    private final CartRepository cartRepository = new CartRepository();
    private final MutableLiveData<Product> _product = new MutableLiveData<>();
    private final MutableLiveData<Integer> _productCount = new MutableLiveData<>(1);
    private final MutableLiveData<String> _currentProductId = new MutableLiveData<>("");
    public LiveData<Product> productLiveData = _product;
    public LiveData<Integer> productCount = _productCount;
    private String userId = "";

    public DetailProductViewModel(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void setCurrentProductId(String id) {
        _currentProductId.setValue(id);
        getCurrentProduct();
    }

    public void incrementProductCount() {
        if (_productCount.getValue() != null &&
                _productCount.getValue() < Objects.requireNonNull(_product.getValue())
                        .getQuantity()) {
            _productCount.setValue(_productCount.getValue() + 1);
        }
    }

    public void decrementProductCount() {
        if (_productCount.getValue() != null &&
                _productCount.getValue() > 1) {
            _productCount.setValue(_productCount.getValue() - 1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void addProductToCart(CartRepository.OnAddProductToCartCompleteListener onCompleteListener) {
        cartRepository.addProductToCart(
                userId,
                new Product.ProductInCart(
                        Objects.requireNonNull(_product.getValue()),
                        _productCount.getValue()),
                onCompleteListener);
    }

    private void getCurrentProduct() {
        productRepository.getProduct(
                _currentProductId.getValue(), new ProductRepository.OnGetProductListener() {
                    @Override
                    public void onComplete(Product product) {
                        _product.setValue(product);
                    }

                    @Override
                    public void onError(String errorMessage) {
                    }
                });
    }

    public void setCurrentUserId(String userId) {
        this.userId = userId;
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
                    "DetailProductViewModelFactory.create() - Unknown ViewModel class: " + modelClass);
        }
    }
}
