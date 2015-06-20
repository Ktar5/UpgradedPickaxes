/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.level;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.util.config.CustomConfig;
import com.minecave.pickaxes.util.firework.FireworkBuilder;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public class LevelManager {

    private final EnhancedPicks plugin;
    @Getter
    private Map<Integer, Level> levelMap;
    private Level exampleLevel = null;
    private FireworkBuilder defaultBuilder;
    @Getter
    private List<Integer> blackList = new ArrayList<>();
    @Getter
    private List<String> levelUpMessage = new ArrayList<>();

    public LevelManager() {
        plugin = EnhancedPicks.getInstance();
        levelMap = new HashMap<>();

        CustomConfig pluginConfig = plugin.getConfig("config");
        ConfigurationSection firework = pluginConfig.getConfigurationSection("cosmetics.firework");
        boolean perLevel = firework.getBoolean("determinePerLevel");
        if (!perLevel) {
            defaultBuilder = new FireworkBuilder();
            if (firework.get("blacklist") != null) {
                this.blackList = firework.getIntegerList("blacklist");
            }
            defaultBuilder.amount(firework.getInt("amount"));
            defaultBuilder.playerOnly(firework.getBoolean("playerOnly"));
            defaultBuilder.range(firework.getInt("range"));
            firework.getStringList("colors").forEach(defaultBuilder::addColor);
            firework.getStringList("fade-colors").forEach(defaultBuilder::addFadeColor);
            defaultBuilder.trail(firework.getBoolean("trail"));
            defaultBuilder.flicker(firework.getBoolean("flicker"));
        }
        levelUpMessage.addAll(pluginConfig.getConfig().getStringList("level-up-message"));

        CustomConfig levelConfig = plugin.getConfig("levels");
        for (String levelKey : levelConfig.getConfigurationSection("levels").getKeys(false)) {
            int level = Integer.parseInt(levelKey);
            int xp = levelConfig.get(levelKey + ".xp", Integer.class);
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
                levelObject = new Level(xp, level, commands, fireworkBuilder);
            } else {
                levelObject = new Level(xp, level, commands, defaultBuilder);
            }
            if (exampleLevel == null) {
                exampleLevel = levelObject;
            }
            this.levelMap.put(level, levelObject);
        }
    }

    public Level getLevel(int id) {
        if (!levelMap.containsKey(id)) {
            if (exampleLevel != null) {
                levelMap.put(id, new Level(id, exampleLevel.getXp(), exampleLevel.getCommands(), exampleLevel.getFireworkBuilder()));
            } else {
                levelMap.put(id, new Level(100, id, Collections.singletonList("give $player$ diamond 1"), defaultBuilder));
            }
        }
        return levelMap.get(id);
    }
}
