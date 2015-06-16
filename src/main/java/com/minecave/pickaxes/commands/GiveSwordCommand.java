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
import com.minecave.pickaxes.items.ItemBuilder;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.pitem.Sword;
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

public class GiveSwordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

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
                    String name = ChatColor.AQUA + player.getName() + "'s Diamond Sword: Level: 1 XP: 0";
                    builder.name(name);

                    Sword sword = new Sword(builder.build(), Level.ONE, 0, name, skill);
                    sword.getEnchant("luck").increaseLevel(player, sword);
                    player.getInventory().addItem(sword.getItemStack());
                    sword.update(player);
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
                    name = ChatColor.AQUA + other.getName() + "'s Diamond Sword: Level: 1 XP: 0";
                    builder.name(name);

                    sword = new Sword(builder.build(), Level.ONE, 0, name, skill);
                    sword.getEnchant("luck").increaseLevel(other, sword);
                    player.getInventory().addItem(sword.getItemStack());
                    sword.update(other);
                    break;
                default:
                    return true;
            }

        }
        return true;
    }
}
