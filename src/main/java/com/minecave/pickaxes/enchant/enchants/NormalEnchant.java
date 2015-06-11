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
import lombok.Getter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NormalEnchant extends PEnchant {

    private Enchantment enchantment;

    public NormalEnchant(Enchantment enchantment) {
        super(enchantment.getName());
        this.enchantment = enchantment;
    }

    @Override
    public void activate(BlockBreakEvent event) {

    }

    @Override
    public void activate(EntityDamageByEntityEvent event) {

    }

    @Override
    public int getMaxLevel() {
        return enchantment.getMaxLevel();
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
    }
}
