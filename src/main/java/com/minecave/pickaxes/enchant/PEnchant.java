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

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.util.config.CustomConfig;
import lombok.Data;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public abstract class PEnchant {

    private int    level;
    private int    maxLevel;
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
        pItem.updateMeta();
    }

    public int getLevelCost(int level) {
        if (costMap.containsKey(level)) {
            return costMap.get(level);
        }
        return 1;
    }

    public int getCost() {
        return getLevelCost(this.level);
    }

    public abstract void activate(BlockBreakEvent event);

    public abstract void activate(EntityDamageByEntityEvent event);

    public void increaseLevel(Player player, PItem pItem) {
        if (level == maxLevel) {
            player.sendMessage(ChatColor.RED + "That enchantment is already at maxLevel.");
            return;
        }
        pItem.setPoints(pItem.getPoints() - getLevelCost(level + 1));
        this.setLevel(getLevel() + 1);
        pItem.update(player);
        player.sendMessage(ChatColor.RED + "You spent " + getLevelCost(level) + " points.");
        player.sendMessage(ChatColor.GOLD + "Current Item Points: " + pItem.getPoints());
        this.apply(pItem);
    }

    public void decreaseLevel(Player player, PItem pItem) {
        if (level == 0) {
            player.sendMessage(ChatColor.RED + "That enchantment is already at level 0.");
            return;
        }
        pItem.setPoints(pItem.getPoints() + getLevelCost(level));
        this.setLevel(getLevel() - 1);
        pItem.update(player);
        player.sendMessage(ChatColor.GREEN + "You received " + getLevelCost(level) + " points back.");
        player.sendMessage(ChatColor.GOLD + "Current Item Points: " + pItem.getPoints());
        this.apply(pItem);
    }

    @Override
    public String toString() {
        return ChatColor.YELLOW + displayName + " " + level;
    }

    public void loadConfig(String key) {
        CustomConfig config = EnhancedPicks.getInstance().getConfig("enchants");
        if (config.getConfig().contains(key + ".maxLevel")) {
            maxLevel = config.getConfig().getInt("tnt.maxLevel");
        }
        if (config.getConfig().contains(key + ".levelCosts")) {
            List<Integer> list = config.getConfig().getIntegerList(key + ".levelCosts");
            int index = 1;
            for (int i : list) {
                this.getCostMap().put(index++, i);
            }
        }
    }

    public abstract PEnchant cloneEnchant();
}
