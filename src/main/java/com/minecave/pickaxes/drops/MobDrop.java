package com.minecave.pickaxes.drops;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Timothy Andis
 */
public class MobDrop extends Drop {

    private static List<MobDrop> mobDrops = new ArrayList<>();

    public MobDrop(int weight, String command, int level) {
        super(weight, command, level);
        mobDrops.add(this);
    }

    @Override
    public void give(Player player) {
        if(!doGive(player)) {
            return;
        }
        MobDrop[] drops = mobDrops.toArray(new MobDrop[mobDrops.size()]);
        int totalWeight = 0;
        for (MobDrop drop : drops) {
            totalWeight += drop.weight;
        }
        int index = -1;
        double random = Math.random() * totalWeight;
        for(int i = 0; i < drops.length; ++i) {
            random -= drops[i].weight;
            if(random <= 0) {
                index = i;
            }
        }
        MobDrop drop;
        try {
            drop = drops[index];
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        if(drop == null) {
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), drop.command);
    }

    public static MobDrop random(int level) {
        return mobDrops.get(0);
    }

    //Block Drops
    //Add enchants / listeners
    //PlayerInfo shit
    //SQLize this shit
    //Commands
    //Skills
    //GUIs
}
