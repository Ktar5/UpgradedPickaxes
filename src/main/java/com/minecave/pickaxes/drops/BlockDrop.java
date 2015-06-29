package com.minecave.pickaxes.drops;

import com.minecave.pickaxes.EnhancedPicks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Timothy Andis
 */
public class BlockDrop extends Drop {

    public BlockDrop(int weight, String item, int level) {
        super(weight, item, level);
    }

    public static BlockDrop random(int level) {
        if(level > EnhancedPicks.getInstance().getDropManager().getBlockDrops().size()) {
            level = EnhancedPicks.getInstance().getDropManager().getBlockDrops().size();
        }
        return EnhancedPicks.getInstance().getDropManager().getBlockDrops()
                .get(level == 1 ? 1 : ThreadLocalRandom.current().nextInt(level - 1) + 1);
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
        Material mat = Material.valueOf(drop.item.toUpperCase());
        player.getInventory().addItem(new ItemStack(mat, 1));
        player.updateInventory();
    }
}
