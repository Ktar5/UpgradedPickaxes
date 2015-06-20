/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of pickaxes.
 * 
 * pickaxes can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 *
 * Created: 6/19/15 @ 7:46 PM
 */
package com.minecave.pickaxes.enchant;

import com.minecave.pickaxes.item.PItem;
import lombok.Data;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@Data
public class PEnchant {

    private int level;
    private int maxLevel;
    private String name;
    private String displayName;
    private Map<Integer, Integer> costMap = new HashMap<>();

    public PEnchant(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
        this.level = 0;
        this.maxLevel = 3;
    }

    public PEnchant(PEnchant copy) {
        this(copy.getName(), copy.getDisplayName());
        this.level = copy.getLevel();
        this.maxLevel = copy.getMaxLevel();
    }

    public void apply(PItem pItem) {
        pItem.addEnchant(this);
    }

    public int getLevelCost(int level) {
        if(costMap.containsKey(level)) {
            return costMap.get(level);
        }
        return 1;
    }

    public void increaseLevel(Player player, PItem pItem) {
        if (level == maxLevel) {
            player.sendMessage(ChatColor.RED + "That enchantment is already at maxLevel.");
            return;
        }
        this.setLevel(getLevel() + 1);
        pItem.setPoints(pItem.getPoints() - getLevelCost(level - 1));
        pItem.update(player);
    }

    @Override
    public String toString() {
        return ChatColor.YELLOW + displayName + " " + level;
    }

    public PEnchant cloneEnchant() {
        return new PEnchant(this);
    }
}
