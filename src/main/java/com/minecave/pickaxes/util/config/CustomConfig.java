/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.config;

import com.minecave.pickaxes.EnhancedPicks;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

/**
 * @author Ktar5, not2excel
 */
public class CustomConfig {
    private String fileName;
    private FileConfiguration config;
    private File configFile;

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

    public void reloadConfig() {
        if (!configFile.exists())
            EnhancedPicks.get().saveResource(fileName, true);
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (Exception e) {
            EnhancedPicks.get().getLogger().severe(String.format("Couldn't save '%s', because: '%s'", fileName, e.getMessage()));
        }
    }

    public void set(String path, Object value, boolean save) {
        config.set(path, value);
        if (save) {
            saveConfig();
        }
    }

    public void set(String path, Object value) {
        set(path, value, false);
    }

    public boolean has(String path) {
        return config.contains(path);
    }

    public <T> T get(String path, Class<T> tClass) {
        if (!config.contains(path)) {
            throw new IllegalArgumentException(path + " does not exist.");
        }
        if(tClass.isPrimitive()) {
            throw new IllegalArgumentException(tClass + " is of a primitive type. Disallowed type.");
        }
        Object object = config.get(path);
        if (!tClass.isInstance(object)) {
            throw new IllegalArgumentException(path + " is not of type " + tClass.getSimpleName());
        }
        return tClass.cast(object);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String path, Class<T> tClass) {
        if (!config.contains(path)) {
            throw new IllegalArgumentException(path + " does not exist.");
        }
        if(tClass.isPrimitive()) {
            throw new IllegalArgumentException(tClass + " is of a primitive type. Disallowed type.");
        }
        List<?> list = config.getList(path);
        if(list == null || list.isEmpty()) {
            throw new IllegalArgumentException(path + " returns a null/empty list.");
        }
        Object first = list.stream().findFirst();
        if(!tClass.isInstance(first)) {
            throw new IllegalArgumentException(path + " is not a list of type " + tClass.getSimpleName());
        }
        return (List<T>) list;
    }
}
