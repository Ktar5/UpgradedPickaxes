package com.minecave.pickaxes.commands;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.items.ItemBuilder;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.skill.Skill;
import com.minecave.pickaxes.sql.PlayerInfo;
import com.minecave.pickaxes.utils.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Timothy Andis
 */
public class PickaxeCommand  implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            switch(args.length) {
                case 0:
                    Player player = (Player) sender;
                    PlayerInfo info = PlayerInfo.get(player);
                    if(info == null) {
                        Message.FAILURE.sendMessage(player, "You dont have any info!");
                        return true;
                    }

                    Skill skill = PickaxesRevamped.getInstance().getConfigValues().getEarthquake();
                    ItemBuilder builder = ItemBuilder.wrap(new ItemStack(Material.DIAMOND_PICKAXE));
                    String name = ChatColor.AQUA + player.getName() + "'s Diamond Pickaxe: Level: 1 XP: 0 Blocks: 0";
                    builder.name(name);

                    Pickaxe pickaxe = new Pickaxe(builder.build(), Level.ONE, 0, name, skill);
                    pickaxe.getEnchant("luck").incrementLevel(player, pickaxe);
                    player.getInventory().addItem(pickaxe.getItemStack());
                    pickaxe.update(player);
                    break;
                case 1:
                    if(Bukkit.getPlayer(args[0]) == null) {
                        return true;
                    }
                    Player other = Bukkit.getPlayer(args[0]);
                    player = (Player) sender;
                    info = PlayerInfo.get(player);
                    if(info == null) {
                        Message.FAILURE.sendMessage(player, "You dont have any info!");
                        return true;
                    }

                    skill = PickaxesRevamped.getInstance().getConfigValues().getEarthquake();
                    builder = ItemBuilder.wrap(new ItemStack(Material.DIAMOND_PICKAXE));
                    name = ChatColor.AQUA + player.getName() + "'s Diamond Pickaxe: Level: 1 XP: 0 Blocks: 0";
                    builder.name(name);

                    pickaxe = new Pickaxe(builder.build(), Level.ONE, 0, name, skill);
                    pickaxe.getEnchant("luck").incrementLevel(other, pickaxe);
                    player.getInventory().addItem(pickaxe.getItemStack());
                    pickaxe.update(player);
                    break;
                default:
                    return true;
            }

        }


        return true;
    }
}
