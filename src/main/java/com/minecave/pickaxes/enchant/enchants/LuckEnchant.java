package com.minecave.pickaxes.enchant.enchants;

import com.tadahtech.pub.drops.BlockDrop;
import com.tadahtech.pub.drops.MobDrop;
import com.tadahtech.pub.enchant.PEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Timothy Andis
 */
public class LuckEnchant extends PEnchant {

    public LuckEnchant() {
        super("LuckyDrop");
    }

    @Override
    public void activate(BlockBreakEvent event) {
        BlockDrop drop = BlockDrop.random(this.getLevel());
        drop.give(event.getPlayer());
    }

    @Override
    public void activate(EntityDamageByEntityEvent event) {
        Player player = (Player) event.getDamager();
        MobDrop drop = MobDrop.random(this.getLevel());
        drop.give(player);
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }
}
