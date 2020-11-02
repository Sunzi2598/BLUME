package com.company.blumeserver.EventBus;

import com.company.blumeserver.Common.Common;

public class ToastEvent {
    private Common.ACTION action;
    private boolean isFromFlowerList;

    public ToastEvent(Common.ACTION action, boolean isFromFlowerList) {
        this.action = action;
        this.isFromFlowerList = isFromFlowerList;
    }

    public Common.ACTION getAction() {
        return action;
    }

    public void setAction(Common.ACTION action) {
        this.action = action;
    }

    public boolean isFromFlowerList() {
        return isFromFlowerList;
    }

    public void setFromFlowerList(boolean fromFlowerList) {
        isFromFlowerList = fromFlowerList;
    }
}

