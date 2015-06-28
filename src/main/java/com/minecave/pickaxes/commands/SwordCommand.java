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
import com.minecave.pickaxes.menu.menus.MenuCreator;
import com.minecave.pickaxes.player.PlayerInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SwordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if(strings.length == 2) {
                if(strings[0].equalsIgnoreCase("points") ||
                        strings[0].equalsIgnoreCase("p")) {
                    if(!PointsCommand.isInteger(strings[1])) {
                        commandSender.sendMessage("Points must be an integer.");
                        return false;
                    } else {
                        Player player = (Player) commandSender;
                        PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
                        PlayerInfo info = EnhancedPicks.getInstance().getPlayerManager().get(player);
                        int points = Integer.parseInt(strings[1]);
                        if(pItem == null) {
                            player.sendMessage("You must have an enhanced pick/sword in your hand.");
                            return true;
                        }
                        if(info.subtractPoints(points)) {
                            pItem.addPoints(points);
                            player.sendMessage(ChatColor.GOLD + "You added " + ChatColor.WHITE + points + " to your sword.");
                            player.sendMessage(ChatColor.GOLD + "Your sword's current points: " + ChatColor.WHITE + pItem.getPoints());
                        } else {
                            player.sendMessage(ChatColor.RED + "You can't spend points you don't have.");
                        }
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                MenuCreator.createMainSword((Player) commandSender);
            }
        }
        return true;
    }
}
