/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.enchant.enchants;

import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.util.message.Strings;
import lombok.Getter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NormalEnchant extends PEnchant {

    @Getter
    private Enchantment enchantment;

    public NormalEnchant(Enchantment enchantment) {
        super(enchantment.getName(), Strings.fixEnchantment(enchantment));
        this.enchantment = enchantment;
        this.setMaxLevel(enchantment.getMaxLevel());
    }

    public NormalEnchant(NormalEnchant normalEnchant) {
        this(normalEnchant.getEnchantment());
    }

    @Override
    public void activate(BlockBreakEvent event) {
        //nothing this is just an object to manage the enchants more easily
    }

    @Override
    public void activate(EntityDamageByEntityEvent event) {
        //nothing this is just an object to manage the enchants more easily
    }

    @Override
    public void apply(PItem pItem) {
        if (pItem.getItem().containsEnchantment(this.enchantment) ||
                super.getLevel() >= 0) {
            pItem.getItem().removeEnchantment(enchantment);
            if (super.getLevel() > 0) {
                pItem.getItem().addUnsafeEnchantment(enchantment, this.getLevel());
            }
        }
//        super.apply(pItem);
    }

    @Override
    public NormalEnchant cloneEnchant() {
        return new NormalEnchant(this);
    }

    public enum VanillaSword {
        SHARPNESS(Enchantment.DAMAGE_ALL),
        FIRE_ASPECT(Enchantment.FIRE_ASPECT),
        KNOCKBACK(Enchantment.KNOCKBACK),
        BANE_OF_ARTHROPODS(Enchantment.DAMAGE_ARTHROPODS),
        SMITE(Enchantment.DAMAGE_UNDEAD),
        LOOTING(Enchantment.LOOT_BONUS_MOBS),
        UNBREAKING(Enchantment.DURABILITY);

        @Getter
        private final Enchantment enchantment;

        VanillaSword(Enchantment enchantment) {
            this.enchantment = enchantment;
        }

        public static boolean has(String s) {
            for (VanillaSword vanilla : values()) {
                if (vanilla.toString().equalsIgnoreCase(s) ||
                        vanilla.getEnchantment().getName().equalsIgnoreCase(s)) {
                    return true;
                }
            }
            return false;
        }

        public static VanillaSword get(String s) {
            for (VanillaSword vanilla : values()) {
                if (vanilla.toString().equalsIgnoreCase(s) ||
                        vanilla.getEnchantment().getName().equalsIgnoreCase(s)) {
                    return vanilla;
                }
            }
            return null;
        }
    }

    public enum VanillaPick {
        SILK_TOUCH(Enchantment.SILK_TOUCH),
        UNBREAKING(Enchantment.DURABILITY),
        EFFICIENCY(Enchantment.DIG_SPEED),
        FORTUNE(Enchantment.LOOT_BONUS_BLOCKS);

        @Getter
        private final Enchantment enchantment;

        VanillaPick(Enchantment enchantment) {
            this.enchantment = enchantment;
        }

        public static boolean has(String s) {
            for (VanillaPick vanilla : values()) {
                if (vanilla.toString().equalsIgnoreCase(s) ||
                        vanilla.getEnchantment().getName().equalsIgnoreCase(s)) {
                    return true;
                }
            }
            return false;
        }

        public static VanillaPick get(String s) {
            for (VanillaPick vanilla : values()) {
                if (vanilla.toString().equalsIgnoreCase(s) ||
                        vanilla.getEnchantment().getName().equalsIgnoreCase(s)) {
                    return vanilla;
                }
            }
            return null;
        }
    }
}
