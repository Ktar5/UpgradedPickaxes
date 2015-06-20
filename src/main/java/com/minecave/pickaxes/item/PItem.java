/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.item;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.metadata.Metadata;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Data
public class PItem {

    //region CONSTANTS

    public static final String ENCHANTS = "enchants";
    public static final String CURRENT_SKILL = "current_skill";
    public static final String SKILLS = "skills";
    public static final String AVAILABLE_SKILLS = "available_skills";
    public static final String XP = "xp";
    public static final String LEVEL = "level";
    public static final String MAX_LEVEL = "max_level";

    //endregion

    private final EnhancedPicks plugin;
    private final String name;
    private final Metadata<PItem> metadata;
    private ItemStack item;

    public PItem(String name, ItemStack item) {
        this.plugin = EnhancedPicks.getInstance();
        this.name = name;
        this.item = item;
        this.metadata = new Metadata<>(this);
    }

    //region GETTERS && SETTERS

    public void setEnchants(List<PEnchant> enchants) {
        metadata.set(ENCHANTS, enchants);
    }

    public List<PEnchant> getEnchants() {
        return metadata.getIfNotSetList(ENCHANTS, PEnchant.class);
    }

    public void setCurrentSkill(PSkill skill) {
        metadata.set(CURRENT_SKILL, skill);
    }

    public PSkill getCurrentSkill() {
        return metadata.getIfNotSet(CURRENT_SKILL, PSkill.class, null);
    }

    public void setSkills(List<PSkill> skills) {
        metadata.set(SKILLS, skills);
    }

    public List<PSkill> getSkills() {
        return metadata.getIfNotSetList(SKILLS, PSkill.class);
    }

    public void setAvailableSkills(List<PSkill> skills) {
        metadata.set(AVAILABLE_SKILLS, skills);
    }

    public List<PSkill> getAvailableSkills() {
        return metadata.getIfNotSetList(AVAILABLE_SKILLS, PSkill.class);
    }

    public void setXp(int xp) {
        metadata.set(XP, xp);
    }

    public int getXp() {
        return metadata.getIfNotSet(XP, Integer.class, 0);
    }

    public void setLevel(int level) {
        metadata.set(LEVEL, plugin.getLevelManager().getLevel(level));
    }

    public void setLevel(Level level) {
        metadata.set(LEVEL, level);
    }

    public Level getLevel() {
        return metadata.getIfNotSet(LEVEL, Level.class, plugin.getLevelManager().getLevel(1));
    }

    public void setMaxLevel(int level) {
        metadata.set(MAX_LEVEL, plugin.getLevelManager().getLevel(level));
    }

    public void setMaxLevel(Level level) {
        metadata.set(MAX_LEVEL, level);
    }

    public Level getMaxLevel() {
        return metadata.getIfNotSet(MAX_LEVEL, Level.class, plugin.getLevelManager().getLevel(10));
    }

    //endregion
}
