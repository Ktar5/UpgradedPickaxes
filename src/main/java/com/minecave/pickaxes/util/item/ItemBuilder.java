/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.item;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;

/**
 * @author Timothy Andis
 */
public class ItemBuilder {

    private ItemStack            item;
    private String               name;
    private String[]             lore;
    private Color                color;
    private WrappedEnchantment[] enchantments;

    /**
     * Wrap an itemstack for editing
     *
     * @param item The desired item
     * @return Wrapped item
     */
    public static ItemBuilder wrap(ItemStack item) {
        ItemBuilder instance = new ItemBuilder();
        instance.setItem(item);
        return instance;
    }

    private void setItem(ItemStack item) {
        this.item = item;
    }

    /**
     * Add a name to the itemstack
     *
     * @param name The desired name
     */
    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Add lore to the itemstack dynamically
     *
     * @param lore The desired lore
     */
    public ItemBuilder lore(String... lore) {
        this.lore = lore;
        return this;
    }

    /**
     * Apply enchantments to the item
     *
     * @param enchantments An array of WrappedEnchantment to be applied on building
     */
    public ItemBuilder enchant(WrappedEnchantment... enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    /**
     * Apply a color to the item.
     *
     * @param color The desired color
     */
    public ItemBuilder color(Color color) {
        if (!item.getType().name().toLowerCase().contains("leather_")) {
            System.out.println("ItemBuilder: Tried setting color to a non leather material. Ignoring");
            return this;
        }
        this.color = color;
        return this;
    }

    public ItemBuilder glow(boolean glow) {
        if (glow) {
            enchant(new WrappedEnchantment(GlowEnchant.getGlowEnchant()));
        }
        return this;
    }

    /**
     * Built the item
     *
     * @return the new item stack
     */
    public ItemStack build() {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) {
            meta.setLore(Arrays.asList(lore));
        }
        if (enchantments != null) {
            for (WrappedEnchantment enchantment : enchantments) {
                meta.addEnchant(enchantment.getEnchantment(), enchantment.getLevel(), enchantment.isOverride());
            }
        }
        item.setItemMeta(meta);
        if (item.getType().name().toLowerCase().contains("leather_")) {
            LeatherArmorMeta lMeta = (LeatherArmorMeta) item.getItemMeta();
            lMeta.setColor(color);
            item.setItemMeta(lMeta);
        }
        return item;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ItemBuilder: {");
        builder.append("\n");
        builder.append("name: ").append(name);
        builder.append("\n");
        builder.append("lore: ");
        if (lore == null) {
            builder.append("null");
        } else {
            builder.append("[");
            for (int i = 0; i < lore.length; i++) {
                String s = lore[i];
                builder.append(s);
                if ((i + 1) != lore.length) {
                    builder.append(", ");
                }
            }
            builder.append("]");
        }
        builder.append("\n");
        builder.append("color: ").append(color == null ? "null" : "Red: " + color.getRed() + " Green: " + color.getGreen() + " Blue: " + color.getBlue());
        builder.append("\n");
        builder.append("enchantments: ");
        if (enchantments == null) {
            builder.append("null");
        } else {
            builder.append("\n");
            for (int i = 0; i < enchantments.length; i++) {
                WrappedEnchantment enchantment = enchantments[i];
                builder.append("  name: ").append(enchantment.getEnchantment().getName())
                        .append("\n")
                        .append("  level: ").append(enchantment.getLevel())
                        .append("\n")
                        .append("  override: ").append(enchantment.isOverride());
                if ((i + 1) != enchantments.length) {
                    builder.append(", ");
                }
            }
        }
        builder.append("\n");
        builder.append("}");
        return builder.toString();
    }
}
