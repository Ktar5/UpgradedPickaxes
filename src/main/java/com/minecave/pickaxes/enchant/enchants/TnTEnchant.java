package com.minecave.pickaxes.enchant.enchants;

import com.tadahtech.pub.enchant.PEnchant;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * @author Timothy Andis
 */
public class TnTEnchant extends PEnchant {

    public TnTEnchant() {
        super("TnT");
    }

    @Override
    public void activate(BlockBreakEvent event) {
        Block block = event.getBlock();
        Location location = block.getLocation();
        World world = location.getWorld();
        world.createExplosion(location.getX(), location.getY(), location.getZ(), this.getLevel(), false, true);
    }

    @Override
    public void activate(EntityDamageByEntityEvent event) {
        Player player = (Player) event.getDamager();
        int radius = this.getLevel() * 2;
        player.getNearbyEntities(radius, radius, radius).stream()
          .filter(ent -> ent instanceof LivingEntity)
          .forEach(ent -> ((LivingEntity) ent).damage(0.5D * this.getLevel()));
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }
}
