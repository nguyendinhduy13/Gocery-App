package com.mobiledevelopment.feature.admin.product;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mobiledevelopment.core.models.Category;
import com.mobiledevelopment.core.models.Product;
import com.mobiledevelopment.core.repository.CategoryRepository;
import com.mobiledevelopment.core.repository.ProductRepository;
import com.mobiledevelopment.feature.customer.shop.CategoryViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProductViewModel extends ViewModel {
    public static final String TAG = "ProductViewModel";
    private MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Product>> searchLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Product>> filterLiveData = new MutableLiveData<>();
    private ProductRepository productRepository;
    public MutableLiveData<List<Product>> getProductsLiveData(){
        return productsLiveData;
    }

    public MutableLiveData<List<Product>> getSearchLiveData() {
        return searchLiveData;
    }

    public MutableLiveData<List<Product>> getFilterLiveData() {
        return filterLiveData;
    }

    public ProductViewModel(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    public void getProducts(String idCategory){
        productRepository.getProducts(idCategory, new ProductRepository.OnGetProductsCompleteListener() {
            @Override
            public void onComplete(List<Product> products) {
                productsLiveData.setValue(products);
            }
        });
    }

    public void sortProducts(int type){
        if(type == 2){
            // highest
            List<Product> clonedProducts = productsLiveData.getValue();

            Collections.sort(clonedProducts, new Comparator<Product>() {
                @Override
                public int compare(Product product1, Product product2) {
                    return Double.compare(product2.getPrice(),product1.getPrice());
                }
            });

            productsLiveData.setValue(clonedProducts);

        }else if(type == 3){
            // lowest
            List<Product> clonedProducts = productsLiveData.getValue();

            Collections.sort(clonedProducts, new Comparator<Product>() {
                @Override
                public int compare(Product product1, Product product2) {
                    return Double.compare(product1.getPrice(),product2.getPrice());
                }
            });

            productsLiveData.setValue(clonedProducts);
        }
    }

    public void filterProduct(int type) {
        List<Product> clonedProducts = productsLiveData.getValue();
        List<Product> filterResults = new ArrayList<>();
        if(type==1){
            for (Product product : clonedProducts) {
                if(product.getPrice()>0&&product.getPrice()<=50000){
                    filterResults.add(product);
                }
            }
            filterLiveData.setValue(filterResults);
        }
        if(type==2){
            for (Product product : clonedProducts) {
                if(product.getPrice()>=50000&&product.getPrice()<=150000){
                    filterResults.add(product);
                }
            }
            filterLiveData.setValue(filterResults);
        }
        if(type==3){
            for (Product product : clonedProducts) {
                if(product.getPrice()>150000){
                    filterResults.add(product);
                }
            }
            filterLiveData.setValue(filterResults);
        }
    }

    public void searchProduct(String keyword){
        List<Product> clonedProducts = productsLiveData.getValue();
        List<Product> searchResults = new ArrayList<>();

        for (Product product : clonedProducts) {
            if (product.getName().toLowerCase().contains(keyword)) {
                searchResults.add(product);
            }
        }
        searchLiveData.setValue(searchResults);
    }

    public static class ProductViewModelFactory implements ViewModelProvider.Factory {
        private final ProductRepository productRepository;

        public ProductViewModelFactory(ProductRepository productRepository) {
            this.productRepository = productRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ProductViewModel(new ProductRepository());
        }
    }

}
