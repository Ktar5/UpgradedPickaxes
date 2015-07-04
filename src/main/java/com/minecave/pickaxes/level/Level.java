/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.level;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.item.PItemSettings;
import com.minecave.pickaxes.util.firework.FireworkBuilder;
import com.minecave.pickaxes.util.message.MessageBuilder;
import com.minecave.pickaxes.util.message.Strings;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Level {

    private int             id;
    private int             xp;
    private List<String>    commands;
    private FireworkBuilder fireworkBuilder;
    private PItemSettings   pItemSetting;

    public Level(PItemSettings settings, int id, int xp, List<String> commands, FireworkBuilder fireworkBuilder) {
        this.pItemSetting = settings;
        this.id = id;
        this.xp = xp;
        this.commands = commands;
        this.fireworkBuilder = fireworkBuilder;
    }

    public void levelUp(Player player, PItem pItem) {
        Level next = getNext();
        List<String> messages = new ArrayList<>();
        if (pItemSetting == null) {
            EnhancedPicks.getInstance().getLogger().severe("Failed to level up " + player.getName() + " due to invalid PItemSettings object.");
            return;
        }
        for (String s : pItemSetting.getLevelUpMessage()) {
            MessageBuilder builder = new MessageBuilder(s);
            builder.replace(player)
                    .replace(id, MessageBuilder.IntegerType.PLAYER_LEVEL);
            if (next.id < pItem.getMaxLevel().getId()) {
                builder.replace(next.xp, MessageBuilder.IntegerType.NEXT_XP)
                        .replace(next.id, MessageBuilder.IntegerType.NEXT_LEVEL);
            } else {
                builder.replace("N/A", MessageBuilder.IntegerType.NEXT_XP)
                        .replace("Max Level", MessageBuilder.IntegerType.NEXT_LEVEL);
            }
            builder.replace(0, MessageBuilder.IntegerType.XP)
                    .replace(pItem);
            messages.add(Strings.color(builder.build()));
        }
        for (String s : this.commands) {
            if(s.startsWith("give")) {
                String[] split = s.split(" ");
                int slot = player.getInventory().first(Material.matchMaterial(split[2]));
                if(slot != -1) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("$player$", player.getName()));
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(),
                                                        new ItemStack(Material.matchMaterial(split[2]),
                                                                      Integer.parseInt(split[3])));
                }
            }
        }
        if (!pItemSetting.getBlackList().contains(this.id)) {
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
            if (fireworkBuilder != null) {
                fireworkBuilder.play(player);
            }
        }
        player.sendMessage(messages.toArray(new String[messages.size()]));
        player.sendMessage(ChatColor.GOLD + "Current Item Points: " + pItem.getPoints());
    }

    public Level getPrevious() {
        if (id == 1) {
            return null;
        }
        return pItemSetting.getLevel(id - 1);
    }

    public Level getNext() {
        return pItemSetting.getLevel(id + 1);
    }
}
