package com.minecave.pickaxes;

import com.minecave.pickaxes.level.LevelManager;
import com.minecave.pickaxes.util.config.CustomConfig;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Carter on 6/4/2015.
 */
public class EnhancedPicks extends JavaPlugin {

    @Getter
    private static EnhancedPicks instance;
    @Getter
    private Map<String, CustomConfig> configMap;
    @Getter
    private LevelManager levelManager;

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
