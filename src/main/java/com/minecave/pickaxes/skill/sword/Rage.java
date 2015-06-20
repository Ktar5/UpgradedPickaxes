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
import org.bukkit.event.player.PlayerInteractEvent;

public class Rage extends PSkill {
    public Rage(String name, long cooldown, int level, int cost, String perm) {
        super(name, cooldown, level, cost, perm);
    }

    @Override
    public void use(PlayerInteractEvent event) {

    }
}
