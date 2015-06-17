/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.skill.skills.sword;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.skill.Skill;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Rain extends Skill {

    @Getter
    private int arrowHeight;
    @Getter
    private int arrowCount;
    @Getter
    private int seconds;

    public Rain(String name, long cooldown, int level, int cost, String perm,
                int arrowHeight, int arrowCount, int seconds) {
        super(name, cooldown, level, cost, perm);
        this.arrowHeight = arrowHeight;
        this.arrowCount = arrowCount;
        this.seconds = seconds;
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location lookingAt = player.getEyeLocation();
        if(!this.wg.canBuild(event.getPlayer(), lookingAt)){
            return;
        }
        lookingAt.add(0, arrowHeight, 0);
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count >= seconds) {
                    this.cancel();
                }
                for (int i = -(arrowCount / 2); i < (arrowCount / 2); i++) {
                    for (int j = -(arrowCount / 2); j < (arrowCount / 2); j++) {
                        Location spawnLocation = lookingAt.clone().add(i, 0, j);
                        if(!wg.canBuild(event.getPlayer(), spawnLocation)){
                            continue;
                        }
                        Arrow arrow = spawnLocation.getWorld().spawnArrow(spawnLocation, new Vector(0, 1, 0), 1f, 12);
                        arrow.setBounce(false);
                        arrow.setShooter(player);
                    }
                }
                count += 1;
            }
        }.runTaskTimer(PickaxesRevamped.getInstance(), 0L, 20);
    }
}