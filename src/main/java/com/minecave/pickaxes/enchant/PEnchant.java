package com.minecave.pickaxes.enchant;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.pitem.PItem;
import com.minecave.pickaxes.utils.Utils;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Timothy Andis
 */
public abstract class PEnchant {

    @Getter
    private String name;
    @Getter
    private int level = 0;
    @Getter
    private boolean inUse;
    @Getter
    protected int maxLevel = 10;
    @Getter
    private Map<Integer, Integer> costMap = new HashMap<>();

    public PEnchant(String name) {
        this.name = name;
        this.inUse = true;
        name = ChatColor.stripColor(name);
        name = name.toLowerCase();
    }

    public abstract void activate(BlockBreakEvent event);

    public abstract void activate(EntityDamageByEntityEvent event);

    public void apply(PItem item, Player player) {
        item.addEnchant(this, player);
    }

    public int getLevelCost(int level) {
        if (costMap.containsKey(level)) {
            return costMap.get(level);
        }
        return 1;
    }

    @Override
    public String toString() {
        return ChatColor.YELLOW.toString() + name + " " + Utils.toRoman(level);
    }

    public void setLevel(int level) {
        if (level > getMaxLevel()) {
            return;
        }
        this.level = level;
    }

    public void increaseLevel(Player player, PItem pItem) {
        if(level == maxLevel) {
            player.sendMessage(ChatColor.RED + "That enchantment is already at maxLevel.");
            return;
        }
        this.setLevel(getLevel() + 1);
        pItem.setPoints(pItem.getPoints() - getLevelCost(getLevelCost(level - 1)));
        pItem.update(player);
    }

    public void decreaseLevel(Player player, PItem pItem){
        if(level == 0) {
            player.sendMessage(ChatColor.RED + "That enchantment is already at level 0.");
            return;
        }
        this.setLevel(getLevel() - 1);
        pItem.setPoints(pItem.getPoints() + getLevelCost(getLevelCost(level + 1)));
        pItem.update(player);
    }

    public void loadConfig(String key) {
        FileConfiguration config = PickaxesRevamped.getInstance().getConfigValues().getEnchants();
        if (config.contains(key + ".maxLevel")) {
            maxLevel = config.getInt("tnt.maxLevel");
        }
        if (config.contains(key + ".levelCosts")) {
            List<Integer> list = config.getIntegerList(key + ".levelCosts");
            int index = 0;
            for (int i : list) {
                this.getCostMap().put(index++, i);
            }
        }
    }

    public abstract String getTrueName();

    public int getCost() {
        return getLevelCost(this.level);
    }

}
