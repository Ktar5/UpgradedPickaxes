package com.minecave.pickaxes;

import com.minecave.pickaxes.util.config.CustomConfig;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Carter on 6/4/2015.
 */
public class EnhancedPicks extends JavaPlugin {

    private static EnhancedPicks instance;

    public static EnhancedPicks get() {
        return instance;
    }

    @Getter
    private Map<String, CustomConfig> configMap;

    @Override
    public void onEnable() {
        instance = this;
        configMap = new HashMap<>();
        saveDefaultConfig();

        attemptSaveResource("drops");
        attemptSaveResource("levels");
        attemptSaveResource("xp");
        attemptSaveResource("menus");
        attemptSaveResource("skills");
        attemptSaveResource("enchants");


    }

    @Override
    public void onDisable() {
    }

    public void attemptSaveResource(String name) {
        String fileName = name + ".yml";
        configMap.put(name, new CustomConfig(getDataFolder(), fileName));
    }
}
