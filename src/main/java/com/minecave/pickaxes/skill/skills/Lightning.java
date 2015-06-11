package com.minecave.pickaxes.skill.skills;

import com.minecave.pickaxes.skill.Skill;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Timothy Andis
 */
public class Lightning extends Skill {

    private int depth, distance;

    public Lightning(String name, long cooldown, int level, int depth, int distance) {
        super(name, cooldown, level);
        this.depth = depth;
        this.distance = distance;
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        Set<Material> materials = new HashSet<>();
        Location target = player.getTargetBlock(materials, distance).getLocation();
        World world = location.getWorld();
        world.strikeLightningEffect(target);
        int targetBlockY = target.getBlockY();
        for(int y = targetBlockY; y < (targetBlockY + depth); y++) {
            Location loc = target.clone().subtract(0, y, 0);
            player.getInventory().addItem(loc.getBlock().getDrops().toArray(new ItemStack[loc.getBlock().getDrops().size()]));
            player.updateInventory();
        }
        this.add(player);
    }
}
