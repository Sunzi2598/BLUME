package com.company.blumeserver.Callback;

import com.company.blumeserver.Model.ShippingOrderModel;

public interface ISingleShippingOrderCallbackListener {
    void onSingleShippingOrderLoadSuccess(ShippingOrderModel shippingOrderModel);
}
