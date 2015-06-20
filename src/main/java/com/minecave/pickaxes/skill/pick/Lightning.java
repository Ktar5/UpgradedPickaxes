package com.minecave.pickaxes.skill.pick;

import com.minecave.pickaxes.skill.PSkill;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Timothy Andis
 */
public class Lightning extends PSkill {

    private int depth, distance;

    public Lightning(String name, long cooldown, int level, int cost, String perm, int depth, int distance) {
        super(name, cooldown, level, cost, perm);
        this.depth = depth;
        this.distance = distance;
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location location = player.getEyeLocation();
        Set<Material> materials = new HashSet<>();
        Location target = player.getTargetBlock(materials, distance).getLocation();
        World world = location.getWorld();
        world.strikeLightningEffect(target);
        int targetBlockY = target.getBlockY();
        for(int y = targetBlockY; y < (targetBlockY + depth); y++) {
            Location loc = target.clone().subtract(0, y, 0);
            if(!this.wg.canBuild(event.getPlayer(), loc)){
                continue;
            }
            List<ItemStack> drops = new ArrayList<>();
            drops.addAll(loc.getBlock().getDrops());
            for(int i = 0; i < drops.size(); i++) {
                ItemStack itemStack = drops.get(i);
                if(itemStack.getType() == Material.AIR ||
                        itemStack.getType() == Material.BEDROCK) {
                    drops.remove(i);
                } else {
                    player.getInventory().addItem(drops.toArray(new ItemStack[drops.size()]));
                }
            }
            loc.getBlock().setType(Material.AIR);
            player.updateInventory();
        }
        this.add(player);
    }
}
