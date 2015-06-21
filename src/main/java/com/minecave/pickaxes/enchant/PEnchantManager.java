/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.enchant;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.enchant.enchants.LuckEnchant;
import com.minecave.pickaxes.enchant.enchants.NormalEnchant;
import com.minecave.pickaxes.enchant.enchants.TnTEnchant;
import com.minecave.pickaxes.util.config.CustomConfig;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PEnchantManager {

    @Getter
    private Map<String, PEnchant> enchantMap;

    public PEnchantManager() {
        enchantMap = new HashMap<>();

        CustomConfig config = EnhancedPicks.getInstance().getConfig("enchants");
        List<String> enchants = config.getConfig().getStringList("availableEnchants");
        for (String s : enchants) {
            switch (s) {
                case "tnt":
                    enchantMap.put(s, new TnTEnchant());
                    break;
                case "luck":
                    enchantMap.put(s, new LuckEnchant());
                    break;
                default:
                    if (NormalEnchant.VanillaPick.has(s)) {
                        NormalEnchant.VanillaPick pick = NormalEnchant.VanillaPick.valueOf(s.toUpperCase());
                        if (pick != null) {
                            NormalEnchant base = new NormalEnchant(pick.getEnchantment());
                            enchantMap.put(s, base);
                            if (s.contains("_")) {
                                enchantMap.put(s.replace("_", " "), base);
                                enchantMap.put(s.replace("_", ""), base);
                            }
                        }
                    } else if (NormalEnchant.VanillaSword.has(s)) {
                        NormalEnchant.VanillaSword sword = NormalEnchant.VanillaSword.valueOf(s.toUpperCase());
                        if (sword != null) {
                            NormalEnchant base = new NormalEnchant(sword.getEnchantment());
                            enchantMap.put(s, base);
                            if (s.contains("_")) {
                                enchantMap.put(s.replace("_", " "), base);
                                enchantMap.put(s.replace("_", ""), base);
                            }
                        }
                    }
            }
        }
    }

    public PEnchant getEnchant(String s) {
        return enchantMap.get(s);
    }
}
