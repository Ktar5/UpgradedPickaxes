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
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DebugCommand implements CommandExecutor {

    private final EnhancedPicks plugin;

    public DebugCommand() {
        this.plugin = EnhancedPicks.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(!plugin.getDebugger().has(player))   {
                plugin.getDebugger().add(player);
                player.sendMessage("You turned ON debug mode.");
            } else {
                plugin.getDebugger().remove(player);
                player.sendMessage("You turned OFF debug mode.");
            }
        } else {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to see debug messages.");
        }
        return true;
    }
}
