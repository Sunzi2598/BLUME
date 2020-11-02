package com.company.blumeshipper.Model;

public class CartItem {

    private String flowerId;

    private String flowerName;

    private String flowerImage;

    private double flowerPrice;

    private int flowerQuantity;

    private String userPhone;

    private double flowerExtraPrice;

    private String flowerAddon;

    private String uid;

    public String getFlowerId() {
        return flowerId;
    }

    public void setFlowerId( String flowerId) {
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


}