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
        super("luck", "Lucky Drop");
        super.loadConfig(getName());
    }

    @Override
    public void activate(BlockBreakEvent event) {
        if(this.getLevel() == 0) {
            return;
        }
        BlockDrop drop = BlockDrop.random(this.getLevel());
        if(drop != null) {
            drop.give(event.getPlayer());
        }
    }

    @Override
    public void activate(EntityDamageByEntityEvent event) {
        if(this.getLevel() == 0) {
            return;
        }
        Player player = (Player) event.getDamager();
        MobDrop drop = MobDrop.random(this.getLevel());
        drop.give(player);
    }

    public void activate(Player player) {
        if(this.getLevel() == 0) {
            return;
        }
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
