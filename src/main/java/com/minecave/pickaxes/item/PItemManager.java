/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.item;

import com.minecave.pickaxes.EnhancedPicks;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PItemManager {

    private final EnhancedPicks plugin;
    @Getter
    private Map<ItemStack, PItem> pItemMap;

    public PItemManager() {
        plugin = EnhancedPicks.getInstance();
        pItemMap = new HashMap<>();
    }
}
