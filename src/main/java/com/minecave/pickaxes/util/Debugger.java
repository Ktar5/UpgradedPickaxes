/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Debugger {

    private final Set<Player> targets;

    public Debugger() {
        this.targets = new HashSet<>();
    }

    public void debugBroadcast(String msg) {
        this.targets.forEach(p -> p.sendMessage(msg));
    }

    public void debugMessage(Player player, String msg) {
        if (!player.hasPermission("pickaxes.admin")) {
            return;
        }
        if(this.has(player)) {
            player.sendMessage(msg);
        }
    }

    public void add(Player player) {
        this.targets.add(player);
    }

    public void remove(Player player) {
        this.targets.remove(player);
    }

    public boolean has(Player player) {
        return this.targets.contains(player);
    }

    public void clear() {
        this.targets.clear();
    }
}
