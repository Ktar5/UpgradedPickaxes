/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.item;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.config.CustomConfig;
import com.minecave.pickaxes.util.firework.FireworkBuilder;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PItemManager {

    private final EnhancedPicks                   plugin;
    @Getter
    private       Map<String, PItem<?>>           pItemMap;
    @Getter
    private       Multimap<String, PItemSettings> settingsMap;

    public PItemManager() {
        plugin = EnhancedPicks.getInstance();
        pItemMap = new HashMap<>();
        settingsMap = ArrayListMultimap.create();

        CustomConfig pluginConfig = plugin.getConfig("config");
        CustomConfig levelConfig = plugin.getConfig("levels");

        //PICKS
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
                    int sLevel = pickConfig.get(startLevel, Integer.class, enchant.getLevel());
                    pEnchant.setLevel(sLevel);
                    pEnchant.setStartLevel(sLevel);
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

            ConfigurationSection firework = pluginConfig.getConfigurationSection("cosmetics.firework");
            boolean perLevel = firework.getBoolean("determinePerLevel");
            if (!perLevel) {
                settings.setDefaultBuilder(new FireworkBuilder());
                if (firework.get("blacklist") != null) {
                    settings.getBlackList().addAll(firework.getIntegerList("blacklist"));
                }
                settings.getDefaultBuilder().amount(firework.getInt("amount"));
                settings.getDefaultBuilder().playerOnly(firework.getBoolean("playerOnly"));
                settings.getDefaultBuilder().range(firework.getInt("range"));
                firework.getStringList("colors").forEach(settings.getDefaultBuilder()::addColor);
                firework.getStringList("fade-colors").forEach(settings.getDefaultBuilder()::addFadeColor);
                settings.getDefaultBuilder().trail(firework.getBoolean("trail"));
                settings.getDefaultBuilder().flicker(firework.getBoolean("flicker"));
            }
            settings.getLevelUpMessage().addAll(pluginConfig.getConfig().getStringList("level-up-message"));
            if (pickConfig.has(n + ".max-level")) {
                settings.setMaxLevel(pickConfig.get(n + ".max-level", Integer.class, 10));
            } else {
                settings.setMaxLevel(levelConfig.get("max-level", Integer.class, 10));
            }
            if (pickConfig.has(concat(n, "levels"))) {
                for (String levelKey : pickConfig.getConfigurationSection(n + ".levels").getKeys(false)) {
                    int lvl = Integer.parseInt(levelKey);
                    levelKey = "levels." + levelKey;
                    int lvlXp = pickConfig.get(concat(n, levelKey) + ".xp", Integer.class);
                    List<String> commands = pickConfig.getConfig().getStringList(concat(n, levelKey) + ".commands");
                    ConfigurationSection fireworkSection = pickConfig.getConfigurationSection(concat(n, levelKey) + ".cosmetics.firework");
                    Level levelObject;
                    if (fireworkSection.getBoolean("enabled")) {
                        FireworkBuilder fireworkBuilder = new FireworkBuilder();
                        fireworkBuilder.amount(fireworkSection.getInt("amount"));
                        fireworkBuilder.playerOnly(fireworkSection.getBoolean("playerOnly"));
                        fireworkBuilder.range(fireworkSection.getInt("range"));
                        fireworkSection.getStringList("colors").forEach(fireworkBuilder::addColor);
                        fireworkSection.getStringList("fade-colors").forEach(fireworkBuilder::addFadeColor);
                        fireworkBuilder.trail(fireworkSection.getBoolean("trail"));
                        fireworkBuilder.flicker(fireworkSection.getBoolean("flicker"));
                        levelObject = new Level(settings, lvl, lvlXp, commands, fireworkBuilder);
                    } else {
                        levelObject = new Level(settings, lvl, lvlXp, commands, settings.getDefaultBuilder());
                    }
                    if (settings.getExampleLevel() == null) {
                        settings.setExampleLevel(levelObject);
                    }
                    settings.getLevelMap().put(lvl, levelObject);
                }
                for (int i = 1; i <= settings.getMaxLevelInt(); i++) {
                    if (!settings.getLevelMap().containsKey(i)) {
                        if (settings.getExampleLevel() != null) {
                            settings.getLevelMap().put(i, new Level(settings, i, settings.getExampleLevel().getXp(),
                                                                    settings.getExampleLevel().getCommands(), settings.getExampleLevel().getFireworkBuilder()));
                        } else {
                            settings.getLevelMap().put(i, new Level(settings, i, 100,
                                                                    Collections.singletonList("give $player$ diamond 1"), settings.getDefaultBuilder()));
                        }
                    }
                }
            } else {
                for (String levelKey : levelConfig.getConfigurationSection("levels").getKeys(false)) {
                    int lvl = Integer.parseInt(levelKey);
                    levelKey = "levels." + levelKey;
                    int lvlXp = levelConfig.get(levelKey + ".xp", Integer.class);
                    List<String> commands = levelConfig.getConfig().getStringList(levelKey + ".commands");
                    ConfigurationSection fireworkSection = levelConfig.getConfigurationSection(levelKey + ".cosmetics.firework");
                    Level levelObject;
                    if (fireworkSection.getBoolean("enabled")) {
                        FireworkBuilder fireworkBuilder = new FireworkBuilder();
                        fireworkBuilder.amount(fireworkSection.getInt("amount"));
                        fireworkBuilder.playerOnly(fireworkSection.getBoolean("playerOnly"));
                        fireworkBuilder.range(fireworkSection.getInt("range"));
                        fireworkSection.getStringList("colors").forEach(fireworkBuilder::addColor);
                        fireworkSection.getStringList("fade-colors").forEach(fireworkBuilder::addFadeColor);
                        fireworkBuilder.trail(fireworkSection.getBoolean("trail"));
                        fireworkBuilder.flicker(fireworkSection.getBoolean("flicker"));
                        levelObject = new Level(settings, lvl, lvlXp, commands, fireworkBuilder);
                    } else {
                        levelObject = new Level(settings, lvl, lvlXp, commands, settings.getDefaultBuilder());
                    }
                    if (settings.getExampleLevel() == null) {
                        settings.setExampleLevel(levelObject);
                    }
                    settings.getLevelMap().put(lvl, levelObject);
                }
                for (int i = 1; i <= settings.getMaxLevelInt(); i++) {
                    if (!settings.getLevelMap().containsKey(i)) {
                        if (settings.getExampleLevel() != null) {
                            settings.getLevelMap().put(i, new Level(settings, i, settings.getExampleLevel().getXp(),
                                                                    settings.getExampleLevel().getCommands(), settings.getExampleLevel().getFireworkBuilder()));
                        } else {
                            settings.getLevelMap().put(i, new Level(settings, i, 100,
                                                                    Collections.singletonList("give $player$ diamond 1"), settings.getDefaultBuilder()));
                        }
                    }
                }
            }

            this.settingsMap.put(n, settings);
            if (pluginConfig.has("debug") && pluginConfig.get("debug", Boolean.class, false)) {
                if (pluginConfig.has("compressedDebug") && !pluginConfig.get("compressedDebug", Boolean.class, false)) {
                    EnhancedPicks.getInstance().getLogger().info(settings.toString());
                } else {
                    EnhancedPicks.getInstance().getLogger().info(settings.compress());
                }
            }
        }
        //SWORDS
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
                    int sLevel = swordConfig.get(startLevel, Integer.class, enchant.getLevel());
                    pEnchant.setLevel(sLevel);
                    pEnchant.setStartLevel(sLevel);
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

            ConfigurationSection firework = pluginConfig.getConfigurationSection("cosmetics.firework");
            boolean perLevel = firework.getBoolean("determinePerLevel");
            if (!perLevel) {
                settings.setDefaultBuilder(new FireworkBuilder());
                if (firework.get("blacklist") != null) {
                    settings.getBlackList().addAll(firework.getIntegerList("blacklist"));
                }
                settings.getDefaultBuilder().amount(firework.getInt("amount"));
                settings.getDefaultBuilder().playerOnly(firework.getBoolean("playerOnly"));
                settings.getDefaultBuilder().range(firework.getInt("range"));
                firework.getStringList("colors").forEach(settings.getDefaultBuilder()::addColor);
                firework.getStringList("fade-colors").forEach(settings.getDefaultBuilder()::addFadeColor);
                settings.getDefaultBuilder().trail(firework.getBoolean("trail"));
                settings.getDefaultBuilder().flicker(firework.getBoolean("flicker"));
            }
            settings.getLevelUpMessage().addAll(pluginConfig.getConfig().getStringList("level-up-message"));
            if (swordConfig.has(n + ".max-level")) {
                settings.setMaxLevel(swordConfig.get(n + ".max-level", Integer.class, 10));
            } else {
                settings.setMaxLevel(levelConfig.get("max-level", Integer.class, 10));
            }
            if (swordConfig.has(concat(n, "levels"))) {
                for (String levelKey : swordConfig.getConfigurationSection(n + ".levels").getKeys(false)) {
                    int lvl = Integer.parseInt(levelKey);
                    levelKey = "levels." + levelKey;
                    int lvlXp = swordConfig.get(concat(n, levelKey) + ".xp", Integer.class);
                    List<String> commands = swordConfig.getConfig().getStringList(concat(n, levelKey) + ".commands");
                    ConfigurationSection fireworkSection = swordConfig.getConfigurationSection(concat(n, levelKey) + ".cosmetics.firework");
                    Level levelObject;
                    if (fireworkSection.getBoolean("enabled")) {
                        FireworkBuilder fireworkBuilder = new FireworkBuilder();
                        fireworkBuilder.amount(fireworkSection.getInt("amount"));
                        fireworkBuilder.playerOnly(fireworkSection.getBoolean("playerOnly"));
                        fireworkBuilder.range(fireworkSection.getInt("range"));
                        fireworkSection.getStringList("colors").forEach(fireworkBuilder::addColor);
                        fireworkSection.getStringList("fade-colors").forEach(fireworkBuilder::addFadeColor);
                        fireworkBuilder.trail(fireworkSection.getBoolean("trail"));
                        fireworkBuilder.flicker(fireworkSection.getBoolean("flicker"));
                        levelObject = new Level(settings, lvl, lvlXp, commands, fireworkBuilder);
                    } else {
                        levelObject = new Level(settings, lvl, lvlXp, commands, settings.getDefaultBuilder());
                    }
                    if (settings.getExampleLevel() == null) {
                        settings.setExampleLevel(levelObject);
                    }
                    settings.getLevelMap().put(lvl, levelObject);
                }
                for (int i = 1; i <= settings.getMaxLevelInt(); i++) {
                    if (!settings.getLevelMap().containsKey(i)) {
                        if (settings.getExampleLevel() != null) {
                            settings.getLevelMap().put(i, new Level(settings, i, settings.getExampleLevel().getXp(),
                                                                    settings.getExampleLevel().getCommands(), settings.getExampleLevel().getFireworkBuilder()));
                        } else {
                            settings.getLevelMap().put(i, new Level(settings, i, 100,
                                                                    Collections.singletonList("give $player$ diamond 1"), settings.getDefaultBuilder()));
                        }
                    }
                }
            } else {
                for (String levelKey : levelConfig.getConfigurationSection("levels").getKeys(false)) {
                    int lvl = Integer.parseInt(levelKey);
                    levelKey = "levels." + levelKey;
                    int lvlXp = levelConfig.get(levelKey + ".xp", Integer.class);
                    List<String> commands = levelConfig.getConfig().getStringList(levelKey + ".commands");
                    ConfigurationSection fireworkSection = levelConfig.getConfigurationSection(levelKey + ".cosmetics.firework");
                    Level levelObject;
                    if (fireworkSection.getBoolean("enabled")) {
                        FireworkBuilder fireworkBuilder = new FireworkBuilder();
                        fireworkBuilder.amount(fireworkSection.getInt("amount"));
                        fireworkBuilder.playerOnly(fireworkSection.getBoolean("playerOnly"));
                        fireworkBuilder.range(fireworkSection.getInt("range"));
                        fireworkSection.getStringList("colors").forEach(fireworkBuilder::addColor);
                        fireworkSection.getStringList("fade-colors").forEach(fireworkBuilder::addFadeColor);
                        fireworkBuilder.trail(fireworkSection.getBoolean("trail"));
                        fireworkBuilder.flicker(fireworkSection.getBoolean("flicker"));
                        levelObject = new Level(settings, lvl, lvlXp, commands, fireworkBuilder);
                    } else {
                        levelObject = new Level(settings, lvl, lvlXp, commands, settings.getDefaultBuilder());
                    }
                    if (settings.getExampleLevel() == null) {
                        settings.setExampleLevel(levelObject);
                    }
                    settings.getLevelMap().put(lvl, levelObject);
                }
                for (int i = 1; i <= settings.getMaxLevelInt(); i++) {
                    if (!settings.getLevelMap().containsKey(i)) {
                        if (settings.getExampleLevel() != null) {
                            settings.getLevelMap().put(i, new Level(settings, i, settings.getExampleLevel().getXp(),
                                                                    settings.getExampleLevel().getCommands(), settings.getExampleLevel().getFireworkBuilder()));
                        } else {
                            settings.getLevelMap().put(i, new Level(settings, i, 100,
                                                                    Collections.singletonList("give $player$ diamond 1"), settings.getDefaultBuilder()));
                        }
                    }
                }
            }

            this.settingsMap.put(n, settings);
            if (pluginConfig.has("debug") && pluginConfig.get("debug", Boolean.class, false)) {
                if (pluginConfig.has("compressedDebug") && !pluginConfig.get("compressedDebug", Boolean.class, false)) {
                    EnhancedPicks.getInstance().getLogger().info(settings.toString());
                } else {
                    EnhancedPicks.getInstance().getLogger().info(settings.compress());
                }
            }
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
            l = l.replace(ChatColor.COLOR_CHAR + "", "");
            if (l.startsWith("UUID:")) {
                String u = l.replace("UUID:", "");
                if (!pItemMap.containsKey(u)) {
                    continue;
                }
                return this.pItemMap.get(u);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <P extends Event> PItem<P> getPItem(Class<P> pClass, ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null || lore.isEmpty()) {
            return null;
        }
        for (String l : lore) {
            if (!l.contains(ChatColor.COLOR_CHAR + "U")) {
                continue;
            }
            l = l.replace(ChatColor.COLOR_CHAR + "", "");
            if (l.startsWith("UUID:")) {
                String u = l.replace("UUID:", "");
                if (!pItemMap.containsKey(u)) {
                    continue;
                }
                PItem<?> pItem = this.pItemMap.get(u);
                if (!pClass.equals(pItem.getEClass())) {
                    return null;
                }
                return (PItem<P>) pItem;
            }
        }
        return null;
    }

    public void addPItem(PItem<?> pItem) {
        this.pItemMap.putIfAbsent(pItem.getUuid().toString(), pItem);
    }

    public void addPItemForce(PItem<?> pItem) {
        this.pItemMap.put(pItem.getUuid().toString(), pItem);
    }

    public Collection<PItemSettings> getSettings(String key) {
        return this.settingsMap.get(key);
    }

    private String concat(String s, String s1) {
        return s + "." + s1;
    }
}
