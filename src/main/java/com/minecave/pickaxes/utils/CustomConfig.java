/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.utils;

import com.minecave.pickaxes.PickaxesRevamped;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

/**
 * @author Ktar5
 */
public class CustomConfig {
    private String fileName;
    private FileConfiguration config;
    private File configFile;

    public CustomConfig(Player player) {
        File dir = new File(PickaxesRevamped.getInstance().getDataFolder(), "players");
        dir.mkdirs();
        this.fileName = player.getUniqueId().toString();
        if(!fileName.endsWith(".yml")) {
            fileName += ".yml";
        }
        configFile = new File(dir, fileName);
        reloadConfigPlayer();
    }

    public CustomConfig(File folder, String fileName) {
        this.fileName = fileName;
        configFile = new File(folder, fileName);
        reloadConfig();
    }

    public String getFileName() {
        return fileName;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public File getConfigFile() {
        return configFile;
    }

    public void reloadConfigPlayer() {
        if(!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void reloadConfig() {
        if (!configFile.exists()){
            PickaxesRevamped.getInstance().saveResource(fileName, true);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (Exception e) {
            PickaxesRevamped.getInstance().getLogger().severe(String.format("Couldn't save '%s', because: '%s'", fileName, e.getMessage()));
        }
    }

    public void set(String path, Object value, boolean save) {
        config.set(path, value);
        if (save)
            saveConfig();
    }

    public void set(String path, Object value) {
        set(path, value, false);
    }

    public boolean has(String path) {
        return config.contains(path);
    }

    public <T> T get(String path, Class<T> tClass, T tDefault) {
        if(!config.contains(path)) {
            return tDefault;
        }
        Object object = config.get(path);
        if(!tClass.isInstance(object)) {
            return tDefault;
        }
        return tClass.cast(object);
    }

    public <T> T get(String path, Class<T> tClass) {
        if(!config.contains(path)) {
            throw new IllegalArgumentException(path + " does not exist.");
        }
        Object object = config.get(path);
        if(!tClass.isInstance(object)) {
            throw new IllegalArgumentException(path + " is not of type " + tClass.getSimpleName());
        }
        return tClass.cast(object);
    }
}
