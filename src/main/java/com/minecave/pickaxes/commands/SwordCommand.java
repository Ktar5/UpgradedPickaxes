/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.commands;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.menu.menus.MainSwordMenu;
import com.minecave.pickaxes.pitem.Sword;
import com.minecave.pickaxes.utils.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SwordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            MainSwordMenu menu = PickaxesRevamped.getInstance().getConfigValues().getMainSwordMenu();
            Player player = (Player) commandSender;
            Sword sword = Sword.tryFromItem(player.getItemInHand());
            if (sword == null) {
                Message.FAILURE.sendMessage(player, "You need to have a sword in your hand.");
                return true;
            }
            menu.display((Player) commandSender);
        }
        return true;
    }
}
