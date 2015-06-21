package com.minecave.pickaxes.enchant.enchants;

import com.minecave.pickaxes.enchant.PEnchant;
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
        super("tnt", "TnT");
        super.loadConfig(getName());
    }

    @Override
    public void activate(BlockBreakEvent event) {
        if (this.getLevel() <= 0) {
            return;
        }
        Block block = event.getBlock();
        Location location = block.getLocation();
        World world = location.getWorld();
        world.createExplosion(location.getX(), location.getY(), location.getZ(), this.getLevel(), false, true);
    }

    @Override
    public void activate(EntityDamageByEntityEvent event) {
        if (this.getLevel() <= 0) {
            return;
        }
        Player player = (Player) event.getDamager();
        int radius = this.getLevel() * 2;
        player.getNearbyEntities(radius, radius, radius).stream()
                .filter(ent -> ent instanceof LivingEntity)
                .forEach(ent -> ((LivingEntity) ent).damage(0.5D * this.getLevel()));
    }

    @Override
    public TnTEnchant cloneEnchant() {
        return new TnTEnchant();
    }
}
