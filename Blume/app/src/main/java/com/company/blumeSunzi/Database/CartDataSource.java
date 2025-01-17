package com.company.blumeSunzi.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CartDataSource {

    Flowable<List<CartItem>> getAllCart(String uid);

    Single<Integer> countItemInCart (String uid);

    Single<Double> sumPriceInCart (String uid);

    Single<CartItem> getItemInCart (String flowerId, String uid);

    Completable insertOrReplaceAll(CartItem... cartItems);

    Single<Integer> updateCartItems(CartItem cartItem);

    Single<Integer> deleteCartItem(CartItem cartItem);

    Single<Integer> cleanCart (String uid);

    Single<CartItem> getItemWithAllOptionsInCart (String uid, String categoryId,String flowerId, String flowerAddon);
}
