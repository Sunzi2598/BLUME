package com.company.blumeserver.EventBus;

public class ChangeMenuClick {
    private boolean isFromFlowerList;

    public ChangeMenuClick(boolean isFromFlowerList) {
        this.isFromFlowerList = isFromFlowerList;
    }

    public boolean isFromFlowerList() {
        return isFromFlowerList;
    }

    public void setFromFlowerList(boolean fromFlowerList) {
        isFromFlowerList = fromFlowerList;
    }
}
