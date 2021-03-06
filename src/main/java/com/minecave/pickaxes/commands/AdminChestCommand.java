/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.commands;

import com.minecave.pickaxes.menu.menus.MenuCreator;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminChestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            if(strings.length != 1) {
                return false;
            }
            Player admin = (Player) commandSender;
            Player target = Bukkit.getPlayer(strings[0]);
            if(target == null || !target.isOnline()) {
                admin.sendMessage(ChatColor.RED + "Target player " + strings[0] + " isn't online.");
                return true;
            }
            MenuCreator.createAdminMenu(admin, target);
        }
        return true;
    }
}
