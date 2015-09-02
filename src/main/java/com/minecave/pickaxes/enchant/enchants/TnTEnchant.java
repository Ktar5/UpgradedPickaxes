package com.minecave.pickaxes.enchant.enchants;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.drops.BlockValue;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.item.OreConversion;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Timothy Andis
 */
public class TnTEnchant extends PEnchant {

    private WorldGuardPlugin wg;

    public TnTEnchant() {
        super("tnt", "TnT");
        super.loadConfig(getName());
        wg = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
    }

    @Override
    public void activate(BlockBreakEvent event) {
        if (this.getLevel() <= 0) {
            return;
        }
        Player player = event.getPlayer();
        PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
        if (pItem == null) {
            return;
        }
        Block block = event.getBlock();
        Location location = block.getLocation();
        int rad = 1;
        if (this.getLevel() >= 1 && this.getLevel() <= 3) {
            rad = 1;
        } else if (this.getLevel() > 3 && this.getLevel() <= 6) {
            rad = 2;
        } else {
            rad = 3;
        }
        int curCount = this.getLevel() * 2;
        boolean broken = false;
        for (Block b : getRegionBlocks(location, rad * (Math.random() * 2))) {
            if (b.equals(location.getBlock())) {
                continue;
            }
            if (ThreadLocalRandom.current().nextBoolean()) {
                continue;
            }
            if (curCount <= 0) {
                break;
            }
            Location loc = b.getLocation();
            if (!wg.canBuild(event.getPlayer(), loc.getBlock()) ||
                loc.getBlock().getType() == Material.BEDROCK ||
                loc.getBlock().getType() == Material.AIR) {
                continue;
            }
            Collection<ItemStack> items = loc.getBlock().getDrops();
            ItemStack[] array = new ItemStack[items.size()];
            int i = 0;
            for (ItemStack stack : items) {
                if (OreConversion.canConvert(stack.getType())
                    || OreConversion.canConvert(loc.getBlock().getType())
                    || OreConversion.isItem(stack.getType())) {
                    Material converted = OreConversion.convertToItem(stack.getType());
                    stack.setType(converted);
                    if (pItem.hasEnchant("LOOT_BONUS_BLOCKS")) {
                        int extra = PSkill.itemsDropped(pItem.getEnchant("LOOT_BONUS_BLOCKS").getLevel());
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
            int xp = BlockValue.getXp(loc.getBlock());
            pItem.addBlockBroken();
            pItem.incrementXp(xp, player);
            pItem.updateManually(player, player.getItemInHand());
            Map<Integer, ItemStack> leftOvers = player.getInventory().addItem(array);
            if (!EnhancedPicks.getInstance().getConfig("scale_factor").get("delete_item_if_inv_full", Boolean.class, true)) {
                leftOvers.values().forEach(it -> player.getWorld().dropItemNaturally(player.getLocation(), it));
            }
            player.updateInventory();
            b.setType(Material.AIR);
            if (ThreadLocalRandom.current().nextBoolean() && !broken) {
                broken = true;
                player.playSound(b.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
                player.playEffect(b.getLocation(), Effect.EXPLOSION_LARGE, 0);
            }
            curCount--;
        }
    }

    @Override
    public void activate(EntityDamageByEntityEvent event) {
        if (this.getLevel() <= 0) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity &&
            ((LivingEntity) event.getEntity()).getHealth() <= 0) {
            return;
        }
        Player player = (Player) event.getDamager();
        int radius = this.getLevel() * 2;
        for (Entity ent : player.getNearbyEntities(radius, radius, radius)) {
            if (ent.equals(event.getEntity())) {
                continue;
            }
            if (ent instanceof LivingEntity && !(ent instanceof Player)) {
                ent.removeMetadata("pitemplayer", EnhancedPicks.getInstance());
                ent.setMetadata("pitemplayer", new FixedMetadataValue(EnhancedPicks.getInstance(), player.getUniqueId().toString()));
                ent.setMetadata("skipTNT", new FixedMetadataValue(EnhancedPicks.getInstance(), true));
                ((LivingEntity) ent).damage(0.5D * this.getLevel(), player);
                Bukkit.getScheduler().runTaskLater(EnhancedPicks.getInstance(), () -> {
                    if (ent.hasMetadata("skipTNT")) {
                        ent.removeMetadata("skipTNT", EnhancedPicks.getInstance());
                    }
                }, 20l);
            }
        }
    }

    @Override
    public TnTEnchant cloneEnchant() {
        TnTEnchant tnt = new TnTEnchant();
        tnt.setLevel(this.getLevel());
        tnt.setStartLevel(this.getStartLevel());
        tnt.setMaxLevel(this.getMaxLevel());
        return tnt;
    }

    public ArrayList<Block> getRegionBlocks(Location loc1, double radius) {
        int neg = 1;
        if (ThreadLocalRandom.current().nextBoolean()) {
            neg = -1;
        }
        ArrayList<Block> blocks = new ArrayList<>();
        while (blocks.size() != this.getLevel() * 5 && radius <= radius + 4) {
            if (neg == -1) {
                for (double x = 0; x >= radius * neg; x--) {
                    for (double z = 0; z >= radius * neg; z--) {
                        for (double y = 0 / 2; y >= -(radius / 2); y--) {
                            if (blocks.size() == this.getLevel() * 5) {
                                return blocks;
                            }
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
            } else {
                for (double x = 0; x <= radius; x++) {
                    for (double z = 0; z <= radius; z++) {
                        for (double y = 0; y >= -(radius / 2); y--) {
                            if (blocks.size() == this.getLevel() * 5) {
                                return blocks;
                            }
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
            }
            radius += 2;
        }
        return blocks;
    }
}
