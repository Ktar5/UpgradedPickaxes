package com.minecave.pickaxes.enchant.enchants;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.drops.BlockValue;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.util.item.OreConversion;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
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
        for (Block b : getRegionBlocks(location, rad * (Math.random() * 2))) {
            if (curCount <= 0) {
                break;
            }
            Location loc = b.getLocation();
            if (!wg.canBuild(event.getPlayer(), loc) ||
                    loc.getBlock().getType() == Material.BEDROCK ||
                    loc.getBlock().getType() == Material.AIR) {
                continue;
            }
            Collection<ItemStack> items = loc.getBlock().getDrops();
            ItemStack[] array = new ItemStack[items.size()];
            int i = 0;
            for (ItemStack stack : items) {
                stack.setType(OreConversion.convertToItem(stack.getType()));
                array[i++] = stack;
            }
            int xp = BlockValue.getXp(loc.getBlock());
            pItem.incrementXp(xp, player);
            pItem.addBlockBroken();
            pItem.update(player);
            player.getInventory().addItem(array);
            player.updateInventory();
            loc.getBlock().setType(Material.AIR);
            if (ThreadLocalRandom.current().nextBoolean()) {
                player.playSound(b.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
                player.playEffect(b.getLocation().add(0, 1, 0), Effect.EXPLOSION, 0);
            }
            curCount--;
        }
    }

    @Override
    public void activate(EntityDamageByEntityEvent event) {
        if (this.getLevel() <= 0) {
            return;
        }
        Player player = (Player) event.getDamager();
        int radius = this.getLevel() * 2;
        player.getNearbyEntities(radius, radius, radius).stream()
                .filter(ent -> ent instanceof LivingEntity && !(ent instanceof Player))
                .forEach(ent -> ((LivingEntity) ent).damage(0.5D * this.getLevel()));
    }

    @Override
    public TnTEnchant cloneEnchant() {
        TnTEnchant tnt = new TnTEnchant();
        tnt.setLevel(this.getLevel());
        tnt.setMaxLevel(this.getMaxLevel());
        return tnt;
    }

    public ArrayList<Block> getRegionBlocks(Location loc1, double radius) {
        ArrayList<Block> blocks = new ArrayList<>();
        for (double x = 0; x <= radius; x++) {
            for (double z = 0; z <= radius; z++) {
                for (double y = 0 / 2; y <= radius / 2; y++) {
                    if (blocks.size() == this.getLevel()) {
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
        return blocks;
    }
}
