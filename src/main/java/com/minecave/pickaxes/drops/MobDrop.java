package com.minecave.pickaxes.drops;

import com.minecave.pickaxes.EnhancedPicks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Timothy Andis
 */
public class MobDrop extends Drop {

    public MobDrop(int weight, String command, int level) {
        super(weight, command, level);
    }

    @Override
    public void give(Player player) {
        if(!doGive(player)) {
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
        return EnhancedPicks.getInstance().getDropManager().getMobDrops().get(0);
    }

}
