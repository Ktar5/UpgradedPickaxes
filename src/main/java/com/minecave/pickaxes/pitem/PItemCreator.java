/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.pitem;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.items.ItemBuilder;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.Skill;
import com.minecave.pickaxes.skill.Skills;
import com.minecave.pickaxes.utils.CustomConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PItemCreator {

    @Getter
    private Map<String, PItemSettings> settingsMap;

    public PItemCreator() {
        this.settingsMap = new HashMap<>();
        CustomConfig pickConfig = new CustomConfig(PickaxesRevamped.getInstance().getDataFolder(), "picks.yml");
        CustomConfig swordConfig = new CustomConfig(PickaxesRevamped.getInstance().getDataFolder(), "swords.yml");
        for (String n : pickConfig.getConfig().getKeys(false)) {
            PItemSettings settings = new PItemSettings(n, PItemType.PICK);
            settings.setKey(n);
            settings.setName(pickConfig.get(concat(n, "name"), String.class, n));
            settings.setStartXp(pickConfig.get(concat(n, "startXp"), Integer.class, 0));
            settings.setStartLevel(pickConfig.get(concat(n, "startLevel"), Integer.class, 1));
            for (String e : pickConfig.getConfig().getConfigurationSection(concat(n, "enchants")).getKeys(false)) {
                PEnchant enchant = PItem.getEnchantMap().get(e);
                if (enchant == null) {
                    PickaxesRevamped.getInstance().getLogger().warning(n + " enchant does not exist.");
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
                    PickaxesRevamped.getInstance().getLogger().warning(s + " skill does not exist.");
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
                    PickaxesRevamped.getInstance().getLogger().warning(n + " enchant does not exist.");
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
                Skill skill = Skills.getSkill(s);
                if (skill == null) {
                    PickaxesRevamped.getInstance().getLogger().warning(s + " skill does not exist.");
                    continue;
                }
                settings.addSkill(skill);
            }
            this.settingsMap.put(n, settings);
        }
    }

    public PItemSettings get(String name) {
        return this.settingsMap.get(name);
    }

    public boolean has(String name) {
        return this.settingsMap.containsKey(name);
    }

    public String concat(String key, String key1) {
        return key + "." + key1;
    }

    public static boolean isSword(ItemStack item) {
        return item.getType() != Material.DIAMOND_SWORD &&
                item.getType() != Material.IRON_SWORD &&
                item.getType() != Material.GOLD_SWORD &&
                item.getType() != Material.STONE_SWORD &&
                item.getType() != Material.WOOD_SWORD;
    }

    public static boolean isPick(ItemStack item) {
        return item.getType() != Material.DIAMOND_PICKAXE &&
                item.getType() != Material.IRON_PICKAXE &&
                item.getType() != Material.GOLD_PICKAXE &&
                item.getType() != Material.STONE_PICKAXE &&
                item.getType() != Material.WOOD_PICKAXE;
    }

    @Getter
    @Setter
    public static class PItemSettings {

        private String key;
        private String name;
        private final PItemType type;
        private final List<Skill> skillList;
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

        public void addSkill(Skill skill) {
            if (!skillList.contains(skill))
                skillList.add(skill);
        }

        public void addEnchant(PEnchant enchant) {
            if (!enchantList.contains(enchant))
                enchantList.add(enchant);
        }

        //TODO: Generate
        public <P extends PItem> P generate(Class<P> clazz) {
            PItem pItem = null;
            Level level = Level.getLevels().get(this.startLevel);
            if(level == null) level = Level.ONE;
            switch(type) {
                case PICK:
                    ItemBuilder builder = ItemBuilder.wrap(new ItemStack(Material.DIAMOND_PICKAXE));
                    pItem = new Pickaxe(builder.build(), level, this.startXp, this.name, null);
                    break;
                case SWORD:
                    builder = ItemBuilder.wrap(new ItemStack(Material.DIAMOND_SWORD));
                    pItem = new Sword(builder.build(), level, this.startXp, this.name, null);
                    break;
            }
            this.enchantList.forEach(pItem::addEnchant);
            pItem.setPSettings(this.key);
            return clazz.cast(pItem);
        }
    }

    public enum PItemType {
        PICK(Pickaxe.class),
        SWORD(Sword.class);

        @Getter
        private Class<? extends PItem> type;

        PItemType(Class<? extends PItem> type) {
            this.type = type;
        }
    }
}
