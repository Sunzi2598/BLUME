package com.company.blumeSunzi.EventBus;

import com.company.blumeSunzi.Model.FlowerModel;

public class FlowerItemClick {
    private boolean success;
    private FlowerModel flowerModel;

    public FlowerItemClick(boolean success, FlowerModel flowerModel) {
        this.success = success;
        this.flowerModel = flowerModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public FlowerModel getFlowerModel() {
        return flowerModel;
    }

    public void setFlowerModel(FlowerModel flowerModel) {
        this.flowerModel = flowerModel;
    }
}