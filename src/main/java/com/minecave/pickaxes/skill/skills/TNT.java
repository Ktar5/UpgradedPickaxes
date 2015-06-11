/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.skill.skills;

import com.minecave.pickaxes.skill.Skill;
import org.bukkit.event.player.PlayerInteractEvent;

public class TNT extends Skill {

    public TNT(String name, long cooldown, int level) {
        super(name, cooldown, level);
    }

    @Override
    public void use(PlayerInteractEvent event) {

    }
}
