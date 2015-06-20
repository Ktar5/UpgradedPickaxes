/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.player;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.WeakHashMap;

public class PlayerManager {

    private Map<Player, PlayerInfo> playerInfoMap;

    public PlayerManager() {
        playerInfoMap = new WeakHashMap<>();
    }

    public PlayerInfo get(Player player) {
        return playerInfoMap.get(player);
    }

    public PlayerInfo add(Player player) {
        if(playerInfoMap.containsKey(player)) {
            return playerInfoMap.get(player);
        }
        PlayerInfo info = new PlayerInfo(player);
        playerInfoMap.put(player, info);
        return info;
    }

    public void remove(Player player) {
        this.playerInfoMap.remove(player);
    }
}
