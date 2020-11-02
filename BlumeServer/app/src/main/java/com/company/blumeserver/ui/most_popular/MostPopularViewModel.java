package com.company.blumeserver.ui.most_popular;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.company.blumeserver.Callback.IBestDealCallbackListener;
import com.company.blumeserver.Callback.IMostPopularCallbackListener;
import com.company.blumeserver.Common.Common;
import com.company.blumeserver.Model.BestDealModel;
import com.company.blumeserver.Model.MostPopularModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MostPopularViewModel extends ViewModel implements IMostPopularCallbackListener {
    private MutableLiveData<List<MostPopularModel>> mostPopularMutableData;
    private MutableLiveData<String> messageError = new MutableLiveData<>();
    private IMostPopularCallbackListener mostPopularCallbackListener;

    public MostPopularViewModel() {
        mostPopularCallbackListener = this;
    }

    public MutableLiveData<List<MostPopularModel>> getBestDealMutableData() {
        if(mostPopularMutableData ==null)
            mostPopularMutableData = new MutableLiveData<>();
        loadMostPopular();

        return mostPopularMutableData;
    }

    public MutableLiveData<String> getMessageError() {
        return messageError;
    }

    public void setMessageError(MutableLiveData<String> messageError) {
        this.messageError = messageError;
    }

    public void loadMostPopular() {
        List<MostPopularModel> tempList= new ArrayList<>();
        DatabaseReference bestRef = FirebaseDatabase.getInstance().getReference(Common.MOST_POPULAR);
        bestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot itemSnapShot:dataSnapshot.getChildren()){
                    MostPopularModel mostPopularModel = itemSnapShot.getValue(MostPopularModel.class);
                    mostPopularModel.setKey(itemSnapShot.getKey());
                    tempList.add(mostPopularModel);
                }
                mostPopularCallbackListener.onMostPopularLoadSuccess(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mostPopularCallbackListener.onMostPopularFailed(databaseError.getMessage());
            }
        });
    }


    @Override
    public void onMostPopularLoadSuccess(List<MostPopularModel> mostPopularModels) {
        mostPopularMutableData.setValue(mostPopularModels);
    }

    @Override
    public void onMostPopularFailed(String message) {
        messageError.setValue(message);
    }
}