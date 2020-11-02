package com.company.blumeserver.ui.best_deals;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.company.blumeserver.Callback.IBestDealCallbackListener;
import com.company.blumeserver.Callback.ICategoryCallbackListener;
import com.company.blumeserver.Common.Common;
import com.company.blumeserver.Model.BestDealModel;
import com.company.blumeserver.Model.CategoryModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

public class BestDealsViewModel extends ViewModel implements IBestDealCallbackListener {
    private MutableLiveData<List<BestDealModel>> bestDealMutableData;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private IBestDealCallbackListener bestDealCallbackListener;

    public BestDealsViewModel() {
        bestDealCallbackListener = this;
    }

    public MutableLiveData<List<BestDealModel>> getBestDealMutableData() {
        if(bestDealMutableData ==null)
            bestDealMutableData = new MutableLiveData<>();
        loadBestDeals();

        return bestDealMutableData;
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    public void setMessageError(MutableLiveData<String> messageError) {
        this.messageError = messageError;
    }

    public void loadBestDeals() {
        List<BestDealModel> tempList= new ArrayList<>();
        DatabaseReference bestRef = FirebaseDatabase.getInstance().getReference(Common.BEST_DEALS);
        bestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnapShot:dataSnapshot.getChildren()){
                    BestDealModel bestDealModel = itemSnapShot.getValue(BestDealModel.class);
                    bestDealModel.setKey(itemSnapShot.getKey());
                    tempList.add(bestDealModel);
                }
                bestDealCallbackListener.onBestDealLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                bestDealCallbackListener.onBestDealLoadFailed(databaseError.getMessage());
            }
        });
    }

    @Override
    public void onBestDealLoadSuccess(List<BestDealModel> bestDealModelList) {
        bestDealMutableData.setValue(bestDealModelList);
    }

    @Override
    public void onBestDealLoadFailed(String message) {
        messageError.setValue(message);
    }
}