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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class Acid extends PSkill {
    private int numberOfAcidParts;
    private int radius;

    public Acid(String name, long cooldown, int level, int cost, String perm, int numberOfAcidParts, int radius) {
        super(name, cooldown, level, cost, perm);
        this.numberOfAcidParts = numberOfAcidParts;
        this.radius = radius;
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Vector velocity = player.getLocation().getDirection().normalize().multiply(0.5);
        for (int i = 0; i < numberOfAcidParts; i++) {
            Vector clone = velocity.clone();
            clone.add(new Vector((Math.random() * 0.5) - 0.25,
                    (Math.random() * 0.5) - 0.25,
                    (Math.random() * 0.5) - 0.25));
            EnderPearl enderPearl = player.launchProjectile(EnderPearl.class);
            enderPearl.setVelocity(clone);
            enderPearl.setCustomName("acid");
            enderPearl.setCustomNameVisible(false);
            enderPearl.setMetadata("player", new FixedMetadataValue(EnhancedPicks.getInstance(), player.getUniqueId().toString()));
        }
        this.add(player);
    }

    public int getRadius() {
        return radius;
    }
}
