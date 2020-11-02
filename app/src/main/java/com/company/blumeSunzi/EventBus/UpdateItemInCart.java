package com.company.blumeSunzi.EventBus;

import com.company.blumeSunzi.Database.CartItem;

public class UpdateItemInCart {
    private CartItem cartItem;

    public CartItem getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    public UpdateItemInCart(CartItem cartItem) {
        this.cartItem = cartItem;
    }
}