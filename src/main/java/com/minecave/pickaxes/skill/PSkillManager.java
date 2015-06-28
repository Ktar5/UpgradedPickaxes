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
import com.minecave.pickaxes.skill.pick.*;
import com.minecave.pickaxes.skill.sword.*;
import com.minecave.pickaxes.util.config.CustomConfig;
import com.minecave.pickaxes.util.message.Strings;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class PSkillManager {

    private EnhancedPicks       plugin;
    private Map<String, PSkill> skillMap;

    public PSkillManager() {
        this.plugin = EnhancedPicks.getInstance();
        skillMap = new HashMap<>();
        loadPSkills();
    }

    public String getPSkillKey(PSkill skill) {
        for (Map.Entry<String, PSkill> entry : skillMap.entrySet()) {
            if (entry.getValue().equals(skill)) {
                return entry.getKey();
            }
        }
        return "null";
    }

    public void loadPSkills() {
        CustomConfig config = plugin.getConfig("skills");

        ConfigurationSection eq = config.getConfigurationSection("earthquake");
        Earthquake earthquake = new Earthquake(eq.getInt("radius"),
                color(eq.getString("name")),
                eq.getInt("cooldown"),
                eq.getInt("levelUnlocked"),
                eq.getInt("cost"),
                eq.getString("permission"));
        skillMap.put("earthquake", earthquake);

        ConfigurationSection ice = config.getConfigurationSection("ice");
        Ice iceS = new Ice(color(ice.getString("name")),
                ice.getInt("cooldown"),
                ice.getInt("levelUnlocked"),
                ice.getInt("cost"),
                ice.getString("permission"),
                ice.getInt("radius"));
        skillMap.put("ice", iceS);

        ConfigurationSection tnt = config.getConfigurationSection("tnt");
        Bomber bomber = new Bomber(color(tnt.getString("name")),
                tnt.getInt("cooldown"),
                tnt.getInt("levelUnlocked"),
                tnt.getInt("cost"),
                tnt.getString("permission"),
                tnt.getInt("maxBlocks"),
                tnt.getInt("fuse"),
                tnt.getBoolean("toSeconds"));
        skillMap.put("bomber", bomber);
        skillMap.put("tnt", bomber);

        ConfigurationSection light = config.getConfigurationSection("lightning");
        Lightning lightning = new Lightning(color(light.getString("name")),
                light.getInt("cooldown"),
                light.getInt("levelUnlocked"),
                light.getInt("cost"),
                light.getString("permission"),
                light.getInt("depth"),
                light.getInt("distance"));
        skillMap.put("lightning", lightning);

        ConfigurationSection shot = config.getConfigurationSection("shotgun");
        Shotgun shotgun = new Shotgun(color(shot.getString("name")),
                shot.getInt("cooldown"),
                shot.getInt("levelUnlocked"),
                shot.getInt("cost"),
                shot.getString("permission"),
                shot.getInt("numberOfSnowballs"));
        skillMap.put("shotgun", shotgun);

        ConfigurationSection acidC = config.getConfigurationSection("acid");
        Acid acid = new Acid(color(acidC.getString("name")),
                acidC.getInt("cooldown"),
                acidC.getInt("levelUnlocked"),
                acidC.getInt("cost"),
                acidC.getString("permission"),
                acidC.getInt("numberOfAcidParts"),
                acidC.getInt("radiusPerHit"));
        skillMap.put("acid", acid);

        ConfigurationSection r = config.getConfigurationSection("rain");
        Rain rain = new Rain(color(r.getString("name")),
                r.getInt("cooldown"),
                r.getInt("levelUnlocked"),
                r.getInt("cost"),
                r.getString("permission"),
                r.getInt("arrowHeight"),
                r.getInt("arrowCount"),
                r.getInt("seconds"));
        skillMap.put("rain", rain);

        ConfigurationSection fire = config.getConfigurationSection("fireball");
        FireballSkill fireball = new FireballSkill(color(fire.getString("name")),
                fire.getInt("cooldown"),
                fire.getInt("levelUnlocked"),
                fire.getInt("cost"),
                fire.getString("permission"));
        skillMap.put("fireball", fireball);

        ConfigurationSection nuk = config.getConfigurationSection("nuker");
        Nuker nuker = new Nuker(color(nuk.getString("name")),
                nuk.getInt("levelUnlocked"),
                nuk.getInt("cost"),
                nuk.getString("permission"));
        skillMap.put("nuker", nuker);
        ConfigurationSection rag = config.getConfigurationSection("rage");
        Rage rage = new Rage(color(rag.getString("name")),
                rag.getInt("cooldown"),
                rag.getInt("levelUnlocked"),
                rag.getInt("cost"),
                rag.getString("permission"));
        skillMap.put("rage", rage);
    }

    public PSkill getPSkill(String name) {
        if (!skillMap.containsKey(name)) {
            throw new IllegalArgumentException(name + " skill does not exist.");
        }
        return skillMap.get(name);
    }

    public <P extends PSkill> P getPSkill(Class<P> pClass) {
        for (PSkill pSkill : skillMap.values()) {
            if (pClass.isInstance(pSkill)) {
                return pClass.cast(pSkill);
            }
        }
        return null;
    }

    private String color(String s) {
        return Strings.color(s);
    }
}
