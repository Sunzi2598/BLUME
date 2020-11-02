package com.company.blumeSunzi.EventBus;

import com.company.blumeSunzi.Model.CategoryModel;

public class CategoryClick {
    private boolean success;
    private CategoryModel categoryModel;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public void setCategoryModel(CategoryModel categoryModel) {
        this.categoryModel = categoryModel;
    }

    public CategoryClick(boolean success, CategoryModel categoryModel) {
        this.success = success;
        this.categoryModel = categoryModel;
    }
}