/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.commands;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.item.PItemSettings;
import com.minecave.pickaxes.item.PItemType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public class GiveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        EnhancedPicks plugin = EnhancedPicks.getInstance();
        if (strings.length < 2) {
            sender.sendMessage(ChatColor.RED + "Not enough arguments.");
            return false;
        }
        String typeStr = strings[0];
        String configStr = strings[1];
        if (!plugin.getPItemManager().getSettingsMap().containsKey(configStr)) {
            sender.sendMessage(ChatColor.RED + configStr + " does not exist in the config.");
            return true;
        }
        PItemType type;
        if (typeStr.equalsIgnoreCase("pick") || typeStr.equalsIgnoreCase("p")) {
            type = PItemType.PICK;
        } else if (typeStr.equalsIgnoreCase("sword") || typeStr.equalsIgnoreCase("s")) {
            type = PItemType.SWORD;
        } else {
            sender.sendMessage(ChatColor.RED + "Not a valid item type.");
            return true;
        }
        Collection<PItemSettings> settingsCollection = plugin.getPItemManager().getSettings(configStr);
        if(settingsCollection == null) {
            sender.sendMessage(ChatColor.RED + configStr + " does not exist in the config.");
            return true;
        }
        PItemSettings pItemSettings = null;
        for(PItemSettings ps : settingsCollection) {
            if(type == ps.getType()) {
                pItemSettings = ps;
            }
        }
        if(pItemSettings == null) {
            sender.sendMessage(ChatColor.RED + configStr + " does not exist in the config.");
            return true;
        }
        if (type != pItemSettings.getType()) {
            sender.sendMessage(configStr + " is not of type " + type.name());
            return true;
        }
        Player player = null;
        if (strings.length == 2) {
            if (!(sender instanceof Player)) {
                return true;
            }
            player = (Player) sender;

        } else if (strings.length >= 3) {
            String playerStr = strings[2];
            player = Bukkit.getPlayer(playerStr);
            if (player == null || !player.isOnline()) {
                sender.sendMessage(ChatColor.RED + playerStr + " is not online.");
                return true;
            }
        }
        if (player == null) {
            return true;
        }
        switch (type) {
            case PICK:
                PItem<BlockBreakEvent> pItem = pItemSettings.generate(BlockBreakEvent.class);
                ItemStack stack = pItem.getItem();
                pItem.updateManually(player, stack);
                for (PEnchant pEnchant : pItem.getEnchants()) {
                    pEnchant.apply(pItem);
                }
                Map<Integer, ItemStack> fail = player.getInventory().addItem(stack);
                if(fail != null && !fail.isEmpty()) {
                    final Player finalPlayer = player;
                    fail.values().forEach(i -> finalPlayer.getWorld().dropItem(finalPlayer.getLocation(), i));
                }
                plugin.getPItemManager().addPItemForce(pItem);
                break;
            case SWORD:
                PItem<EntityDamageByEntityEvent> eItem = pItemSettings.generate(EntityDamageByEntityEvent.class);
                stack = eItem.getItem();
                eItem.updateManually(player, stack);
                for (PEnchant pEnchant : eItem.getEnchants()) {
                    pEnchant.apply(eItem);
                }
                Map<Integer, ItemStack> fail1 = player.getInventory().addItem(stack);
                if(fail1 != null && !fail1.isEmpty()) {
                    final Player finalPlayer = player;
                    fail1.values().forEach(i -> finalPlayer.getWorld().dropItem(finalPlayer.getLocation(), i));
                }
                plugin.getPItemManager().addPItemForce(eItem);
                break;
        }
        return true;
    }
}
