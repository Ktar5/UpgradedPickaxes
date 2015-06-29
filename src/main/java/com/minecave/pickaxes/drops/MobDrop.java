package com.minecave.pickaxes.drops;

import com.minecave.pickaxes.EnhancedPicks;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Timothy Andis
 */
public class MobDrop extends Drop {

    public MobDrop(int weight, String item, int level) {
        super(weight, item, level);
    }

    public static MobDrop random(int level) {
        if(level > EnhancedPicks.getInstance().getDropManager().getMobDrops().size()) {
            level = EnhancedPicks.getInstance().getDropManager().getMobDrops().size();
        }
        return EnhancedPicks.getInstance().getDropManager().getMobDrops()
                .get(level == 1 ? 1 : ThreadLocalRandom.current().nextInt(level - 1) + 1);
    }

    @Override
    public void give(Player player) {
        if (!doGive(player)) {
            return;
        }
        MobDrop[] drops = EnhancedPicks.getInstance().getDropManager().getMobDrops()
                .toArray(new MobDrop[EnhancedPicks.getInstance().getDropManager().getMobDrops().size()]);
        int totalWeight = 0;
        for (MobDrop drop : drops) {
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
        MobDrop drop;
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
