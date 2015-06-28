/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.skill.sword;

import com.minecave.pickaxes.skill.PSkill;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

public class Rage extends PSkill {

    private List<Player> ragePlayers;

    public Rage(String name, long cooldown, int level, int cost, String perm) {
        super(name, cooldown, level, cost, perm);
        ragePlayers = new ArrayList<>();
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!ragePlayers.contains(player)) {
            ragePlayers.add(player);
        }
        this.add(player);
    }
}
