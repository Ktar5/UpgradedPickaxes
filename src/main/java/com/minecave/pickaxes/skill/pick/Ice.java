package com.minecave.pickaxes.skill.pick;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.drops.BlockValue;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.item.OreConversion;
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
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Carter on 6/11/2015.
 */
public class Ice extends PSkill {

    private final int radius;

    public Ice(String name, long cooldown, int level, int cost, String perm, int radius) {
        super(name, cooldown, level, cost, perm);
        this.radius = radius;
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
        if (pItem == null) {
            return;
        }
        List<Block> blocks = getRegionBlocks(player.getLocation(), radius);
        List<Block> broken = new ArrayList<>();

//        int curCount = 0;
//        int cap = ThreadLocalRandom.current().nextInt(radius * 2) + 1;
        for (Block block : blocks) {
            if (!this.wg.canBuild(event.getPlayer(), block)) {
                continue;
            }
//            if (curCount >= cap) {
//                break;
//            }
            if(ThreadLocalRandom.current().nextInt(10) > 4) {
                Collection<ItemStack> items = block.getDrops(player.getItemInHand());
                ItemStack[] array = new ItemStack[items.size()];
                int i = 0;
                for (ItemStack stack : items) {
                    if (OreConversion.canConvert(stack.getType())
                        || OreConversion.canConvert(block.getType())
                        || OreConversion.isItem(stack.getType())) {
                        Material converted = OreConversion.convertToItem(stack.getType());
                        stack.setType(converted);
                        if(pItem.hasEnchant("LOOT_BONUS_BLOCKS")) {
                            int extra = itemsDropped(pItem.getEnchant("LOOT_BONUS_BLOCKS").getLevel());
                            Integer scale = EnhancedPicks.getInstance().getScaleFactors().get(stack.getType());
                            if(scale != null) {
                                extra *= scale;
                            }
                            if(!EnhancedPicks.getInstance().getGems().contains(stack.getType())){
                                extra = (int) Math.round(extra / 10D);
                                if(--extra < 0) {
                                    extra = 0;
                                }
                            }
                            stack.setAmount(stack.getAmount() + extra);
                        }
                    }
                    array[i++] = stack;
                }
                int xp = BlockValue.getXp(block);
                pItem.incrementXp(xp, player);
                pItem.addBlockBroken();
                pItem.update(player);
                Map<Integer, ItemStack> leftOvers = player.getInventory().addItem(array);
                if(!EnhancedPicks.getInstance().getConfig("scale_factor").get("delete_item_if_inv_full", Boolean.class, true)) {
                    leftOvers.values().forEach(it -> player.getWorld().dropItemNaturally(player.getLocation(), it));
                }
                player.updateInventory();
                block.setType(Material.ICE);
                broken.add(block);
//                curCount++;
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                player.playSound(event.getPlayer().getLocation(), Sound.GLASS, 3.0F, 2.0F);
                for (Block block : broken) {
                    if (!wg.canBuild(event.getPlayer(), block)) {
                        continue;
                    }
                    block.setType(Material.AIR);
                }
            }
        }.runTaskLater(EnhancedPicks.getInstance(), 50L);
        this.add(player);
    }

    public ArrayList<Block> getRegionBlocks(Location loc1, double radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        int d = (int) (radius / 2);
        if (d == 0) {
            d = 1;
        }
        for(int y = 0; y >= -d; y--) {
            for (double z = -radius; z <= radius; z++) {
                for (double x = -radius; x <= radius; x++) {
//                    if (ThreadLocalRandom.current().nextInt(10) > 4) {
                        if (Math.pow(x, 2) + Math.pow(z, 2) <= Math.pow(radius, 2)) {
                            Location l = loc1.clone().add(x, y, z);
                            if (!l.getChunk().isLoaded()) {
                                continue;
                            }
                            Block b = l.getBlock();
                            if (b.getType() != Material.AIR && b.getType().isBlock()) {
                                blocks.add(b);
                            }
                        }
//                    }
                }
            }
        }
        return blocks;
    }

    /*
    for(int x = -depth; x <= depth; x++) {
            for(int z = -depth; z <= depth; z++) {
                if(Math.pow(x, 2) + Math.pow(z, 2) <= Math.pow(depth, 2)) {
                    int d = depth / 2;
                    if(d == 0) {
                        d = 1;
                    }
                    for (int y = 0; y < d; y++) {
                        if (ThreadLocalRandom.current().nextInt(10) > 4) {
                            Location loc = location.clone().add(x, -y, z);
                            if (!this.wg.canBuild(event.getPlayer(), loc)) {
                                continue;
                            }
                            if (loc.getBlock().getType() == Material.AIR || loc.getBlock().getType() == Material.BEDROCK) {
                                continue;
                            }
                            Collection<ItemStack> items = loc.getBlock().getDrops(player.getItemInHand());
                            player.getInventory().addItem(items.toArray(new ItemStack[items.size()]));
                            loc.getBlock().setType(Material.AIR);
                            player.updateInventory();
                        }
                    }
                }
            }
        }
     */

}
