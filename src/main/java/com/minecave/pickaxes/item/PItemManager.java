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
import com.minecave.pickaxes.drops.BlockValue;
import com.minecave.pickaxes.drops.MobValue;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.config.CustomConfig;
import com.minecave.pickaxes.util.item.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PItemManager {

    private final EnhancedPicks              plugin;
    @Getter
    private       Map<String, PItem<?>>      pItemMap;
    @Getter
    private       Map<String, PItemSettings> settingsMap;

    public PItemManager() {
        plugin = EnhancedPicks.getInstance();
        pItemMap = new HashMap<>();
        settingsMap = new HashMap<>();

        CustomConfig pickConfig = plugin.getConfig("picks");
        for (String n : pickConfig.getConfig().getKeys(false)) {
            PItemSettings settings = new PItemSettings(n, PItemType.PICK);
            String name = pickConfig.get(concat(n, "name"), String.class, n);
            int xp = pickConfig.get(concat(n, "startXp"), Integer.class, 0);
            int level = pickConfig.get(concat(n, "startLevel"), Integer.class, 1);
            int points = pickConfig.get(concat(n, "startPoints"), Integer.class, 1);
            int ppl = pickConfig.get(concat(n, "pointsPerLevel"), Integer.class, 1);
            List<String> nukerBlocks = pickConfig.getConfig().getStringList(concat(n, "blocks"));
            settings.setName(name);
            settings.setStartXp(xp);
            settings.setStartLevel(level);
            settings.setStartPoints(points);
            settings.setPointsPerLevel(ppl);
            nukerBlocks.forEach(settings::addNukerBlocks);
            for (String e : pickConfig.getConfig().getConfigurationSection(concat(n, "enchants")).getKeys(false)) {
                PEnchant enchant = plugin.getPEnchantManager().getEnchantMap().get(e);
                if (enchant == null) {
                    EnhancedPicks.getInstance().getLogger().warning(n + " enchant does not exist.");
                    continue;
                }
                String ePath = concat(n, "enchants");
                ePath = concat(ePath, e);
                String startLevel = concat(ePath, "startLevel");
                String maxLevel = concat(ePath, "maxLevel");
                PEnchant pEnchant = enchant.cloneEnchant();
                if (pickConfig.has(maxLevel)) {
                    int maxLevelInt = pickConfig.get(maxLevel, Integer.class, enchant.getMaxLevel());
                    pEnchant.setMaxLevel(maxLevelInt);
                }
                if (pickConfig.has(startLevel)) {
                    pEnchant.setLevel(pickConfig.get(startLevel, Integer.class, enchant.getLevel()));
                }
                settings.addEnchant(pEnchant);
            }
            for (String s : pickConfig.getConfig().getStringList(concat(n, "skills"))) {
                PSkill skill = plugin.getPSkillManager().getPSkill(s);
                if (skill == null) {
                    EnhancedPicks.getInstance().getLogger().warning(s + " skill does not exist.");
                    continue;
                }
                settings.addSkill(skill);
            }
            this.settingsMap.put(n, settings);
            EnhancedPicks.getInstance().getLogger().info(settings.toString());
        }
        CustomConfig swordConfig = plugin.getConfig("swords");
        for (String n : swordConfig.getConfig().getKeys(false)) {
            PItemSettings settings = new PItemSettings(n, PItemType.SWORD);
            String name = swordConfig.get(concat(n, "name"), String.class, n);
            int xp = swordConfig.get(concat(n, "startXp"), Integer.class, 0);
            int level = swordConfig.get(concat(n, "startLevel"), Integer.class, 1);
            int points = swordConfig.get(concat(n, "startPoints"), Integer.class, 1);
            int ppl = swordConfig.get(concat(n, "pointsPerLevel"), Integer.class, 1);
            settings.setName(name);
            settings.setStartXp(xp);
            settings.setStartLevel(level);
            settings.setStartPoints(points);
            settings.setPointsPerLevel(ppl);
            for (String e : swordConfig.getConfig().getConfigurationSection(concat(n, "enchants")).getKeys(false)) {
                PEnchant enchant = plugin.getPEnchantManager().getEnchantMap().get(e);
                if (enchant == null) {
                    EnhancedPicks.getInstance().getLogger().warning(n + " enchant does not exist.");
                    continue;
                }
                String ePath = concat(n, "enchants");
                ePath = concat(ePath, e);
                String startLevel = concat(ePath, "startLevel");
                String maxLevel = concat(ePath, "maxLevel");
                PEnchant pEnchant = enchant.cloneEnchant();
                if (swordConfig.has(maxLevel)) {
                    pEnchant.setMaxLevel(swordConfig.get(maxLevel, Integer.class, enchant.getMaxLevel()));
                }
                if (swordConfig.has(startLevel)) {
                    pEnchant.setLevel(swordConfig.get(startLevel, Integer.class, enchant.getLevel()));
                }
                settings.addEnchant(pEnchant);
            }
            for (String s : swordConfig.getConfig().getStringList(concat(n, "skills"))) {
                PSkill skill = plugin.getPSkillManager().getPSkill(s);
                if (skill == null) {
                    EnhancedPicks.getInstance().getLogger().warning(s + " skill does not exist.");
                    continue;
                }
                settings.addSkill(skill);
            }
            this.settingsMap.put(n, settings);
            EnhancedPicks.getInstance().getLogger().info(settings.toString());
        }
    }

    public PItem<?> getPItem(ItemStack item) {
        if (item == null) {
            return null;
        }
        if (!item.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) {
            return null;
        }
        for (String l : lore) {
            if (!pItemMap.containsKey(l)) {
                continue;
            }
            return this.pItemMap.get(l);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <P extends Event> PItem<P> getPItem(Class<P> pClass, ItemStack item) {
        if (!item.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) {
            return null;
        }
        for (String l : lore) {
            if (!pItemMap.containsKey(l)) {
                continue;
            }
            PItem<?> pItem = this.pItemMap.get(l);
            if (!pClass.equals(pItem.getEClass())) {
                return null;
            }
            return (PItem<P>) pItem;
        }
        return null;
    }

    public void addPItem(PItem<?> pItem) {
        this.pItemMap.put(pItem.getUuid().toString(), pItem);
    }

    public PItemSettings getSettings(String key) {
        return this.settingsMap.get(key);
    }

    private String concat(String s, String s1) {
        return s + "." + s1;
    }

    @Getter
    @Setter
    public static class PItemSettings {

        private final PItemType      type;
        private final List<PSkill>   skillList;
        private final List<PEnchant> enchantList;
        private       String         key;
        private       String         name;
        private       int            startXp;
        private       int            startLevel;
        private       int            startPoints;
        private       int            pointsPerLevel;
        private       List<String>   nukerBlocks;

        public PItemSettings(String key, PItemType type) {
            this.key = key;
            this.name = key;
            this.type = type;
            this.skillList = new ArrayList<>();
            this.enchantList = new ArrayList<>();
            this.nukerBlocks = new ArrayList<>();
            this.startXp = 0;
            this.startLevel = 0;
            this.startPoints = 1;
            this.pointsPerLevel = 1;
        }

        public void addNukerBlocks(String s) {
            nukerBlocks.add(s);
        }

        public void addSkill(PSkill skill) {
            if (!skillList.contains(skill))
                skillList.add(skill);
        }

        public void addEnchant(PEnchant enchant) {
            if (!enchantList.contains(enchant))
                enchantList.add(enchant);
        }

        public <P extends Event> PItem<P> generate(Class<P> pClass) {
            EnhancedPicks plugin = EnhancedPicks.getInstance();
            Level level = plugin.getLevelManager().getLevel(this.startLevel);
            if (level == null) {
                level = EnhancedPicks.getInstance().getLevelManager().getLevel(1);
            }
            ItemBuilder builder = ItemBuilder.wrap(new ItemStack(type.getType()));
            PItem<P> pItem = null;
            switch (type) {
                case PICK:
                    if (!pClass.equals(BlockBreakEvent.class)) {
                        return null;
                    }
                    pItem = new PItem<>(pClass, this.name, type, builder.build());
                    pItem.setAction((p, e) -> {
                        BlockBreakEvent blockBreakEvent = (BlockBreakEvent) e;
                        p.setBlocksBroken(p.getBlocksBroken() + 1);
                        int xp = BlockValue.getXp(blockBreakEvent.getBlock());
                        p.incrementXp(xp, blockBreakEvent.getPlayer());
                        p.activateEnchants(blockBreakEvent);
                        p.update(blockBreakEvent.getPlayer());
                    });
                    break;
                case SWORD:
                    if (!pClass.equals(EntityDamageByEntityEvent.class)) {
                        return null;
                    }
                    pItem = new PItem<>(pClass, this.name, type, builder.build());
                    pItem.setAction((p, e) -> {
                        EntityDamageByEntityEvent attackEvent = (EntityDamageByEntityEvent) e;
                        int xp = MobValue.getXp(attackEvent.getEntityType());
                        p.incrementXp(xp, (Player) attackEvent.getDamager());
                        p.activateEnchants(attackEvent);
                        p.update((Player) attackEvent.getDamager());
                    });
                    break;
            }
            pItem.setLevel(level);
            pItem.setPoints(startPoints);
            pItem.setPointsPerLevel(pointsPerLevel);
            nukerBlocks.forEach(pItem::addNukerBlocks);
            for (PEnchant pEnchant : this.enchantList) {
                PEnchant clone = pEnchant.cloneEnchant();
                clone.setLevel(pEnchant.getLevel());
                clone.setMaxLevel(pEnchant.getMaxLevel());
                pItem.addEnchant(clone);
            }
            this.skillList.forEach(pItem::addAvailableSkill);
            for (PEnchant pEnchant : pItem.getEnchants()) {
                pEnchant.apply(pItem);
            }
            pItem.setPItemSettings(this.key);
            pItem.updateMeta();
            return pItem;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("\n");
            builder.append("  =====PItemSettings=====").append("\n")
                    .append("  Key: ").append(key).append("\n")
                    .append("  Name: ").append(name).append("\n")
                    .append("  Type: ").append(type.name()).append("\n")
                    .append("  Start XP: ").append(startXp).append("\n")
                    .append("  Start Level: ").append(startLevel).append("\n")
                    .append("  Start Points: ").append(startPoints).append("\n")
                    .append("  Points per level: ").append(pointsPerLevel).append("\n")
                    .append("  Skills: \n");
            for (PSkill pSkill : this.skillList) {
                builder.append("    Name: ").append(pSkill.getName()).append("\n")
                        .append("    Level: ").append(pSkill.getLevel()).append("\n")
                        .append("    Cost: ").append(pSkill.getCost()).append("\n")
                        .append("    Permission: ").append(pSkill.getPerm()).append("\n");
            }
            builder.append("  Enchants: \n");
            for (PEnchant pEnchant : this.enchantList) {
                builder.append("    Name: ").append(pEnchant.getName()).append("\n")
                        .append("    Display Name: ").append(pEnchant.getDisplayName()).append("\n")
                        .append("    Level: ").append(pEnchant.getLevel()).append("\n")
                        .append("    Max Level: ").append(pEnchant.getMaxLevel()).append("\n");
            }
            builder.append("  =====PItemSettings=====").append("\n");
            return builder.toString();
        }
    }
}
