package com.company.blumeSunzi.ui.flower_detail;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.company.blumeSunzi.Common.Common;
import com.company.blumeSunzi.Model.CommentModel;
import com.company.blumeSunzi.Model.FlowerModel;

public class FlowerDetailViewModel extends ViewModel {

    private MutableLiveData<FlowerModel> mutableLiveDataFlower;
    private MutableLiveData<CommentModel> mutableLiveDataComment;

    public void setCommentModel(CommentModel commentModel){
        if(mutableLiveDataComment !=null)
            mutableLiveDataComment.setValue(commentModel);
    }

    public MutableLiveData<CommentModel> getMutableLiveDataComment() {
        return mutableLiveDataComment;
    }

    public FlowerDetailViewModel() {
        mutableLiveDataComment=new MutableLiveData<>();
    }

    public MutableLiveData<FlowerModel> getMutableLiveDataFlower() {
        if(mutableLiveDataFlower == null)
            mutableLiveDataFlower = new MutableLiveData<>();
        mutableLiveDataFlower.setValue(Common.selectedFlower);
        return mutableLiveDataFlower;
    }

    public void setFlowerModel(FlowerModel flowerModel) {
        if(mutableLiveDataFlower !=null)
            mutableLiveDataFlower.setValue(flowerModel);
    }
}