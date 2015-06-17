package com.minecave.pickaxes.commands;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.menu.menus.MainPickMenu;
import com.minecave.pickaxes.pitem.Pickaxe;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Timothy Andis
 */
public class MainCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            MainPickMenu menu = PickaxesRevamped.getInstance().getConfigValues().getMainPickMenu();
            Player player = (Player) commandSender;
            Pickaxe pickaxe = Pickaxe.tryFromItem(player.getItemInHand());
//            if (pickaxe == null) {
//                Message.FAILURE.sendMessage(player, "You need to have a pickaxe in your hand.");
//                return true;
//            }
            menu.display((Player) commandSender);
        }
        return true;
    }
}
