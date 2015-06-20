/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.skill;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.util.config.CustomConfig;

import java.util.HashMap;
import java.util.Map;

public class PSkillManager {

    private EnhancedPicks plugin;
    private Map<String, PSkill> skillMap;

    public PSkillManager() {
        this.plugin = EnhancedPicks.getInstance();
        skillMap = new HashMap<>();

        CustomConfig skillConfig = plugin.getConfig("skills");
        skillConfig.getKeys(false).forEach(s -> {
            skillMap.put(s, new PSkill(skillConfig.getConfigurationSection(s)));
        });
    }

    public PSkill getSkill(String name) {
        if(!skillMap.containsKey(name)) {
            throw new IllegalArgumentException(name + " skill does not exist.");
        }
        return skillMap.get(name);
    }
}
