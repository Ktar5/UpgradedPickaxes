/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.item;


import com.minecave.pickaxes.EnhancedPicks;
import org.bukkit.Material;
import org.bukkit.material.Dye;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Material.*;

public class OreConversion {

    private static Map<Material, Material> oreToItem = new HashMap<>();
    private static Map<Material, Material> itemToOre = new HashMap<>();

    static {
        oreToItem.clear();
        itemToOre.clear();
        Map<Material, Material> temp = new HashMap<>();
        temp.put(COAL_ORE, COAL);
        temp.put(IRON_ORE, IRON_INGOT);
        temp.put(GOLD_ORE, GOLD_INGOT);
        temp.put(DIAMOND_ORE, DIAMOND);
        temp.put(EMERALD_ORE, EMERALD);
        temp.put(REDSTONE_ORE, REDSTONE);
        temp.put(GLOWING_REDSTONE_ORE, REDSTONE);
        temp.put(QUARTZ_ORE, QUARTZ);
        temp.put(LAPIS_ORE, new Dye(4).getItemType());
        temp.put(GLOWSTONE, GLOWSTONE_DUST);
        temp.put(SEA_LANTERN, SEA_LANTERN);
        temp.forEach((m1, m2) -> {
            oreToItem.put(m1, m2);
            itemToOre.put(m2, m1);
        });
    }

    public static Material convertToItem(Material material) {
        if (oreToItem.containsKey(material)) {
            return oreToItem.get(material);
        }
        return material;
    }

    public static boolean canConvert(Material material) {
        return oreToItem.containsKey(material);
    }

    public static boolean isItem(Material material) {
        return itemToOre.containsKey(material) || EnhancedPicks.getInstance().getScaleFactors().containsKey(material);
    }
}
