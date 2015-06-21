package com.minecave.pickaxes.enchant.enchants;

import com.minecave.pickaxes.drops.BlockDrop;
import com.minecave.pickaxes.drops.MobDrop;
import com.minecave.pickaxes.enchant.PEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Timothy Andis
 */
public class LuckEnchant extends PEnchant {

    public LuckEnchant() {
        super("lucky", "Lucky Drop");
        super.loadConfig(getName());
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

    @Override
    public LuckEnchant cloneEnchant() {
        return new LuckEnchant();
    }
}
