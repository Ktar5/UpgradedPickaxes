/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.message;

import com.minecave.pickaxes.item.PItem;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Timothy Andis
 */
public class MessageBuilder {

    private String base = "";

    public MessageBuilder(String base) {
        this.base = base;
        if (this.base == null) {
            this.base = "";
        }
        this.base = ChatColor.translateAlternateColorCodes('&', base);
    }

    public MessageBuilder replace(Player player) {
        this.base = base.replace("$player$", player.getName());
        return this;
    }

    public MessageBuilder replace(PItem PItem) {
        if (PItem == null) {
            return this;
        }
        this.base = base.replace("$name$", PItem.getName());
        return this;
    }

    public MessageBuilder replace(int level, IntegerType type) {
        this.base = type.replace(base, level);
        return this;
    }

    public MessageBuilder replace(String level, IntegerType type) {
        this.base = type.replace(base, level);
        return this;
    }

    public String build() {
        return base;
    }

    public enum IntegerType {
        PLAYER_LEVEL("$newLevel$", "$currentLevel", "$level$"),
        XP("$currentXp$", "$xp$"),
        NEXT_XP("$nextXP$"),
        NEXT_LEVEL("$nextLevel$");

        private String[] all;

        IntegerType(String... all) {
            this.all = all;
        }

        public String replace(String s, int i) {
            if (s == null) {
                return null;
            }
            String string = s;
            for (String a : all) {
                if (s.contains(a)) {
                    string = string.replace(a, String.valueOf(i));
                }
            }
            return string;
        }

        public String replace(String s, String s1) {
            if(s == null) {
                return null;
            }
            String string = s;
            for(String a : all) {
                if(s.contains(a)) {
                    string = string.replace(a, s1);
                }
            }
            return string;
        }
    }
}
