/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.item;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;

/**
 * @author Timothy Andis
 */
public class GlowEnchant extends Enchantment {

    public static final int         ID         = 112;
    public static final Enchantment instance   = new GlowEnchant();
    static              boolean     registered = false;

    public GlowEnchant() {
        super(ID);
    }

    public static boolean isRegistered() {
        return registered;
    }

    public static GlowEnchant getGlowEnchant() {
        return (GlowEnchant) instance;
    }

    /**
     * Apply the glowing enchantment to the item.
     *
     * @param itemStack The itemstack to enchant
     * @return If the enchantment was successful
     */
    public static boolean apply(final ItemStack itemStack) {
        if (itemStack == null || !registered) {
            System.out.println("Cannot enchant because: " + (itemStack == null ? " NULL ITEM" : itemStack.getType().name() + " enchantment not registered"));
            return false;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta.hasEnchant(instance)) {
            return true;
        }
        meta.addEnchant(instance, 1, true);
        itemStack.setItemMeta(meta);
        return true;
    }

    /**
     * Called onEnable()
     * Don't touch this plz
     */
    public static void register() {
        try {
            final Enchantment e = Enchantment.getByName("GLOW");
            if (e != null && e.getClass() == GlowEnchant.class) {
                GlowEnchant.registered = true;
                return;
            }
            final Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
            Enchantment.registerEnchantment(GlowEnchant.instance);
            GlowEnchant.registered = true;
            f.set(null, false);
        } catch (final Exception ex) {
            GlowEnchant.registered = true;
        }
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
}
