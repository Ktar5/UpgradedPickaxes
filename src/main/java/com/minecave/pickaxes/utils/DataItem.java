package com.minecave.pickaxes.utils;

import com.tadahtech.pub.pitem.PItem;

/**
 * @author Timothy Andis
 */
public class DataItem {

    private int slot;
    private PItem itemStack;

    public DataItem(int slot, PItem itemStack) {
        this.slot = slot;
        this.itemStack = itemStack;
    }

    public int getSlot() {
        return slot;
    }

    public PItem getItemStack() {
        return itemStack;
    }
}
