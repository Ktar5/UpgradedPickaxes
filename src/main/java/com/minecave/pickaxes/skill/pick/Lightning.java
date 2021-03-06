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
import java.util.Map;
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
        if (pItem == null) {
            return;
        }
        Set<Material> materialSet = new HashSet<>();
        materialSet.add(Material.AIR);
        Block block = player.getTargetBlock(materialSet, 20);
        Location location = block.getLocation();
        World world = location.getWorld();
        world.strikeLightningEffect(location);

        for (int y = 0; y >= -depth; y--) {
            for (int x = -(distance / 2) - 2; x < distance / 2 + 2; x++) {
                for (int z = -(distance / 2) - 2; z < distance / 2 + 2; z++) {
                    if (Math.pow(x, 2) + Math.pow(z, 2) <= Math.pow((distance + 2), 2)) {
                        if (ThreadLocalRandom.current().nextDouble(10) >= 3.4) {
                            Location loc = location.clone().add(x, y, z);
                            if (!this.wg.canBuild(event.getPlayer(), loc.getBlock())) {
                                continue;
                            }
                            if (loc.getBlock().getType() == Material.AIR ||
                                loc.getBlock().getType() == Material.BEDROCK ||
                                block.getType() == Material.ITEM_FRAME) {
                                continue;
                            }
                            int xp = BlockValue.getXp(loc.getBlock());
                            pItem.incrementXp(xp, player);
                            pItem.addBlockBroken();
                            pItem.update(player);
                            Collection<ItemStack> items = loc.getBlock().getDrops(player.getItemInHand());
                            ItemStack[] array = new ItemStack[items.size()];
                            int i = 0;
                            for (ItemStack stack : items) {
                                if (OreConversion.canConvert(stack.getType()) ||
                                    OreConversion.canConvert(loc.getBlock().getType()) ||
                                    OreConversion.isItem(stack.getType())) {
                                    Material converted = OreConversion.convertToItem(stack.getType());
                                    stack.setType(converted);
                                    if (pItem.hasEnchant("LOOT_BONUS_BLOCKS")) {
                                        int extra = itemsDropped(pItem.getEnchant("LOOT_BONUS_BLOCKS").getLevel());
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
                            Map<Integer, ItemStack> leftOvers = player.getInventory().addItem(array);
                            if (!EnhancedPicks.getInstance().getConfig("scale_factor").get("delete_item_if_inv_full", Boolean.class, true)) {
                                leftOvers.values().forEach(it -> player.getWorld().dropItemNaturally(player.getLocation(), it));
                            }
                            loc.getBlock().setType(Material.AIR);
                            player.updateInventory();
                        }
                    }
                }
            }
        }

        this.add(player);
    }
}
