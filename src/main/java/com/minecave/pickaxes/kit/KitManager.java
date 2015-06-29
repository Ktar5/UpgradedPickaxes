/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.kit;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.util.config.CustomConfig;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class KitManager {

    private EnhancedPicks    plugin;
    @Getter
    private Map<String, Kit> kitMap;

    public KitManager() {
        this.plugin = EnhancedPicks.getInstance();
        this.kitMap = new HashMap<>();
    }

    public void load() {
        CustomConfig config = plugin.getConfig("kits");
        for (String key : config.getKeys(false)) {
            boolean pick = config.get(key + ".pick", Boolean.class, false);
            boolean sword = config.get(key + ".sword", Boolean.class, false);
            String kitName = config.get(key + ".kitname", String.class, "");
            if(kitName.equals("") && !pick && !sword) {
                continue;
            }
            this.kitMap.put(kitName, new Kit(key, pick, sword));
        }
    }
}
