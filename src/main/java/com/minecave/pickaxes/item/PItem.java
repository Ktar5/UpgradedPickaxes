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
import com.minecave.pickaxes.drops.DropManager;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.PSkill;
import lombok.Data;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
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

    private final String    name;
    private final PItemType type;
    private       ItemStack item;
    private       String    pItemSettings;

    private int xp;
    private int points         = 1;
    private int pointsPerLevel = 1;
    private Level level;
    private Level maxLevel;

    private List<PEnchant> enchants;
    private List<PSkill>   purchasedSkills;
    private List<PSkill>   availableSkills;
    private PSkill         currentSkill;

    private int blocksBroken;

    private BiConsumer<PItem, E> action;
    private Class<E>             eClass;

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
        this.maxLevel = EnhancedPicks.getInstance().getLevelManager().getMaxLevel();
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
                item = clone;
                slot = i;
                break;
            }
        }
        if (item == null || slot == -1) { //item == null
//          player.sendMessage(ChatColor.RED + "Could not find that item in your inventory.");
            return;
        }
        if (plugin.getPItemManager().getPItemMap().containsKey(item)) {
            plugin.getPItemManager().getPItemMap().remove(item);
        } else {
            for (Map.Entry<ItemStack, PItem<?>> entry : plugin.getPItemManager().getPItemMap().entrySet()) {
                if (entry.getValue().equals(this)) {
                    plugin.getPItemManager().getPItemMap().remove(entry.getKey());
                    break;
                }
            }
        }
        updateMeta();
        player.updateInventory();
        player.getInventory().setItem(slot, this.getItem());
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

    public boolean hasEnchant(PEnchant pEnchant) {
        if (enchants.isEmpty()) {
            return false;
        }
        for (PEnchant enchant : enchants) {
            if (enchant.getName().equalsIgnoreCase(pEnchant.getName())) {
                return true;
            }
        }
        return false;
    }

    public void addEnchant(PEnchant pEnchant) {
        if (!enchants.contains(pEnchant) && !hasEnchant(pEnchant)) {
            pEnchant.apply(this);
            enchants.add(pEnchant);
        }
    }

    public void addAvailableSkill(PSkill pSkill) {
        if (!availableSkills.contains(pSkill)) {
            availableSkills.add(pSkill);
        }
    }

    public void addPurchasedSkill(PSkill pSkill) {
        if (!purchasedSkills.contains(pSkill)) {
            purchasedSkills.add(pSkill);
        }
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
                    if (type == PItemType.PICK) {
                        enchant.activate((BlockBreakEvent) event);
                    } else if (type == PItemType.SWORD) {
                        enchant.activate((EntityDamageByEntityEvent) event);
                    }
                });
    }

    public void incrementXp(int xp, Player player) {
        DropManager.Multiplier multiplier = plugin.getDropManager().getMultiplier();
        if (multiplier.isActive()) {
            if (multiplier.isRequirePermission()) {
                if (player.hasPermission(multiplier.getPermission())) {
                    xp *= multiplier.getValue();
                }
            } else {
                xp *= multiplier.getValue();
            }
        }
        this.xp += xp;
        while (getTotalXp() <= this.xp && level.getId() != maxLevel.getId()) {
            this.level = level.getNext();
            level.levelUp(player, this);
            this.points += pointsPerLevel;
        }
        update(player);
    }

    private int getTotalXp() {
        Level lvl = level;
        int total = 0;
        for (int i = level.getId(); i > 0 && lvl != null; i--) {
            total += lvl.getXp();
            lvl = lvl.getPrevious();
        }
        return total;
    }

    public void setItem(ItemStack item) {
        if (EnhancedPicks.getInstance().getPItemManager().getPItemMap().containsKey(this.item)) {
            EnhancedPicks.getInstance().getPItemManager().getPItemMap().remove(this.item);
        }
        this.item = item;
        EnhancedPicks.getInstance().getPItemManager().getPItemMap().put(item, this);
    }

    public boolean hasEnchant(String name) {
        for (PEnchant pEnchant : enchants) {
            if (pEnchant.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public PEnchant getEnchant(String name) {
        for (PEnchant pEnchant : enchants) {
            if (pEnchant.getName().equalsIgnoreCase(name)) {
                return pEnchant;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("");
        String type = this.getType().name();
        String pItemSettings = this.getPItemSettings();
        String xp = String.valueOf(this.getXp());
        String points = String.valueOf(this.getPoints());
        String curLevel = String.valueOf(this.getLevel().getId());
        String maxLevel = String.valueOf(this.getMaxLevel().getId());
        StringBuilder availSkills = new StringBuilder("");
        for (PSkill pSkill : this.getAvailableSkills()) {
            availSkills.append(plugin.getPSkillManager().getPSkillKey(pSkill)).append("-");
        }
        StringBuilder purSkills = new StringBuilder("");
        for (PSkill pSkill : this.getPurchasedSkills()) {
            purSkills.append(plugin.getPSkillManager().getPSkillKey(pSkill)).append("-");
        }
        String curSkill = this.getCurrentSkill() == null ? "null" :
                plugin.getPSkillManager().getPSkillKey(this.getCurrentSkill());
        String blocksBroken = String.valueOf(this.getBlocksBroken());
        StringBuilder enchants = new StringBuilder("");
        for (PEnchant pEnchant : this.getEnchants()) {
            enchants.append(pEnchant.getName().toLowerCase()).append(":")
                    .append(pEnchant.getLevel()).append(":")
                    .append(pEnchant.getMaxLevel()).append("-");
        }
        return builder.append(type).append(",")
                .append(pItemSettings).append(",")
                .append(item.getDurability()).append(",")
                .append(xp).append(",")
                .append(points).append(",")
                .append(curLevel).append(",")
                .append(maxLevel).append(",")
                .append(availSkills.toString()).append(",")
                .append(purSkills.toString()).append(",")
                .append(curSkill).append(",")
                .append(blocksBroken).append(",")
                .append(enchants.toString()).append(";")
                .toString();

    }

    public void updateMeta() {
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(item.getType());
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
    }
}
