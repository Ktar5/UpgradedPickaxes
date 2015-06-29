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
import com.minecave.pickaxes.enchant.enchants.LuckEnchant;
import com.minecave.pickaxes.enchant.enchants.NormalEnchant;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.config.CustomConfig;
import com.minecave.pickaxes.util.message.Strings;
import lombok.Data;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Data
public class PItem<E extends Event> {

    private final EnhancedPicks plugin;

    private final String       name;
    private final PItemType    type;
    private       ItemStack    item;
    private final UUID         uuid;
    private       String       pItemSettings;
    private       List<String> nukerBlocks;

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
        this.nukerBlocks = new ArrayList<>();
        this.level = EnhancedPicks.getInstance().getLevelManager().getLevel(1);
        this.maxLevel = EnhancedPicks.getInstance().getLevelManager().getMaxLevel();
        UUID temp = UUID.randomUUID();
        while (plugin.getPItemManager().getPItemMap().containsKey(temp.toString())) {
            temp = UUID.randomUUID();
        }
        uuid = temp;
    }

    public void updateManually(Player player, ItemStack stack) {
        int slot = -1;
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            if (itemStack.equals(stack)) {
                slot = i;
                break;
            }
        }
        if (slot == -1) {
            return;
        }
        this.item = stack;
        updateMeta();
        for (PEnchant pEnchant : enchants) {
            pEnchant.apply(this);
        }
        player.getInventory().setItem(slot, this.getItem());
        player.updateInventory();
    }

    public void update(Player player) {
        ItemStack item = null;
        int slot = -1;
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR || !itemStack.hasItemMeta()) {
                continue;
            }
            ItemMeta stackMeta = itemStack.getItemMeta();
            List<String> lore = stackMeta.getLore();
            if (lore == null || lore.isEmpty()) {
                continue;
            }
            if (lore.contains(this.uuid.toString())) {
                item = itemStack;
                slot = i;
                break;
            }
        }
        if (item == null || slot == -1) { //item == null
            return;
        }
        this.item = item;
        updateMeta();
        for (PEnchant pEnchant : enchants) {
            pEnchant.apply(this);
        }
        player.getInventory().setItem(slot, this.getItem());
        player.updateInventory();
    }

    public String buildName() {
        CustomConfig config = plugin.getConfig("config");
        String displayName = "";
        if (type == PItemType.PICK) {
            displayName = config.get("pick-display-name", String.class);
        } else if (type == PItemType.SWORD) {
            displayName = config.get("sword-display-name", String.class);
        }
        int totalXp = getTotalXp();
        if (totalXp < xp) {
            totalXp = xp;
        }
        displayName = displayName.replace("{name}", Strings.color(name) + ChatColor.RESET)
                .replace("{level}", String.valueOf(level.getId()))
                .replace("{xp}", String.valueOf(xp))
                .replace("{nextLevelXpTotal}", String.valueOf(totalXp))
                .replace("{xpDiff}", String.valueOf(getXpToNextLevel()))
                .replace("{blocks}", String.valueOf(blocksBroken));
        return Strings.color(displayName);
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
                        if(enchant instanceof LuckEnchant) {
                            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
                            if(e.getEntity() instanceof LivingEntity) {
                                if(((LivingEntity) e.getEntity()).getHealth() <= e.getFinalDamage()) {
                                    enchant.activate((EntityDamageByEntityEvent) event);
                                }
                            }
                        } else {
                            enchant.activate((EntityDamageByEntityEvent) event);
                        }
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
            levelUp(player);
        }
        update(player);
    }

    public void levelUp(Player player) {
        this.level = level.getNext();
        this.points += pointsPerLevel;
        level.levelUp(player, this);
    }

    private int getXpToNextLevel() {
        return getTotalXp() - this.xp;
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
        this.item = item;
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

        lore.add(ChatColor.DARK_AQUA + "Level: " + ChatColor.YELLOW + level.getId());
        int totalXp = getTotalXp();
        if (totalXp < xp) {
            totalXp = xp;
        }
        lore.add(ChatColor.DARK_AQUA + "Xp: " + ChatColor.YELLOW + xp + "/" + totalXp);
        if(type == PItemType.PICK) {
            lore.add(ChatColor.DARK_AQUA + "Blocks Broken: " + ChatColor.YELLOW + blocksBroken);
        }
        if (currentSkill != null) {
            lore.add(ChatColor.DARK_AQUA + "Skill: " + ChatColor.YELLOW + currentSkill.getName());
        }
        lore.add("Custom Enchants: ");
        List<String> list = this.enchants.stream()
                .filter(enchant -> !(enchant instanceof NormalEnchant) && enchant.getLevel() > 0)
                .map(enchant -> ChatColor.AQUA + enchant.toString())
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            lore.add("None");
        } else {
            lore.addAll(list);
        }
        lore.add("UUID:" + this.uuid.toString());
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.GOLD + Strings.color(name));
        item.setItemMeta(meta);
    }

    public void addBlockBroken() {
        this.blocksBroken++;
    }

    public void addNukerBlocks(String s) {
        this.nukerBlocks.add(s);
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void subtractPoints(int cost) {
        this.points -= points;
    }
}
