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
import com.minecave.pickaxes.util.config.CustomConfig;
import com.minecave.pickaxes.util.item.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PItemManager {

    private final EnhancedPicks plugin;
    @Getter
    private Map<ItemStack, PItem> pItemMap;
    @Getter
    private Map<String, PItemSettings> settingsMap;

    public PItemManager() {
        plugin = EnhancedPicks.getInstance();
        pItemMap = new HashMap<>();
        CustomConfig pickConfig = plugin.getConfig("picks");
        CustomConfig swordConfig = plugin.getConfig("swords");

        for (String n : pickConfig.getConfig().getKeys(false)) {
            PItemSettings settings = new PItemSettings(n, PItemType.PICK);
            settings.setKey(n);
            settings.setName(pickConfig.get(concat(n, "name"), String.class, n));
            settings.setStartXp(pickConfig.get(concat(n, "startXp"), Integer.class, 0));
            settings.setStartLevel(pickConfig.get(concat(n, "startLevel"), Integer.class, 1));
            for (String e : pickConfig.getConfig().getConfigurationSection(concat(n, "enchants")).getKeys(false)) {
                PEnchant enchant = PItem.getEnchantMap().get(e);
                if (enchant == null) {
                    EnhancedPicks.getInstance().getLogger().warning(n + " enchant does not exist.");
                    continue;
                }
                String ePath = concat(n, e);
                String startLevel = concat(ePath, "startLevel");
                String maxLevel = concat(ePath, "maxLevel");
                PEnchant pEnchant = enchant.cloneEnchant();
                if (pickConfig.has(maxLevel)) {
                    pEnchant.setMaxLevel(pickConfig.get(maxLevel, Integer.class, enchant.getMaxLevel()));
                }
                if (pickConfig.has(startLevel)) {
                    pEnchant.setLevel(pickConfig.get(startLevel, Integer.class, enchant.getLevel()));
                }
                settings.addEnchant(pEnchant);
            }
            for (String s : pickConfig.getConfig().getStringList(concat(n, "skills"))) {
                Skill skill = Skills.getSkill(s);
                if (skill == null) {
                    EnhancedPicks.getInstance().getLogger().warning(s + " skill does not exist.");
                    continue;
                }
                settings.addSkill(skill);
            }
            this.settingsMap.put(n, settings);
        }

        for (String n : swordConfig.getConfig().getKeys(false)) {
            PItemSettings settings = new PItemSettings(n, PItemType.SWORD);
            settings.setName(swordConfig.get(concat(n, "name"), String.class, n));
            settings.setStartXp(swordConfig.get(concat(n, "startXp"), Integer.class, 0));
            settings.setStartLevel(swordConfig.get(concat(n, "startLevel"), Integer.class, 1));
            for (String e : swordConfig.getConfig().getConfigurationSection(concat(n, "enchants")).getKeys(false)) {
                PEnchant enchant = PItem.getEnchantMap().get(e);
                if (enchant == null) {
                    EnhancedPicks.getInstance().getLogger().warning(n + " enchant does not exist.");
                    continue;
                }
                String ePath = concat(n, e);
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
                PSkill skill = plugin.getPSkillManager().getSkill(s);
                if (skill == null) {
                    EnhancedPicks.getInstance().getLogger().warning(s + " skill does not exist.");
                    continue;
                }
                settings.addSkill(skill);
            }
            this.settingsMap.put(n, settings);
        }
    }

    public void createPItem(ItemStack itemStack, String label, PItemType type) {
        PItemSettings settings = settingsMap.get(label);
        if(settings == null) {

        }
    }

    @Getter
    @Setter
    public static class PItemSettings {

        private String key;
        private String name;
        private final PItemType type;
        private final List<PSkill> skillList;
        private final List<PEnchant> enchantList;
        private int startXp;
        private int startLevel;

        public PItemSettings(String name, PItemType type) {
            this.name = name;
            this.type = type;
            this.skillList = new ArrayList<>();
            this.enchantList = new ArrayList<>();
            this.startXp = 0;
            this.startLevel = 0;
        }

        public void addSkill(PSkill skill) {
            if (!skillList.contains(skill))
                skillList.add(skill);
        }

        public void addEnchant(PEnchant enchant) {
            if (!enchantList.contains(enchant))
                enchantList.add(enchant);
        }

        public PItem generate() {
            PItem pItem;
            Level level = EnhancedPicks.getInstance().getLevelManager().getLevel(this.startLevel);
            if (level == null) {
                level = EnhancedPicks.getInstance().getLevelManager().getLevel(1);
            }
            ItemBuilder builder = ItemBuilder.wrap(new ItemStack(type.getType()));
            pItem = new PItem(this.name, type, builder.build());
            pItem.setLevel(level);
            this.enchantList.forEach(pItem::addEnchant);
            this.skillList.forEach(pItem::addAvailableSkill);
            pItem.setPItemSettings(this.key);
            return pItem;
        }
    }

    private String concat(String s, String s1) {
        return s + "." + s1;
    }
}
