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
import lombok.Data;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Data
public class PItem<E extends Event> {

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

    private int blocksBroken;

    private BiConsumer<PItem, E> action;
    private Class<E> eClass;

    public PItem(Class<E> eClass, String name, PItemType type, ItemStack item) {
        this.plugin = EnhancedPicks.getInstance();
        this.eClass = eClass;
        this.name = name;
        this.type = type;
        this.item = item;
        this.enchants = new ArrayList<>();
        this.purchasedSkills = new ArrayList<>();
        this.availableSkills = new ArrayList<>();
        this.level = EnhancedPicks.getInstance().getLevelManager().getLevel(1);
    }

    public void update(Player player) {
       ItemStack clone = this.item;
        ItemStack item = null;
        int slot = -1;
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            if (clone.isSimilar(itemStack)) {
                item = itemStack;
                slot = i;
                break;
            }
        }
        if (item == null || slot == -1) {
            player.sendMessage(ChatColor.RED + "Could not find that item in your inventory.");
            return;
        }
        if(plugin.getPItemManager().getPItemMap().containsKey(this.item)) {
            plugin.getPItemManager().getPItemMap().remove(this.item);
        } else {
            for (Map.Entry<ItemStack, PItem<?>> entry : plugin.getPItemManager().getPItemMap().entrySet()) {
                if(entry.getValue().equals(this)) {
                    plugin.getPItemManager().getPItemMap().remove(entry.getKey());
                    break;
                }
            }
        }
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("Custom Enchants: ");
        List<String> list = this.enchants.stream()
                .filter(enchant -> enchant.getLevel() > 0)
                .map(enchant -> ChatColor.AQUA + enchant.toString())
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            lore.add("None");
        } else {
            lore.addAll(list);
        }
        meta.setLore(lore);
        meta.setDisplayName(buildName());
        item.setItemMeta(meta);
        this.setItem(item);
        plugin.getPItemManager().getPItemMap().put(item, this);
        player.updateInventory();
    }

    public String buildName() {
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.AQUA).append(name)
                .append(ChatColor.GOLD).append(" | ").append(ChatColor.AQUA)
                .append("Level: ").append(level.getId())
                .append(ChatColor.GOLD).append(" | ").append(ChatColor.AQUA)
                .append("Xp: ").append(xp);
        if (type == PItemType.PICK) {
            builder.append(ChatColor.GOLD).append(" | ").append(ChatColor.AQUA)
                    .append("BlocksBroken: ")
                    .append(blocksBroken);
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

    public void onAction(E event) {
        if (action != null) {
            action.accept(this, event);
        }
    }

    public <T extends Event> void activateEnchants(T event) {
        enchants.stream()
                .filter(enchant -> enchant != null && enchant.getLevel() > 0)
                .forEach(enchant -> {
                    if(type == PItemType.PICK) {
                        enchant.activate((BlockBreakEvent) event);
                    } else if(type == PItemType.SWORD) {
                        enchant.activate((EntityDamageByEntityEvent) event);
                    }
                });
    }

    public void incrementXp(int xp, Player player) {
        this.xp += xp;
        Level next = level.getNext();
        Level lvl = next.getPrevious();
        int total = 0;
        while (lvl != null && lvl.getId() >= 1) {
            total += lvl.getXp();
            lvl = lvl.getPrevious();
        }
        if (total <= this.xp && level.getId() != maxLevel.getId()) {
            this.level = next;
            level.levelUp(player, this);
            this.points++;
        }
        update(player);
    }
}
