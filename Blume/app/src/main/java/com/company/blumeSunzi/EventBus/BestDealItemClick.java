package com.company.blumeSunzi.EventBus;

import com.company.blumeSunzi.Model.BestDealModel;

public class BestDealItemClick {
    private BestDealModel bestDealModel;

    public BestDealModel getBestDealModel() {
        return bestDealModel;
    }

    public void setBestDealModel(BestDealModel bestDealModel) {
        this.bestDealModel = bestDealModel;
    }

    public BestDealItemClick(BestDealModel bestDealModel) {
        this.bestDealModel = bestDealModel;
    }
}