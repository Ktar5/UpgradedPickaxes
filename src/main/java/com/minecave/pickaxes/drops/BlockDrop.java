package com.minecave.pickaxes.drops;

import com.minecave.pickaxes.EnhancedPicks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Timothy Andis
 */
public class BlockDrop extends Drop {

    public BlockDrop(int weight, String command, int level) {
        super(weight, command, level);
    }

    public static BlockDrop random(int level) {
        return EnhancedPicks.getInstance().getDropManager().getBlockDrops().get(0);
    }

    @Override
    public void give(Player player) {
        if (!doGive(player)) {
            return;
        }
        BlockDrop[] drops = EnhancedPicks.getInstance().getDropManager().getBlockDrops()
                .toArray(new BlockDrop[EnhancedPicks.getInstance().getDropManager().getBlockDrops().size()]);
        int totalWeight = 0;
        for (BlockDrop drop : drops) {
            totalWeight += drop.weight;
        }
        int index = -1;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < drops.length; ++i) {
            random -= drops[i].weight;
            if (random <= 0) {
                index = i;
            }
        }
        BlockDrop drop;
        try {
            drop = drops[index];
        } catch (IndexOutOfBoundsException e) {
            return;
        }
        if (drop == null) {
            return;
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), drop.command);
    }

}
