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
        if(commandSender instanceof Player) {

        } else {
            //ppoints give player 10
            if (strings.length != 4) {
                return false;
            } else {
                if (strings[1].equalsIgnoreCase("give") || strings[1].equalsIgnoreCase("g")) {
                    Player player = Bukkit.getPlayer(strings[2]);
                    if(player == null || !player.isOnline()) {
                        commandSender.sendMessage(ChatColor.RED + strings[1] + " is not a valid player.");
                    } else {

                        PlayerInfo info = EnhancedPicks.getInstance().getPlayerManager().get(player);

                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}
