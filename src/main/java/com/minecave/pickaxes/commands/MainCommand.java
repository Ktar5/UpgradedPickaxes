package com.minecave.pickaxes.commands;

import com.tadahtech.pub.PickaxesRevamped;
import com.tadahtech.pub.menu.menus.MainMenu;
import com.tadahtech.pub.pitem.Pickaxe;
import com.tadahtech.pub.utils.Message;
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
        MainMenu menu = PickaxesRevamped.getInstance().getConfigValues().getMainMenu();
        menu.display((Player) commandSender);
        Player player = (Player) commandSender;
        Pickaxe pickaxe = Pickaxe.tryFromItem(player.getItemInHand());
        if(pickaxe == null) {
            Message.FAILURE.sendMessage(player, "You need to have a pickaze in your hand.");
            return true;
        }
        MainMenu.ITEMS.put(player.getUniqueId(), pickaxe);
        return true;
    }
}
