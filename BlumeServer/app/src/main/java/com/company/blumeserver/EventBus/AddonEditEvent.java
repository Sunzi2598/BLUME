package com.company.blumeserver.EventBus;

public class AddonEditEvent {
    private boolean addon;
    private int pos;

    public boolean isAddon() {
        return addon;
    }

    public void setAddon(boolean addon) {
        this.addon = addon;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public AddonEditEvent(boolean addon, int pos) {
        this.addon = addon;
        this.pos = pos;
    }
}