package com.minecave.pickaxes.commands;

import com.minecave.pickaxes.menu.menus.MenuCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Timothy Andis
 */
public class PickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            MenuCreator.createMainPick((Player) commandSender);
        }
        return true;
    }
}
