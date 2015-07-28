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
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Acid extends PSkill {
    private int          numberOfAcidParts;
    private int          radius;
    private List<String> playerList;

    public Acid(String name, long cooldown, int level, int cost, String perm, int numberOfAcidParts, int radius) {
        super(name, cooldown, level, cost, perm);
        this.numberOfAcidParts = numberOfAcidParts;
        this.radius = radius;
        this.playerList = new ArrayList<>();
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Vector velocity = player.getLocation().getDirection().normalize().multiply(0.8);
//        List<EnderPearl> cleanup = new ArrayList<>();
        this.add(player);
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                for(int i = 0; i < 2; i++) {
                    Vector clone = velocity.clone();
                    clone.add(new Vector((Math.random() * 0.3) - 0.25,
                                         (Math.random() * 0.3) + 0.1,
                                         (Math.random() * 0.3) - 0.25));
                    EnderPearl enderPearl = player.launchProjectile(EnderPearl.class);
                    enderPearl.setVelocity(clone);
                    enderPearl.setCustomName("acid");
                    enderPearl.setCustomNameVisible(false);
                    enderPearl.setShooter(player);
                    count++;
                }

                if (count >= numberOfAcidParts) {
                    this.cancel();
//                    Bukkit.broadcastMessage(String.valueOf(count));
                }
            }
        }.runTaskTimer(EnhancedPicks.getInstance(), 0L, 2L);
//        for (int i = 0; i < numberOfAcidParts; i++) {

//            enderPearl.setMetadata("acid", new FixedMetadataValue(EnhancedPicks.getInstance(), ""));
//        le.removeMetadata("pitemplayer", EnhancedPicks.getInstance());
//            enderPearl.setMetadata("pitemplayer", new FixedMetadataValue(EnhancedPicks.getInstance(), player.getUniqueId().toString()));
//            cleanup.add(enderPearl);
//        }
        EnhancedPicks.getInstance().getDebugger().debugMessage(player, String.format("Acid | Count: %d, Shooter: %s", numberOfAcidParts, player.getUniqueId().toString()));
//        cleanup.clear();
//        if (!playerList.contains(player.getUniqueId().toString())) {
//            playerList.add(player.getUniqueId().toString());
//            Bukkit.getScheduler().runTaskLater(EnhancedPicks.getInstance(), () -> {
//                cleanup.stream().filter(entity -> entity.isValid() && !entity.isDead()).forEach(org.bukkit.entity.Entity::remove);
//                cleanup.clear();
//                if (playerList.contains(player.getUniqueId().toString())) {
//                    playerList.remove(player.getUniqueId().toString());
//                }
//            }, this.cooldown * 20L);
//        }
    }

    public int getRadius() {
        return radius;
    }

    public List<String> getPlayerList() {
        return playerList;
    }

    public void removePlayer(Player player) {
        if (this.playerList.contains(player.getUniqueId().toString())) {
            this.playerList.remove(player.getUniqueId().toString());
        }
    }

    public boolean hasPlayer(Player player) {
        return this.playerList.contains(player.getUniqueId().toString());
    }
}
