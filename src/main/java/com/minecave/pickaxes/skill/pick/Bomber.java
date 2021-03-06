package com.minecave.pickaxes.skill.pick;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.drops.BlockValue;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.item.OreConversion;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Timothy Andis
 */
public class Bomber extends PSkill implements Listener {

    protected Random random = new Random();
    private int boomblocks;
    private int ticks;

    public Bomber(String name, long cooldown, int level, int cost, String perm, int boomblocks, int ticks, boolean seconds) {
        super(name, cooldown, level, cost, perm);
        this.boomblocks = boomblocks;
        this.ticks = seconds ? ticks * 20 : ticks;
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
        if (pItem == null) {
            return;
        }
        Location location = player.getEyeLocation();
        Vector dir = location.getDirection();
        Vector to = dir.multiply(Math.random() * 4);
        TNTPrimed tnt = location.getWorld().spawn(location.clone().add(0, 0.2, 0), TNTPrimed.class);
        tnt.setFuseTicks(ticks + 1);
        tnt.setVelocity(to);
        this.add(player);
//        int amount = random.nextInt(boomblocks / 2) + (boomblocks / 2);
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                float radius = tnt.getYield();
//                tnt.remove();
//                Location location = tnt.getLocation();
//                if (!wg.canBuild(event.getPlayer(), location.getBlock())) {
//                    return;
//                }
//
//                for (Block b : getRegionBlocks(location, radius)) {
//                    Location loc = b.getLocation();
//                    if (!wg.canBuild(event.getPlayer(), loc.getBlock()) ||
//                            loc.getBlock().getType() == Material.BEDROCK ||
//                            loc.getBlock().getType() == Material.AIR) {
//                        continue;
//                    }
//                    Collection<ItemStack> items = loc.getBlock().getDrops();
//                    ItemStack[] array = new ItemStack[items.size()];
//                    int i = 0;
//                    for (ItemStack stack : items) {
//                        if (OreConversion.canConvert(stack.getType())
//                            || OreConversion.canConvert(loc.getBlock().getType())
//                            || OreConversion.isItem(stack.getType())) {
//                            Material converted = OreConversion.convertToItem(stack.getType());
//                            stack.setType(converted);
//                            if(pItem.hasEnchant("LOOT_BONUS_BLOCKS")) {
//                                int extra = Bomber.super.itemsDropped(pItem.getEnchant("LOOT_BONUS_BLOCKS").getLevel());
//                                Integer scale = EnhancedPicks.getInstance().getScaleFactors().get(stack.getType());
//                                if(scale != null) {
//                                    extra *= scale;
//                                }
//                                if(!EnhancedPicks.getInstance().getGems().contains(stack.getType())){
//                                    extra = (int) Math.round(extra / 10D);
//                                    if(--extra < 0) {
//                                        extra = 0;
//                                    }
//                                }
//                                stack.setAmount(stack.getAmount() + extra);
//                            }
//                        }
//                        array[i++] = stack;
//                    }
//                    int xp = BlockValue.getXp(loc.getBlock());
//                    pItem.incrementXp(xp, player);
//                    pItem.addBlockBroken();
//                    pItem.update(player);
//                    Map<Integer, ItemStack> leftOvers = player.getInventory().addItem(array);
//                    if(!EnhancedPicks.getInstance().getConfig("scale_factor").get("delete_item_if_inv_full", Boolean.class, true)) {
//                        leftOvers.values().forEach(it -> player.getWorld().dropItemNaturally(player.getLocation(), it));
//                    }
//                    player.updateInventory();
//                    loc.getBlock().setType(Material.AIR);
//                }
//                player.playSound(location, Sound.EXPLODE, 1.0F, 1.0F);
//                player.playEffect(location, Effect.LARGE_SMOKE, 1);
//            }
//        }.runTaskLater(EnhancedPicks.getInstance(), ticks);
//        this.add(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onExplode(EntityExplodeEvent event) {
        if (event.getEntityType() == EntityType.PRIMED_TNT || event.getEntity() instanceof TNTPrimed) {
            if (!event.getEntity().hasMetadata("bomber")) {
                return;
            }
            Player player = Bukkit.getPlayer(UUID.fromString(event.getEntity().getMetadata("bomber").get(0).asString()));
            if (player != null && player.isOnline()) {
                PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
                if (pItem == null) {
                    event.setCancelled(true);
                    return;
                }
                for (Block sourceBlock : event.blockList()) {
                    for(int j = 0; j < 2; j++) {
                        if(j == 1 && ThreadLocalRandom.current().nextInt(10) >= 7) {
                            continue;
                        }
                        Block block = sourceBlock.getRelative(0, -j, 0);
                        if (!wg.canBuild(player, block) ||
                            block.getType() == Material.BEDROCK ||
                            block.getType() == Material.AIR ||
                                block.getType() == Material.ITEM_FRAME) {
                            continue;
                        }
                        Collection<ItemStack> items = block.getDrops();
                        ItemStack[] array = new ItemStack[items.size()];
                        int i = 0;
                        for (ItemStack stack : items) {
                            if (OreConversion.canConvert(stack.getType())
                                || OreConversion.canConvert(block.getType())
                                || OreConversion.isItem(stack.getType())) {
                                Material converted = OreConversion.convertToItem(stack.getType());
                                stack.setType(converted);
                                if (pItem.hasEnchant("LOOT_BONUS_BLOCKS")) {
                                    int extra = itemsDropped(pItem.getEnchant("LOOT_BONUS_BLOCKS").getLevel());
                                    extra *= 2;
                                    Integer scale = EnhancedPicks.getInstance().getScaleFactors().get(stack.getType());
                                    if (scale != null) {
                                        extra *= scale;
                                    }
                                    if (!EnhancedPicks.getInstance().getGems().contains(stack.getType())) {
                                    extra = (int) Math.round(extra / 10D);
                                        if (--extra < 0) {
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
                        if (!EnhancedPicks.getInstance().getConfig("scale_factor").get("delete_item_if_inv_full", Boolean.class, true)) {
                            leftOvers.values().forEach(it -> player.getWorld().dropItemNaturally(player.getLocation(), it));
                        }
                        player.updateInventory();
                        block.setType(Material.AIR);
                    }
                }
                player.playSound(event.getEntity().getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
                player.playEffect(event.getEntity().getLocation(), Effect.EXPLOSION_HUGE, 1);
                event.setCancelled(true);
            }
        }
    }

    public ArrayList<Block> getRegionBlocks(Location loc1, double radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        int d = (int) (radius / 1.5);
        if (d == 0) {
            d = 1;
        }
        for (double y = -d; y <= 0; y++)
            for (double x = -radius; x <= radius; x++) {
                for (double z = -radius; z <= radius; z++) {
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
                }
            }
        return blocks;
    }
}
