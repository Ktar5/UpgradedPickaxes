package com.minecave.pickaxes.skill.skills;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.skill.Skill;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * @author Timothy Andis
 */
public class Earthquake extends Skill {

    private int radius;
    private  Random random = new Random();

    public Earthquake(int radius, String name, long cooldown, int level, int cost, String perm) {
        super(name, cooldown, level, cost, perm);
        this.radius = radius;
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        int playerX = location.getBlockX();
        int playerZ = location.getBlockZ();
        int y = location.getBlockY();
        for(int x = playerX - radius; x <= (playerX + radius); x++) {
            for(int z = playerZ - radius; z <= (playerZ + radius); z++) {
                Location block = new Location(location.getWorld(), x, y, z);
                Material type = block.getBlock().getType();
                FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(block, type, block.getBlock().getData());
                fallingBlock.setVelocity(new Vector(Math.random() * 4.1, (random.nextInt(3) * Math.random()), Math.random() * 4.1));
                fallingBlock.setDropItem(false);
                fallingBlock.setMetadata("custom", new FixedMetadataValue(PickaxesRevamped.getInstance(), ""));
                //not sure if they want this
                block.getBlock().setType(Material.AIR);
            }
        }
        this.add(player);
    }
}
