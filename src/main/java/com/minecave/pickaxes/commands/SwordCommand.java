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
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.menu.menus.MenuCreator;
import com.minecave.pickaxes.player.PlayerInfo;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SwordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (strings.length == 1) {
                if (strings[0].equalsIgnoreCase("levelup") ||
                        strings[0].equalsIgnoreCase("lvlup") ||
                        strings[0].equalsIgnoreCase("level") ||
                        strings[0].equalsIgnoreCase("lvl")) {
                    Player player = (Player) commandSender;
                    PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
                    int cost = EnhancedPicks.getInstance().getCostPerLevel();
                    if (pItem == null) {
                        player.sendMessage(ChatColor.RED + "You must have an enhanced item in your hand.");
                        return true;
                    }
                    if (pItem.getPoints() < cost) {
                        player.sendMessage(ChatColor.RED + "You don't have enough points for a level.");
                        return true;
                    } else if (pItem.getLevel().getId() + 1 <= pItem.getMaxLevel().getId()) {
                        player.sendMessage(ChatColor.RED + "Level " + (pItem.getLevel().getId() + 1) +
                                " is higher than the max " + pItem.getMaxLevel().getId());
                        return true;
                    } else {
                        pItem.subtractPoints(cost);
                        pItem.levelUp(player);
                        player.sendMessage(ChatColor.GOLD + "Your level'd your item to: " + ChatColor.WHITE + pItem.getLevel().getId());
                    }
                    return true;
                } else {
                    return false;
                }
            } else if (strings.length == 2) {
                if (strings[0].equalsIgnoreCase("points") ||
                        strings[0].equalsIgnoreCase("p")) {
                    if (!PointsCommand.isInteger(strings[1])) {
                        commandSender.sendMessage(ChatColor.RED + "Points must be an integer.");
                        return false;
                    } else {
                        Player player = (Player) commandSender;
                        PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
                        PlayerInfo info = EnhancedPicks.getInstance().getPlayerManager().add(player);
                        int points = Integer.parseInt(strings[1]);
                        if (pItem == null) {
                            player.sendMessage(ChatColor.RED + "You must have an enhanced pick/sword in your hand.");
                            return true;
                        }
                        if (info.subtractPoints(points)) {
                            pItem.addPoints(points);
                            player.sendMessage(ChatColor.GOLD + "You added " + ChatColor.WHITE + points + " to your sword.");
                            player.sendMessage(ChatColor.GOLD + "Your sword's current points: " + ChatColor.WHITE + pItem.getPoints());
                        } else {
                            player.sendMessage(ChatColor.RED + "You can't spend points you don't have.");
                        }
                        return true;
                    }
                } else if (strings[0].equalsIgnoreCase("levelup") ||
                        strings[0].equalsIgnoreCase("lvlup") ||
                        strings[0].equalsIgnoreCase("level") ||
                        strings[0].equalsIgnoreCase("lvl")) {
                    Player player = (Player) commandSender;
                    if (!player.isOp() || !player.hasPermission("pickaxes.admin")) {
                        PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
                        int levels = Integer.parseInt(strings[1]);
                        int cost = levels * EnhancedPicks.getInstance().getCostPerLevel();
                        if (pItem == null) {
                            player.sendMessage(ChatColor.RED + "You must have an enhanced item in your hand.");
                            return true;
                        }
                        if (pItem.getPoints() < cost) {
                            player.sendMessage(ChatColor.RED + "You don't have enough points for " + levels + " levels.");
                            return true;
                        } else if (pItem.getLevel().getId() + levels > pItem.getMaxLevel().getId()) {
                            player.sendMessage(ChatColor.RED + "Level " + (pItem.getLevel().getId() + 1) +
                                               " is higher than the max " + pItem.getMaxLevel().getId());
                            return true;
                        } else {
                            pItem.subtractPoints(cost);
                            for (int i = 0; i < levels; i++) {
                                pItem.setXp(pItem.getXp() + pItem.getXpToNextLevel());
                                pItem.levelUp(player);
                            }
                            player.sendMessage(ChatColor.GOLD + "Your level'd your item to: " + ChatColor.WHITE + pItem.getLevel().getId());
                        }
                    } else {
                        PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
                        int levels = Integer.parseInt(strings[1]);
                        if (pItem == null) {
                            player.sendMessage(ChatColor.RED + "You must have an enhanced item in your hand.");
                            return true;
                        }
                        if (pItem.getLevel().getId() + levels > pItem.getMaxLevel().getId()) {
                            levels = pItem.getMaxLevel().getId() - pItem.getLevel().getId();
                        }
                        for (int i = 0; i < levels; i++) {
                            pItem.setXp(pItem.getXp() + pItem.getXpToNextLevel());
                            pItem.levelUp(player);
                        }
                        player.sendMessage(ChatColor.GOLD + "Your level'd your item to: " + ChatColor.WHITE + pItem.getLevel().getId());
                    }
                    return true;
                } else if(strings[0].equalsIgnoreCase("xp")) {
                    Player player = (Player) commandSender;
                    if(player.isOp() || player.hasPermission("pickaxes.admin")) {
                        PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
                        int xp = Integer.parseInt(strings[1]);
                        if (pItem == null) {
                            player.sendMessage(ChatColor.RED + "You must have an enhanced item in your hand.");
                            return true;
                        }
                        pItem.setXp(xp);
                    }
                }
                else {
                    return false;
                }
            } else {
                MenuCreator.createMainSword((Player) commandSender);
            }
        }
        return true;
    }
}
