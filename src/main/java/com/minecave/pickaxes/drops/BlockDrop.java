package com.minecave.pickaxes.drops;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Timothy Andis
 */
public class BlockDrop extends Drop {

    private static List<BlockDrop> blockDrops = new ArrayList<>();

    public BlockDrop(int weight, String command, int level) {
        super(weight, command, level);
        blockDrops.add(this);
    }

    @Override
    public void give(Player player) {
        if(!doGive(player)) {
            return;
        }
        BlockDrop[] drops = blockDrops.toArray(new BlockDrop[blockDrops.size()]);
        int totalWeight = 0;
        for (BlockDrop drop : drops) {
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
        BlockDrop drop;
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

    public static BlockDrop random(int level) {
        return blockDrops.get(0);
    }
    
}
