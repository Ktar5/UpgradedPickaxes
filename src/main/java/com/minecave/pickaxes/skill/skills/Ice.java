package com.minecave.pickaxes.skill.skills;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.skill.Skill;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Carter on 6/11/2015.
 */
public class Ice extends Skill {

    private final int radius;

    public Ice(String name, long cooldown, int level, int radius) {
        super(name, cooldown, level);
        this.radius = radius;
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        List<Block> blocks = getRegionBlocks(player.getLocation(), radius);

        for(Block block : blocks){
            Collection<ItemStack> items = block.getDrops(player.getItemInHand());
            player.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
            player.updateInventory();
            block.setType(Material.ICE);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                player.playSound(event.getPlayer().getLocation(), Sound.GLASS, 3.0F, 2.0F);
                for(Block block : blocks){
                    block.setType(Material.AIR);
                }
            }
        }.runTaskLater(PickaxesRevamped.getInstance(), 50L);
        this.add(player);
    }

    public ArrayList<Block> getRegionBlocks( Location loc1, double radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        for(double x = -radius; x <= radius; x++) {
            for(double y = -radius; x <= radius; y++) {
                for(double z = -radius; x <= radius; z++) {
                    blocks.add(loc1.clone().add(x,y,z).getBlock());
                }
            }
        }
        return blocks;
    }

}
