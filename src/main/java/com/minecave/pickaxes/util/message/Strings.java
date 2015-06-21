/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.message;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.enchantments.Enchantment;

public class Strings {

    public static String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String fixEnchantment(Enchantment enchantment) {
        String name = enchantment.getName();
        String[] split = name.toLowerCase().split("_");
        String retVal = "";
        for (String s : split) {
            s = s.substring(0, 1).toUpperCase() + s.substring(1) + " ";
            retVal += s;
        }
        return retVal.trim();
    }
}
