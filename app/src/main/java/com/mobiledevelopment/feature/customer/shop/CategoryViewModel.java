package com.mobiledevelopment.feature.customer.shop;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mobiledevelopment.core.models.Category;
import com.mobiledevelopment.core.repository.CategoryRepository;

import java.util.List;

public class CategoryViewModel extends ViewModel {
    public static final String TAG = "CategoryViewModel";
    private MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>();
    private CategoryRepository categoryRepository;
    public MutableLiveData<List<Category>> getCategoriesLiveData(){
        return categoriesLiveData;
    }

    public CategoryViewModel(CategoryRepository categoryRepository){
        this.categoryRepository = categoryRepository;
    }

    public void initData(){
        categoryRepository.getCategories(new CategoryRepository.OnGetCategoriesCompleteListener() {
            @Override
            public void onComplete(List<Category> newCategories) {
                categoriesLiveData.setValue(newCategories);
            }
        });
    }

    public static class CategoryViewModelFactory implements ViewModelProvider.Factory {
        private final CategoryRepository categoryRepository;

        public CategoryViewModelFactory(CategoryRepository categoryRepository) {
            this.categoryRepository = categoryRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new CategoryViewModel(new CategoryRepository());
        }
    }

}
