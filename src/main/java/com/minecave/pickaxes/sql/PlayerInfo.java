package com.minecave.pickaxes.sql;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.pitem.Sword;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Timothy Andis
 */
public class PlayerInfo {

    private List<Pickaxe> pickaxes;
    private List<Sword> swords;
    private Player player;
    private static Map<UUID, PlayerInfo> infoMap = new HashMap<>();

    public PlayerInfo(Player player) {
        this.player = player;
        this.pickaxes = new ArrayList<>();
        this.swords = new ArrayList<>();
        infoMap.put(player.getUniqueId(), this);
    }

    public PlayerInfo(List<Pickaxe> pickaxes, List<Sword> swords, Player player) {
        this.pickaxes = pickaxes;
        this.swords = swords;
        this.player = player;
        infoMap.put(player.getUniqueId(), this);
    }

    public static PlayerInfo get(Player player) {
        return infoMap.get(player.getUniqueId());
    }

    public static void save(Player player) {
        PlayerInfo info = PlayerInfo.get(player);
        if (info == null) {
            return;
        }
        info.logOff();
    }

    public void addSword(Sword sword) {
        this.swords.add(sword);
    }

    public void addPickaxe(Pickaxe pickaxe) {
        this.pickaxes.add(pickaxe);
    }

    public void removeSword(Sword sword) {
        this.swords.remove(sword);
    }

    public void removePickaxe(Pickaxe pickaxe) {
        this.pickaxes.remove(pickaxe);
    }

    public List<Pickaxe> getPickaxes() {
        return pickaxes;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Sword> getSwords() {
        return swords;
    }

    public static Map<UUID, PlayerInfo> getInfoMap() {
        return infoMap;
    }

    public void logOff() {
        infoMap.remove(this.getPlayer().getUniqueId());
        SQLManager sqlManager = PickaxesRevamped.getInstance().getSqlManager();
        sqlManager.logoff(this);
    }
}
