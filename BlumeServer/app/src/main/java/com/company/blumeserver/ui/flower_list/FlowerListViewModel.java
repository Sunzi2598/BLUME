package com.company.blumeserver.ui.flower_list;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.company.blumeserver.Common.Common;
import com.company.blumeserver.Model.FlowerModel;

import java.util.List;

public class FlowerListViewModel extends ViewModel {

    private MutableLiveData<List<FlowerModel>> mutableLiveDataFlowerList;

    public FlowerListViewModel() {

    }

    public MutableLiveData<List<FlowerModel>> getMutableLiveDataFlowerList() {
        if(mutableLiveDataFlowerList == null)
            mutableLiveDataFlowerList =  new MutableLiveData<>();
        mutableLiveDataFlowerList.setValue(Common.CategorySelected.getFlowers());
        return mutableLiveDataFlowerList;
    }
}