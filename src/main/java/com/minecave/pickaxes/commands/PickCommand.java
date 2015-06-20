package com.minecave.pickaxes.commands;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.menu.menus.MainPickMenu;
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
            MainPickMenu menu = EnhancedPicks.getInstance().getMenuManager().get(MainPickMenu.class);
            menu.display((Player) commandSender);
        }
        return true;
    }
}
