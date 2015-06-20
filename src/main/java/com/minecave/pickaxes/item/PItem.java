/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.item;

import com.minecave.pickaxes.util.metadata.Metadata;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class PItem {

    private final String name;
    private final Metadata<PItem> metadata;
    private ItemStack item;

    public PItem(String name, ItemStack item) {
        this.name = name;
        this.item = item;
        this.metadata = new Metadata<>(this);
    }
}
