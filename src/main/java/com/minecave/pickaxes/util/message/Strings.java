/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.message;

import com.minecave.pickaxes.enchant.enchants.NormalEnchant;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Strings {

    public static String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    @SuppressWarnings("ConstantConditions")
    public static String fixEnchantment(Enchantment enchantment) {
        String name = enchantment.getName();
        if(NormalEnchant.VanillaPick.has(name)) {
            name = NormalEnchant.VanillaPick.get(name).name();
        } else if(NormalEnchant.VanillaSword.has(name)) {
            name = NormalEnchant.VanillaSword.get(name).name();
        }
        String[] split = name.toLowerCase().split("_");
        String retVal = "";
        for (String s : split) {
            s = s.substring(0, 1).toUpperCase() + s.substring(1) + " ";
            retVal += s;
        }
        return retVal.trim();
    }

    public static void debug(Player player, ItemStack stack) {
        StringBuilder builder = new StringBuilder("");
        builder.append("\n").append("=== Item Debug ===").append("\n")
                .append(System.identityHashCode(stack)).append("\n")
                .append("===============================").append("\n")
                .append("ItemStack: ").append(stack.hashCode()).append("\n")
                .append("=== ItemDebug ===").append("\n");
        player.sendMessage(builder.toString());
    }
}
