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
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.item.PItemManager;
import com.minecave.pickaxes.item.PItemType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class GiveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        EnhancedPicks plugin = EnhancedPicks.getInstance();
        if(strings.length < 2) {
            return false;
        }
        String typeStr = strings[0];
        String configStr = strings[1];
        if(!plugin.getPItemManager().getSettingsMap().containsKey(configStr)) {
            sender.sendMessage(ChatColor.RED + configStr + " does not exist in the config.");
            return true;
        }
        PItemType type;
        if (typeStr.equalsIgnoreCase("pick") || typeStr.equalsIgnoreCase("p")) {
            type = PItemType.PICK;
        } else if (typeStr.equalsIgnoreCase("sword") || typeStr.equalsIgnoreCase("s")) {
            type = PItemType.SWORD;
        } else {
            return false;
        }
        PItemManager.PItemSettings pItemSettings = plugin.getPItemManager().getSettingsMap().get(configStr);
        if(type != pItemSettings.getType()) {
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
            if(player == null || !player.isOnline()) {
                sender.sendMessage(ChatColor.RED + playerStr + " is not online.");
                return true;
            }
        }
        if(player == null) {
            return true;
        }
        switch(type) {
            case PICK:
                PItem<BlockBreakEvent> pItem = pItemSettings.generate(BlockBreakEvent.class);
                player.getInventory().addItem(pItem.getItem());
                pItem.update(player);
                plugin.getPItemManager().addPItem(pItem);
                break;
            case SWORD:
                PItem<EntityDamageByEntityEvent> eItem = pItemSettings.generate(EntityDamageByEntityEvent.class);
                player.getInventory().addItem(eItem.getItem());
                eItem.update(player);
                plugin.getPItemManager().addPItem(eItem);
                break;
        }
        return true;
    }
}