/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.item;

import com.minecave.pickaxes.util.item.ItemSerialization;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;

public class PItemSerializer {

    public static Inventory deserializeInventory(byte[] data) {
        try {
            return ItemSerialization.fromBlob(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Bukkit.createInventory(null, 9);
    }

    public static byte[] serialPicks(List<PItem<BlockBreakEvent>> inventory) {
        if (inventory.isEmpty()) {
            return null;
        }
        ItemStack[] items = new ItemStack[inventory.size()];
        int i = 0;
        for (PItem<BlockBreakEvent> p : inventory) {
            items[i++] = PItemSerializer.serializePItem(p);
        }
        return ItemSerialization.toBlob(ItemSerialization.getInventoryFromArray(items));
    }

    public static byte[] serialSwords(List<PItem<EntityDamageByEntityEvent>> inventory) {
        if (inventory.isEmpty()) {
            return null;
        }
        ItemStack[] items = new ItemStack[inventory.size()];
        int i = 0;
        for (PItem<EntityDamageByEntityEvent> p : inventory) {
            items[i++] = PItemSerializer.serializePItem(p);
        }
        return ItemSerialization.toBlob(ItemSerialization.getInventoryFromArray(items));
    }

    public static <T extends Event> PItem<T> deserializePItem(ItemStack stack) {
        //TODO:
        return null;
    }

    public static <T extends Event> ItemStack serializePItem(PItem<T> pItem) {
        //TODO:
        return null;
    }
}
