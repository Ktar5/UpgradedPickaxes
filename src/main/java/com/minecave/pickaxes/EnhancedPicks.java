package com.minecave.pickaxes;

import com.minecave.pickaxes.level.LevelManager;
import com.minecave.pickaxes.skill.PSkillManager;
import com.minecave.pickaxes.util.config.CustomConfig;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class EnhancedPicks extends JavaPlugin {

    @Getter
    private static EnhancedPicks instance;
    @Getter
    private Map<String, CustomConfig> configMap;
    @Getter
    private LevelManager levelManager;
    @Getter
    private PSkillManager pSkillManager;

    @Override
    public void onEnable() {
        instance = this;
        configMap = new HashMap<>();
        saveDefaultConfig();

        saveDefaultConfig("config");
        saveDefaultConfig("drops");
        saveDefaultConfig("levels");
        saveDefaultConfig("xp");
        saveDefaultConfig("menus");
        saveDefaultConfig("skills");
        saveDefaultConfig("enchants");

        levelManager = new LevelManager();
        pSkillManager = new PSkillManager();
    }

    @Override
    public void onDisable() {
    }

    public void saveDefaultConfig(String name) {
        String fileName = name + ".yml";
        configMap.put(name, new CustomConfig(getDataFolder(), fileName));
    }

    public CustomConfig getConfig(String name) {
        return configMap.get(name);
    }
}
