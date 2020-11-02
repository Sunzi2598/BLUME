package com.company.blumeSunzi.Callback;

import com.company.blumeSunzi.Database.CartItem;
import com.company.blumeSunzi.Model.CategoryModel;
import com.company.blumeSunzi.Model.FlowerModel;

public interface ISearchCategoryCallbackListener {
    void onSearchCategoryFound(CategoryModel categoryModel, CartItem cartItem);
    void onSearchCategoryNotFound(String message);
}
