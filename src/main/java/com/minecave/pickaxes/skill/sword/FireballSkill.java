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
import org.bukkit.Location;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class FireballSkill extends PSkill {

    public FireballSkill(String name, long cooldown, int level, int cost, String perm) {
        super(name, cooldown, level, cost, perm);
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!this.wg.canBuild(event.getPlayer(), player.getEyeLocation())){
            return;
        }
        Location spawn = player.getEyeLocation().toVector()
                .add(player.getEyeLocation().getDirection().multiply(3))
                .toLocation(player.getWorld());
        Fireball fireball = spawn.getWorld().spawn(spawn, Fireball.class);
        fireball.setDirection(player.getEyeLocation().toVector().multiply(2));
        fireball.setBounce(false);
        fireball.setIsIncendiary(false);
        fireball.setYield(0);
        fireball.setCustomName("fireball");
    }
}
