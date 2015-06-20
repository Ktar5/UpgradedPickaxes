/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.item;

import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.metadata.Metadata;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
public class PItem {

    //region CONSTANTS
    public static final String ENCHANTS = "enchants";
    public static final String SKILLS = "skills";
    //endregion

    private final String name;
    private final Metadata<PItem> metadata;
    private ItemStack item;

    public PItem(String name, ItemStack item) {
        this.name = name;
        this.item = item;
        this.metadata = new Metadata<>(this);
    }

    public List<PEnchant> getEnchants() {
        return metadata.getIfNotSetList(ENCHANTS, PEnchant.class);
    }

    public List<PSkill> getSkills() {
        return metadata.getIfNotSetList(SKILLS, PSkill.class);
    }
}
