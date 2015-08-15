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
import com.minecave.pickaxes.player.PlayerInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PointsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        //ppoints give player 10
        if (strings.length != 2) {
            return false;
        } else {
            Player player = Bukkit.getPlayer(strings[0]);
            if (player == null || !player.isOnline()) {
                commandSender.sendMessage(ChatColor.RED + strings[0] + " is not a valid player.");
                return true;
            } else {
                if (!isInteger(strings[1])) {
                    commandSender.sendMessage("The points must be an integer.");
                    return true;
                }
                PlayerInfo info = EnhancedPicks.getInstance().getPlayerManager().add(player);
                int points = Integer.parseInt(strings[1]);
                info.addPoints(points);
                info.getPlayer().sendMessage(ChatColor.GREEN + "You received " + points + " points to spend on your picks/swords.");
                commandSender.sendMessage("You gave " + info.getPlayer().getName() + " " + points + " points.");
                return true;
            }
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }
}
