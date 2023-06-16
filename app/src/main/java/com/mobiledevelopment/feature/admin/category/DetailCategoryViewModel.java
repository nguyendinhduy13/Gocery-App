package com.mobiledevelopment.feature.admin.category;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.mobiledevelopment.core.models.Category;
import com.mobiledevelopment.core.repository.CategoryRepository;

public class DetailCategoryViewModel extends ViewModel {
    public static final String TAG = "AddEditCatViewModel";
    private CategoryRepository categoryRepository;

    private MutableLiveData<Category> _category = new MutableLiveData<>();
    public LiveData<Category>categoryLiveData = _category;

    private MutableLiveData<Uri> uriLiveData = new MutableLiveData<>();
    private final MutableLiveData<DetailCategoryUiState> _uiState = new MutableLiveData<>();
    public final LiveData<DetailCategoryUiState> uiState = _uiState;
    private final MutableLiveData<String> _currentCategoryId = new MutableLiveData<>("");

    public DetailCategoryViewModel(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void resetData() {
        uriLiveData = new MutableLiveData<>();
        _uiState.setValue(new DetailCategoryUiState.Initial());
        _currentCategoryId.setValue("");
    }

    public void setUri(Uri uri) {
        uriLiveData.setValue(uri);
    }

    public MutableLiveData<Uri> getUriLiveData() {
        return uriLiveData;
    }

    public void removeImage() {
        setUri(null);
    }
    public void setCurrentCategoryId(String id) {
        _uiState.setValue(new DetailCategoryUiState.Loading());

        _currentCategoryId.setValue(id);

        resetCurrentCategory();
    }

    private void resetCurrentCategory() {
        categoryRepository.getCategory(
                _currentCategoryId.getValue(), new CategoryRepository.OnGetCategoryListener() {
                    @Override
                    public void onComplete(Category category) {
                        _category.setValue(category);
                        _uiState.setValue(new DetailCategoryUiState.FetchDataSuccess(category));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        _uiState.setValue(new DetailCategoryUiState.FetchDataFailure(errorMessage));
                    }
                });

        Log.d(
                TAG,
                "resetCurrentCategory() - _currentCategory.value = " + _uiState.getValue());
    }

    public void addCategory(String name, byte[] dataImage) {
        _uiState.setValue(new DetailCategoryUiState.Loading());

        Category category = new Category();
        category.setName(name);

        categoryRepository.addCategory(
                messageError -> {
                    _uiState.setValue(new DetailCategoryUiState.AddFailure(messageError));
                },
                idCategory -> {
                    category.setId(idCategory);
                    _uiState.setValue(new DetailCategoryUiState.AddSuccess(idCategory));
                }
                , name, dataImage);
    }

    public void deleteCategory(String idCategory) {
        categoryRepository.deleteCategory(new CategoryRepository.OnDeleteCategoryListener() {
            @Override
            public void onComplete() {
                _uiState.setValue(new DetailCategoryUiState.DeleteSuccess());
            }

            @Override
            public void onError(String errorMessage) {
                _uiState.setValue(new DetailCategoryUiState.DeleteFailure(errorMessage));
            }
        }, idCategory);

    }

    public void updateCategory(Category newCategory, byte[] dataImage) {
        _uiState.setValue(new DetailCategoryUiState.Loading());

        if (dataImage == null) {
            categoryRepository.updateCategory(new CategoryRepository.OnUpdateCategoryListener() {
                @Override
                public void onComplete() {
                    _uiState.setValue(new DetailCategoryUiState.EditSuccess());
                }

                @Override
                public void onError(String errorMessage) {
                    _uiState.setValue(new DetailCategoryUiState.EditFailure(errorMessage));
                }
            }, newCategory);
        }else{
            categoryRepository.updateCategoryWithNewImage(new CategoryRepository.OnUpdateCategoryListener() {
                @Override
                public void onComplete() {
                    _uiState.setValue(new DetailCategoryUiState.EditSuccess());
                }

                @Override
                public void onError(String errorMessage) {
                    _uiState.setValue(new DetailCategoryUiState.EditFailure(errorMessage));
                }
            }, newCategory,dataImage);
        }
    }

    public static class DetailCategoryViewModelFactory implements ViewModelProvider.Factory {
        private final CategoryRepository categoryRepository;

        public DetailCategoryViewModelFactory(CategoryRepository categoryRepository) {
            this.categoryRepository = categoryRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(DetailCategoryViewModel.class)) {
                @SuppressWarnings("unchecked")
                T result = (T) new DetailCategoryViewModel(categoryRepository);
                return result;
            }
            throw new IllegalArgumentException(
                    "AddEditAddressViewModelFactory.create() - Unknown ViewModel class: " + modelClass);
        }
    }
}

abstract class DetailCategoryUiState {
    static class Loading extends DetailCategoryUiState {
    }

    static class Initial extends DetailCategoryUiState {
    }

    static class InAddMode extends DetailCategoryUiState {
    }

    static class FetchDataSuccess extends DetailCategoryUiState {
        private final Category data;

        public FetchDataSuccess(Category data) {
            this.data = data;
        }

        public Category getData() {
            return data;
        }
    }

    static class FetchDataFailure extends DetailCategoryUiState {
        private final String errorMessage;

        public FetchDataFailure(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    static class AddSuccess extends DetailCategoryUiState {
        private final String categoryId;

        public AddSuccess(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getData() {
            return categoryId;
        }
    }

    static class AddFailure extends DetailCategoryUiState {
        private final String errorMessage;

        public AddFailure(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    static class EditSuccess extends DetailCategoryUiState {
    }

    static class EditFailure extends DetailCategoryUiState {
        private final String errorMessage;

        public EditFailure(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    static class DeleteSuccess extends DetailCategoryUiState {
    }

    static class DeleteFailure extends DetailCategoryUiState {
        private final String errorMessage;

        public DeleteFailure(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
