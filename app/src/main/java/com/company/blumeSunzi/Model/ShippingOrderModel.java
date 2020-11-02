package com.company.blumeSunzi.Model;

public class ShippingOrderModel {
    private String key,shipperPhone, shipperName;
    private double currentLat, currentLng;
    private OrderModel orderModel;
    private boolean isStartTrip;
    private String estimateTime;

    public ShippingOrderModel() {
    }

    public String getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(String estimateTime) {
        this.estimateTime = estimateTime;
    }

    public String getShipperPhone() {
        return shipperPhone;
    }

    public boolean isStartTrip() {
        return isStartTrip;
    }

    public void setStartTrip(boolean startTrip) {
        isStartTrip = startTrip;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setShipperPhone(String shipperPhone) {
        this.shipperPhone = shipperPhone;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLong) {
        this.currentLng = currentLong;
    }

    public OrderModel getOrderModel() {
        return orderModel;
    }

    public void setOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel;
    }


}

