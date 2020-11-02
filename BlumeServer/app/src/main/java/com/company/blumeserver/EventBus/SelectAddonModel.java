package com.company.blumeserver.EventBus;

import com.company.blumeserver.Model.AddonModel;

public class SelectAddonModel {
    private AddonModel addonModel;

    public SelectAddonModel(AddonModel addonModel) {
        this.addonModel = addonModel;
    }

    public AddonModel getAddonModel() {
        return addonModel;
    }

    public void setAddonModel(AddonModel addOnModel) {
        this.addonModel = addOnModel;
    }
}