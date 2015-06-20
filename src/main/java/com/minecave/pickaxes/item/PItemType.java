/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.item;

import lombok.Getter;
import org.bukkit.Material;

public enum PItemType {
    PICK(Material.DIAMOND_PICKAXE),
    SWORD(Material.DIAMOND_SWORD);

    @Getter
    private Material type;

    PItemType(Material type) {
        this.type = type;
    }
}
