package com.company.blumeSunzi.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface CartDAO {
    @Query("SELECT * FROM Cart WHERE uid=:uid")
    Flowable<List<CartItem>> getAllCart(String uid);

    @Query("SELECT SUM(flowerQuantity) FROM Cart WHERE uid=:uid")
    Single<Integer> countItemInCart (String uid);

    @Query("SELECT SUM((flowerPrice+flowerExtraPrice) * flowerQuantity) FROM CART WHERE uid=:uid")
    Single<Double> sumPriceInCart (String uid);

    @Query("SELECT * FROM Cart WHERE flowerId=:flowerId AND uid=:uid")
    Single<CartItem> getItemInCart (String flowerId, String uid);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(CartItem... cartItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCartItems(CartItem cartItem);

    @Delete
    Single<Integer> deleteCartItem(CartItem cartItem);

    @Query("DELETE FROM Cart WHERE uid=:uid")
    Single<Integer> cleanCart (String uid);

    @Query("SELECT * FROM Cart WHERE categoryId=:categoryId AND flowerAddon=:flowerAddon AND flowerId=:flowerId AND uid=:uid ")
    Single<CartItem> getItemWithAllOptionsInCart (String uid, String categoryId, String flowerId, String flowerAddon);



}
