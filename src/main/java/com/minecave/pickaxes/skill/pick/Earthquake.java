package com.minecave.pickaxes.skill.pick;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.drops.BlockValue;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.item.OreConversion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Timothy Andis
 */
public class Earthquake extends PSkill {

    private int radius;
    private Random             random           = new Random();
    private List<FallingBlock> fallingBlockList = new ArrayList<>();

    public Earthquake(int radius, String name, long cooldown, int level, int cost, String perm) {
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
        Location location = player.getLocation();
//        int playerX = location.getBlockX();
//        int playerZ = location.getBlockZ();
//        int y = location.getBlockY();
//
//        int curCount = 0;
//        int cap = ThreadLocalRandom.current().nextInt(30) + 1;

        List<Block> blocks = getRegionBlocks(player.getLocation(), radius / 2);

//        int curCount = 0;
//        int cap = ThreadLocalRandom.current().nextInt(radius * 2) + 1;
        for (Block block : blocks) {
            if (!this.wg.canBuild(event.getPlayer(), block)) {
                continue;
            }
//            if (curCount >= cap) {
//                break;
//            }
            if(ThreadLocalRandom.current().nextInt(10) >= 4) {
                if (block.getType() == Material.AIR ||
                    block.getType() == Material.BEDROCK ||
                    block.getType() == Material.ITEM_FRAME) {
                    continue;
                }
                Collection<ItemStack> items = block.getDrops(player.getItemInHand());
                ItemStack[] array = new ItemStack[items.size()];
                int i = 0;
                for(ItemStack stack : items) {
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
                Material type = block.getType();
//                if(block.getLocation().getBlockY() == location.getBlockY() - 1) {
                Block oneAbove = block.getLocation().clone().add(0, 1, 0).getBlock();
                    if(oneAbove.getType() == Material.AIR ||
                            !oneAbove.getType().isSolid()) {
                        FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(block.getLocation(), type, block.getData());
                        fallingBlock.setVelocity(new Vector(0, (random.nextInt(4) * Math.random() + 0.2), 0));
                        fallingBlock.setDropItem(false);
                        fallingBlock.setCustomName("earthquake");
                        fallingBlockList.add(fallingBlock);
                    }
//                }
                //not sure if they want this
                block.setType(Material.AIR);
            }
        }

//        for (int x = playerX - radius; x <= (playerX + radius); x++) {
//            if(curCount >= cap) {
//                break;
//            }
//            for (int z = playerZ - radius; z <= (playerZ + radius); z++) {
//                if(curCount >= cap) {
//                    break;
//                }
//                Location loc = new Location(location.getWorld(), x, y - 1, z);
//                if(ThreadLocalRandom.current().nextInt(10) < 5) {
//                    continue;
//                }
//                if (!this.wg.canBuild(event.getPlayer(), loc)) {
//                    continue;
//                }
//                if (loc.getBlock().getType() == Material.AIR || loc.getBlock().getType() == Material.BEDROCK) {
//                    continue;
//                }
//                Collection<ItemStack> items = loc.getBlock().getDrops(player.getItemInHand());
//                ItemStack[] array = new ItemStack[items.size()];
//                int i = 0;
//                for(ItemStack stack : items) {
//                    if (OreConversion.canConvert(stack.getType())
//                        || OreConversion.canConvert(loc.getBlock().getType())
//                        || OreConversion.isItem(stack.getType())) {
//                        Material converted = OreConversion.convertToItem(stack.getType());
//                        stack.setType(converted);
//                        if(pItem.hasEnchant("LOOT_BONUS_BLOCKS")) {
//                            int extra = Earthquake.super.itemsDropped(pItem.getEnchant("LOOT_BONUS_BLOCKS").getLevel());
//                            stack.setAmount(stack.getAmount() + extra);
//                        }
//                    }
//                    array[i++] = stack;
//                }
//                int xp = BlockValue.getXp(loc.getBlock());
//                pItem.incrementXp(xp, player);
//                pItem.addBlockBroken();
//                pItem.update(player);
//                player.getInventory().addItem(array);
//                player.updateInventory();
//                Material type = loc.getBlock().getType();
//                FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(loc.clone().add(0, 1, 0), type, loc.getBlock().getData());
//                fallingBlock.setVelocity(new Vector(0, (random.nextInt(3) * Math.random() + 0.2), 0));
//                fallingBlock.setDropItem(false);
//                fallingBlock.setCustomName("earthquake");
//                fallingBlockList.add(fallingBlock);
//                //not sure if they want this
//                loc.getBlock().setType(Material.AIR);
//                curCount++;
//            }
//        }
        this.add(player);
    }

    public ArrayList<Block> getRegionBlocks(Location loc1, double radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        for(int y = -1; y >= -3; y--) {
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

    public List<FallingBlock> getFallingBlockList() {
        return this.fallingBlockList;
    }
}
