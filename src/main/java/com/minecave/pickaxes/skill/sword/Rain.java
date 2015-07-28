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
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class Rain extends PSkill {

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
        Set<Material> materialSet = new HashSet<>();
        materialSet.add(Material.AIR);
        Block block = player.getTargetBlock(materialSet, 20);
        Location lookingAt = block.getLocation();
        lookingAt.add(0, arrowHeight, 0);
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= seconds) {
                    this.cancel();
                }
//                new BukkitRunnable() {
//                    int arrows = 0;
//
//                    public void run() {
//                        if (arrows >= arrowCount) {
//                            this.cancel();
//                        }
//                        for (int j = -(arrowCount / 2); j < (arrowCount / 2); j++) {
//                            Location spawnLocation = lookingAt.clone().add(arrows - (arrowCount / 2), 0, j);
//                            Arrow arrow = spawnLocation.getWorld().spawnArrow(spawnLocation, new Vector(0, -1, 0), 1.5f, 12);
//                            arrow.setBounce(false);
//                            arrow.setShooter(player);
//                            arrow.setCustomName("rain");
//                        }
//
//                        arrows++;
//                    }
//                }.runTaskTimer(EnhancedPicks.getInstance(), 0L, 5L);
                int c = (int) Math.sqrt(arrowCount);
                for (int i = (-c)  * 2; i < c * 2; i += 2) {
                    for (int j = (-c) * 2; j < c * 2; j += 2) {
                        Location spawnLocation = lookingAt.clone().add(i, 0, j);
                        Arrow arrow = spawnLocation.getWorld().spawnArrow(spawnLocation, new Vector(0, -1, 0), 1.5f, 12);
                        arrow.setBounce(false);
                        arrow.setShooter(player);
                        arrow.setCustomName("rain");
                    }
                }
                EnhancedPicks.getInstance().getDebugger().debugMessage(player, String.format("Rain | Count: %d, Shooter: %s", arrowCount, player.getUniqueId().toString()));
                count += 1;
            }
        }.runTaskTimer(EnhancedPicks.getInstance(), 0L, 20);
        this.add(player);
    }
}
