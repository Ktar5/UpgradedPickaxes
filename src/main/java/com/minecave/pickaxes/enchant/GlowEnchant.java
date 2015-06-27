/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.enchant;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.HashMap;

public class GlowEnchant extends Enchantment {
    public static final int         ID       = 112;
    private static      GlowEnchant instance = new GlowEnchant();

    /**
     * Apply the glowing enchantment to the item.
     *
     * @param itemStack The itemstack to enchant
     * @return If the enchantment was successful
     */
    public static boolean apply(final ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(instance, 1, true);
        itemStack.setItemMeta(meta);
        return true;
    }

    public static boolean remove(final ItemStack itemStack) {
        if(itemStack == null) {
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        meta.removeEnchant(instance);
        itemStack.setItemMeta(meta);
        return true;
    }

    public GlowEnchant() {
        super(ID);
    }

    @Override
    public boolean canEnchantItem(ItemStack arg0) {
        return true;
    }

    @Override
    public boolean conflictsWith(Enchantment arg0) {
        return false;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public String getName() {
        return "GLOW";
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    public static void register() {
        try {
            Field byIdField = Enchantment.class.getDeclaredField("byId");
            Field byNameField = Enchantment.class.getDeclaredField("byName");

            byIdField.setAccessible(true);
            byNameField.setAccessible(true);

            @SuppressWarnings("unchecked")
            HashMap<Integer, Enchantment> byId = (HashMap<Integer, Enchantment>) byIdField.get(null);
            @SuppressWarnings("unchecked")
            HashMap<String, Enchantment> byName = (HashMap<String, Enchantment>) byNameField.get(null);

            if (byId.containsKey(ID))
                byId.remove(ID);

            if (byName.containsKey(instance.getName()))
                byName.remove(instance.getName());

            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Enchantment.registerEnchantment(instance);
    }
}
