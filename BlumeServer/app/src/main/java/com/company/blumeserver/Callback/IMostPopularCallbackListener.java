package com.company.blumeserver.Callback;


import com.company.blumeserver.Model.MostPopularModel;

import java.util.List;

public interface IMostPopularCallbackListener {
    void onMostPopularLoadSuccess(List<MostPopularModel> mostPopularModels);
    void onMostPopularFailed(String message);
}
