package com.company.blumeserver.EventBus;

import com.company.blumeserver.Model.AddonModel;

import java.util.List;

public class UpdateAddonModel {
    protected List<AddonModel> addonModels;


    public List<AddonModel> getAddonModels() {
        return addonModels;
    }

    public void setAddonModels(List<AddonModel> addonModels) {
        this.addonModels = addonModels;
    }

    public UpdateAddonModel() {

    }
}