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
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Data
public class PItem {

    //region CONSTANTS

    public static final String BLOCKS_BROKEN = "blocks_broken";
    //endregion

    private final EnhancedPicks plugin;

    private final String name;
    private final PItemType type;
    private ItemStack item;
    private String pItemSettings;

    private int xp;
    private int points;
    private Level level;
    private Level maxLevel;

    private List<PEnchant> enchants;
    private List<PSkill> purchasedSkills;
    private List<PSkill> availableSkills;
    private PSkill currentSkill;

    private Metadata<PItem> metadata;

    public PItem(String name, PItemType type, ItemStack item) {
        this.plugin = EnhancedPicks.getInstance();
        this.name = name;
        this.type = type;
        this.item = item;
        this.enchants = new ArrayList<>();
        this.purchasedSkills = new ArrayList<>();
        this.availableSkills = new ArrayList<>();
        this.metadata = new Metadata<>();
    }

    public String buildName() {
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.AQUA).append(name)
                .append(ChatColor.GOLD).append(" | ").append(ChatColor.AQUA)
                .append("Level: ").append(level)
                .append(ChatColor.GOLD).append(" | ").append(ChatColor.AQUA)
                .append("Xp: ").append(xp);
        if (type == PItemType.PICK) {
            builder.append(ChatColor.GOLD).append(" | ").append(ChatColor.AQUA)
                    .append("BlocksBroken: ")
                    .append(this.metadata.getIfNotSet(BLOCKS_BROKEN, Integer.class, 0));
        }
        return builder.toString();
    }

    public void addEnchant(PEnchant pEnchant) {
        enchants.add(pEnchant);
    }

    public void addAvailableSkill(PSkill pSkill) {
        availableSkills.add(pSkill);
    }

    public void addPurchasedSkill(PSkill pSkill) {
        purchasedSkills.add(pSkill);
    }
}
