package com.minecave.pickaxes.enchant;

import com.minecave.pickaxes.pitem.PItem;
import com.minecave.pickaxes.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Timothy Andis
 */
public abstract class PEnchant {

    private String name;
    private int level;
    private boolean inUse;

    public PEnchant(String name) {
        this.name = name;
        this.inUse = true;
        this.level = 1;
        name = ChatColor.stripColor(name);
        name = name.toLowerCase();
    }

    public abstract void activate(BlockBreakEvent event);

    public abstract void activate(EntityDamageByEntityEvent event);

    public abstract int getMaxLevel();

    public void apply(PItem item, Player player) {
        item.addEnchant(this, player);
    }

    @Override
    public String toString() {
        return ChatColor.YELLOW.toString() + name + " " + Utils.toRoman(level);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if(level > getMaxLevel()) {
            return;
        }
        this.level = level;
    }

    public void incrementLevel(Player player, PItem pItem) {
        this.setLevel(getLevel() + 1);
        pItem.update(player);
    }

}
