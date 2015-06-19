package com.minecave.pickaxes.builder;

import com.minecave.pickaxes.pitem.PItem;
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
        if(PItem == null) {
            return this;
        }
        this.base = base.replace("$skill$", PItem.getName());
        return this;
    }

    public MessageBuilder replace(int level, IntegerType type) {
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

    }

}
