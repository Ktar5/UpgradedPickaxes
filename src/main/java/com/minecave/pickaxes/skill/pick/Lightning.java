package com.minecave.pickaxes.skill.pick;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.drops.BlockValue;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.item.OreConversion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
        ItemStack inhand = player.getItemInHand();
        PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
        if(pItem == null) {
            return;
        }
        Set<Material> materialSet = new HashSet<>();
        materialSet.add(Material.AIR);
        Block block = player.getTargetBlock(materialSet, distance);
        Location location = block.getLocation();
        System.out.println(location);
        World world = location.getWorld();
        world.strikeLightningEffect(location);
        player.sendMessage("location: " + location);
        int curCount = 0;
        for(int x = -depth; x <= depth; x++) {
            if(curCount >= depth * 2) {
                break;
            }
            for(int z = -depth; z <= depth; z++) {
                if(curCount >= depth * 2) {
                    break;
                }
                if(Math.pow(x, 2) + Math.pow(z, 2) <= Math.pow(depth, 2)) {
                    int d = depth / 2;
                    if(d == 0) {
                        d = 1;
                    }
                    for (int y = 0; y < d; y++) {
                        if(curCount >= 30) {
                            break;
                        }
                        if (ThreadLocalRandom.current().nextInt(10) > 4) {
                            Location loc = location.clone().add(x, -y, z);
                            if (!this.wg.canBuild(event.getPlayer(), loc)) {
                                continue;
                            }
                            if (loc.getBlock().getType() == Material.AIR || loc.getBlock().getType() == Material.BEDROCK) {
                                continue;
                            }
                            int xp = BlockValue.getXp(loc.getBlock());
                            pItem.incrementXp(xp, player);
                            pItem.addBlockBroken();
                            pItem.update(player);
                            Collection<ItemStack> items = loc.getBlock().getDrops(player.getItemInHand());
                            ItemStack[] array = new ItemStack[items.size()];
                            int i = 0;
                            for(ItemStack stack : items) {
                                stack.setType(OreConversion.convertToItem(stack.getType()));
                                array[i++] = stack;
                            }
                            player.getInventory().addItem(array);
                            loc.getBlock().setType(Material.AIR);
                            player.updateInventory();
                            curCount++;
                        }
                    }
                }
            }
        }
        this.add(player);
    }
}
