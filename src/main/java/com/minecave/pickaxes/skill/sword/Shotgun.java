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
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class Shotgun extends PSkill {

    private final int numberOfSnowballs;

    public Shotgun(String name, long cooldown, int level, int cost, String perm, int numberOfSnowballs) {
        super(name, cooldown, level, cost, perm);
        this.numberOfSnowballs = numberOfSnowballs;
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Vector velocity = player.getLocation().getDirection().normalize().multiply(0.5);
        for (int i = 0; i < numberOfSnowballs; i++) {
            Vector clone = velocity.clone();
            clone.add(new Vector((Math.random() * 0.5) - 0.25,
                    (Math.random() * 0.5) - 0.25,
                    (Math.random() * 0.5) - 0.25));
            Snowball snowball = player.launchProjectile(Snowball.class);
            snowball.setVelocity(clone);
            snowball.setCustomName("shotgun");
            snowball.setMetadata("player", new FixedMetadataValue(EnhancedPicks.getInstance(), player.getUniqueId().toString()));
            snowball.setCustomNameVisible(false);
        }
        this.add(player);
    }
}
