/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.menu;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.menu.menus.*;
import com.minecave.pickaxes.util.config.CustomConfig;
import com.minecave.pickaxes.util.message.Strings;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class MenuManager {

    private EnhancedPicks     plugin;
    @Getter
    private Map<String, Menu> menuMap;

    public MenuManager() {
        plugin = EnhancedPicks.getInstance();
        menuMap = new HashMap<>();
        CustomConfig config = plugin.getConfig("menus");
        menuMap.put("mainPick", new MainPickMenu(Strings.color(config.get("mainPickMenu", String.class, "Main Pick Menu"))));
        menuMap.put("mainSword", new MainSwordMenu(Strings.color(config.get("mainSwordMenu", String.class, "Main Sword Menu"))));
        menuMap.put("skills", new SkillsMenu(Strings.color(config.get("skillsMenu", String.class, "Skills Menu"))));
        menuMap.put("upgrade", new UpgradesMenu(Strings.color(config.get("upgradeMenu", String.class, "Upgrade Menu"))));
        menuMap.put("pick", new PickMenu(Strings.color(config.get("pickaxeMenu", String.class, "Pickaxe Menu"))));
        menuMap.put("sword", new SwordMenu(Strings.color(config.get("swordMenu", String.class, "Sword Menu"))));
    }

    public <M extends Menu> M get(Class<M> mClass, String key) {
        Menu menu = menuMap.get(key);
        if (menu == null || !mClass.isInstance(menu)) {
            return null;
        }
        return mClass.cast(menu);
    }

    public <M extends Menu> M get(Class<M> mClass) {
        for (Menu m : menuMap.values()) {
            if (mClass.isInstance(m)) {
                return mClass.cast(m);
            }
        }
        return null;
    }
}
