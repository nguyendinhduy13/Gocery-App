package com.mobiledevelopment.feature.customer.shop;

import com.mobiledevelopment.core.models.Category;

abstract public class CategoryState {

    public static class DeleteCategorySuccess extends CategoryState{}
    public static class AddCategorySuccess extends CategoryState{
        private final Category newCategory;

        public AddCategorySuccess(Category newCategory) {
            this.newCategory = newCategory;
        }

        public Category getNewCategory() {
            return newCategory;
        }
    }

    public static class AddCategoryFailure extends CategoryState{
        private final String errorMessage;

        public AddCategoryFailure(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public static class DeleteCategoryFailure extends CategoryState{
        private final String errorMessage;

        public DeleteCategoryFailure(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }

    public static class FetchCategoriesSuccess extends CategoryState{}

    public static class FetchCategoriesFailure extends CategoryState{}
    public static class Loading extends CategoryState {}

}
