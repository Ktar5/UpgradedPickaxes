/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.drops;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.util.config.CustomConfig;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class DropManager {

    private EnhancedPicks plugin;
    private Map<Material, BlockValue> blockValues = new HashMap<>();
    private Map<EntityType, MobValue> mobValues   = new HashMap<>();
    private List<BlockDrop>           blockDrops  = new ArrayList<>();
    private List<MobDrop>             mobDrops    = new ArrayList<>();

    public DropManager() {
        this.plugin = EnhancedPicks.getInstance();

        CustomConfig config = plugin.getConfig("xp");
        ConfigurationSection section = config.getConfigurationSection("blocks");
        for (String s : section.getKeys(false)) {
            Material material = Material.matchMaterial(s);
            int xp = section.getInt(s);
            BlockValue value = new BlockValue(xp, material);
            blockValues.put(material, value);
        }
        section = config.getConfigurationSection("mob");
        for (String s : section.getKeys(false)) {
            EntityType entity = EntityType.valueOf(s);
            int xp = section.getInt(s);
            MobValue value = new MobValue(xp, entity);
            mobValues.put(entity, value);
        }

        config = plugin.getConfig("drops");
        ConfigurationSection drops = config.getConfigurationSection("blocks");
        for (String s : drops.getKeys(false)) {
            section = drops.getConfigurationSection(s);
            for (String l : section.getKeys(false)) {
                ConfigurationSection levels = section.getConfigurationSection("levels");
                for (String d : levels.getKeys(false)) {
                    List<String> dropList = levels.getStringList(d + ".drops");
                    for (String raw : dropList) {
                        String[] str = raw.split("%");
                        String command = str[0];
                        int weight = Integer.parseInt(str[1]);
                        BlockDrop blockDrop = new BlockDrop(weight, command, Integer.parseInt(d));
                        blockDrops.add(blockDrop);
                    }
                }
            }
        }
        drops = config.getConfigurationSection("mobs");
        for (String s : drops.getKeys(false)) {
            section = drops.getConfigurationSection(s);
            for (String l : section.getKeys(false)) {
                ConfigurationSection levels = section.getConfigurationSection("levels");
                for (String d : levels.getKeys(false)) {
                    List<String> dropList = levels.getStringList(d + ".drops");
                    for (String raw : dropList) {
                        String[] str = raw.split("%");
                        String command = str[0];
                        int weight = Integer.parseInt(str[1]);
                        MobDrop mobDrop = new MobDrop(weight, command, Integer.parseInt(d));
                        mobDrops.add(mobDrop);
                    }
                }
            }
        }
    }
}
