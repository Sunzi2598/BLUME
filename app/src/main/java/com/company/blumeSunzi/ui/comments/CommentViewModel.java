package com.company.blumeSunzi.ui.comments;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.company.blumeSunzi.Model.CommentModel;

import java.util.List;

public class CommentViewModel extends ViewModel {
    private MutableLiveData<List<CommentModel>> mutableLiveDataFlowerList;

    public CommentViewModel(){
        mutableLiveDataFlowerList= new MutableLiveData<>();
    }

    public MutableLiveData<List<CommentModel>> getMutableLiveDataFlowerList() {
        return mutableLiveDataFlowerList;
    }

    public void setCommentList(List<CommentModel> commentList) {
        mutableLiveDataFlowerList.setValue(commentList);

    }
}

