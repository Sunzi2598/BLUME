package com.company.blumeSunzi.Database;

import androidx.annotation.NonNull;


import androidx.annotation.Nullable;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "Cart",primaryKeys = {"uid","categoryId","flowerId", "flowerAddon"})
public class CartItem {

    @NonNull
    @ColumnInfo(name = "categoryId")
    private String categoryId;

    @NonNull
    @ColumnInfo(name = "flowerId")
    private String flowerId;

    @ColumnInfo(name = "flowerName")
    private String flowerName;

    @ColumnInfo(name = "flowerImage")
    private String flowerImage;

    @ColumnInfo(name = "flowerPrice")
    private double flowerPrice;

    @ColumnInfo(name = "flowerQuantity")
    private int flowerQuantity;

    @ColumnInfo(name = "userPhone")
    private String userPhone;

    @ColumnInfo(name = "flowerExtraPrice")
    private double flowerExtraPrice;

    @NonNull
    @ColumnInfo(name = "flowerAddon")
    private String flowerAddon;

    @NonNull
    @ColumnInfo(name = "uid")
    private String uid;


    @NonNull
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(@NonNull String categoryId) {
        this.categoryId = categoryId;
    }

    public String getFlowerId() {
        return flowerId;
    }

    public void setFlowerId(String flowerId) {
        this.flowerId = flowerId;
    }

    public String getFlowerName() {
        return flowerName;
    }

    public void setFlowerName(String flowerName) {
        this.flowerName = flowerName;
    }

    public String getFlowerImage() {
        return flowerImage;
    }

    public void setFlowerImage(String flowerImage) {
        this.flowerImage = flowerImage;
    }

    public double getFlowerPrice() {
        return flowerPrice;
    }

    public void setFlowerPrice(double flowerPrice) {
        this.flowerPrice = flowerPrice;
    }

    public int getFlowerQuantity() {
        return flowerQuantity;
    }

    public void setFlowerQuantity(int flowerQuantity) {
        this.flowerQuantity = flowerQuantity;
    }

    public double getFlowerExtraPrice() {
        return flowerExtraPrice;
    }

    public void setFlowerExtraPrice(double flowerExtraPrice) {
        this.flowerExtraPrice = flowerExtraPrice;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getFlowerAddon() {
        return flowerAddon;
    }

    public void setFlowerAddon(String flowerAddon) {
        this.flowerAddon = flowerAddon;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == this)
            return true;
        if(!(obj instanceof CartItem))
            return false;
        CartItem cartItem = (CartItem)obj;
        return cartItem.getFlowerId().equals(this.flowerId)&&
                cartItem.getFlowerAddon().equals(this.flowerAddon);
    }
}

