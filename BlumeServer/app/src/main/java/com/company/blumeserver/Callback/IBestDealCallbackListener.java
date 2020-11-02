package com.company.blumeserver.Callback;



import com.company.blumeserver.Model.BestDealModel;

import java.util.List;

public interface IBestDealCallbackListener {
        void onBestDealLoadSuccess(List<BestDealModel> bestDealModelList);
        void onBestDealLoadFailed(String message);
}
