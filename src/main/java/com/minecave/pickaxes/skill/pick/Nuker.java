/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.skill.pick;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.drops.BlockValue;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.item.OreConversion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public class Nuker extends PSkill {

    public Nuker(String name, int level, int cost, String perm) {
        super(name, 0, level, cost, perm);
    }

    @Override
    protected void use(PlayerInteractEvent event) {

    }

    @Override
    public void onBreak(BlockBreakEvent event) {
        EnhancedPicks plugin = EnhancedPicks.getInstance();
        Player player = event.getPlayer();
        PItem<?> pItem = plugin.getPItemManager().getPItem(player.getItemInHand());
        if (pItem == null) {
            return;
        }
        if (!pItem.getCurrentSkill().equals(this)) {
            return;
        }
        String[] dir = getCardinalDirection(player);
        for (String s : pItem.getNukerBlocks()) {
            String[] dat = s.split("\\|");
            int x = Integer.parseInt(dat[0]);
            int y = Integer.parseInt(dat[1]);
            int z = Integer.parseInt(dat[2]);
            Block targetBlock;

            switch (dir[1]) {
                case "N":
                    if (dir[0].equals("V")) {
                        targetBlock = event.getBlock().getRelative(-x, y, -z);
                    } else {
                        targetBlock = event.getBlock().getRelative(-y, x, -z);
                    }
                    break;
                case "E":
                    if (dir[0].equals("V")) {
                        targetBlock = event.getBlock().getRelative(z, y, -x);
                    } else {
                        targetBlock = event.getBlock().getRelative(-z, x, -y);
                    }
                    break;
                case "S":
                    if (dir[0].equals("V")) {
                        targetBlock = event.getBlock().getRelative(x, y, z);
                    } else {
                        targetBlock = event.getBlock().getRelative(-y, x, z);
                    }
                    break;
                default:
                    if (dir[0].equals("V")) {
                        targetBlock = event.getBlock().getRelative(-z, y, x);
                    } else {
                        targetBlock = event.getBlock().getRelative(-z, x, y);
                    }
                    break;
            }
            Location loc = targetBlock.getLocation();
            if (!this.wg.canBuild(event.getPlayer(), loc.getBlock())) {
                continue;
            }
//            loc.getBlock().setType(Material.GOLD_BLOCK);
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
            for (ItemStack stack : items) {
                if (OreConversion.canConvert(stack.getType())
                    || OreConversion.canConvert(loc.getBlock().getType())
                    || OreConversion.isItem(stack.getType())) {
                    Material converted = OreConversion.convertToItem(stack.getType());
                    stack.setType(converted);
                    if (pItem.hasEnchant("LOOT_BONUS_BLOCKS")) {
                        int extra = Nuker.super.itemsDropped(pItem.getEnchant("LOOT_BONUS_BLOCKS").getLevel());
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
            Map<Integer, ItemStack> leftOvers = player.getInventory().addItem(array);
            if(!plugin.getConfig("scale_factor").get("delete_item_if_inv_full", Boolean.class, true)) {
                leftOvers.values().forEach(it -> player.getWorld().dropItemNaturally(player.getLocation(), it));
            }
            loc.getBlock().setType(Material.AIR);
            player.updateInventory();
        }

    }

    public static String[] getCardinalDirection(Player player) {

        double pitch = (player.getLocation().getPitch());
        String[] ret = new String[2];
        ret[0] = "Q";
        if (pitch > (25) || pitch < (-25)) ret[0] = "V";

        double rotation = (player.getLocation().getYaw() - 90) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (0 <= rotation && rotation < 22.5) {
            ret[1] = "N";
        } else if (22.5 <= rotation && rotation < 112.5) {
            ret[1] = "E";
        } else if (112.5 <= rotation && rotation < 202.5) {
            ret[1] = "S";
        } else if (202.5 <= rotation && rotation < 292.5) {
            ret[1] = "W";
        } else if (292.5 <= rotation && rotation < 360) {
            ret[1] = "N";
        } else {
            ret[1] = null;
        }
        return ret;
    }
}
