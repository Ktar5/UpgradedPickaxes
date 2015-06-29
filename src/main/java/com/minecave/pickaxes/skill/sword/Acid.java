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
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Acid extends PSkill {
    private int          numberOfAcidParts;
    private int          radius;
    private List<Player> playerList;

    public Acid(String name, long cooldown, int level, int cost, String perm, int numberOfAcidParts, int radius) {
        super(name, cooldown, level, cost, perm);
        this.numberOfAcidParts = numberOfAcidParts;
        this.radius = radius;
        this.playerList = new ArrayList<>();
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Vector velocity = player.getLocation().getDirection().normalize().multiply(0.5);
        List<Entity> cleanup = new ArrayList<>();
        for (int i = 0; i < numberOfAcidParts; i++) {
            Vector clone = velocity.clone();
            clone.add(new Vector((Math.random() * 0.5) - 0.25,
                    (Math.random() * 0.5) - 0.25,
                    (Math.random() * 0.5) - 0.25));
            EnderPearl enderPearl = player.launchProjectile(EnderPearl.class);
            enderPearl.setVelocity(clone);
            enderPearl.setCustomName("acid");
            enderPearl.setCustomNameVisible(false);
            enderPearl.setShooter(player);
            enderPearl.setMetadata("acid", new FixedMetadataValue(EnhancedPicks.getInstance(), ""));
            enderPearl.setMetadata("player", new FixedMetadataValue(EnhancedPicks.getInstance(), player.getUniqueId().toString()));
            cleanup.add(enderPearl);
        }
        if (!playerList.contains(player)) {
            playerList.add(player);
            Bukkit.getScheduler().runTaskLater(EnhancedPicks.getInstance(), () -> {
                cleanup.forEach(Entity::remove);
                cleanup.clear();
                if (playerList.contains(player)) {
                    playerList.remove(player);
                }
            }, this.cooldown * 20L);
        }
        this.add(player);
    }

    public int getRadius() {
        return radius;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public void removePlayer(Player player) {
        if(this.playerList.contains(player)) {
            this.playerList.remove(player);
        }
    }

    public boolean hasPlayer(Player player) {
        return this.playerList.contains(player);
    }
}
