/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.skill.sword;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.skill.PSkill;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class Rage extends PSkill {

    private List<Player> ragePlayers;
    private int time = 0;

    public Rage(String name, long cooldown, int time, int level, int cost, String perm) {
        super(name, cooldown, level, cost, perm);
        ragePlayers = new ArrayList<>();
        this.time = time;
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!ragePlayers.contains(player)) {
            ragePlayers.add(player);
            Bukkit.getScheduler().runTaskLater(EnhancedPicks.getInstance(), () -> {
                ragePlayers.remove(player);
                player.sendMessage(ChatColor.RED + "Your rage has ended.");
            }, this.time * 20L);
        }
        this.add(player);
    }

    public List<Player> getRagePlayers() {
        return this.ragePlayers;
    }

    public void removePlayer(Player player) {
        if(this.ragePlayers.contains(player)) {
            this.ragePlayers.remove(player);
        }
    }
}
