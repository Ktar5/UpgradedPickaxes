package com.minecave.pickaxes.sql;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.pitem.Sword;
import com.minecave.pickaxes.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author Timothy Andis
 */
public class PlayerInfo {

    private List<Pickaxe> pickaxes;
    private List<Sword> swords;
    private Player player;
    private List<Pickaxe> invPicks;
    private List<Sword> invSwords;
    private static Map<UUID, PlayerInfo> infoMap = new HashMap<>();

    public PlayerInfo(Player player) {
        this.player = player;
        this.pickaxes = new ArrayList<>();
        this.swords = new ArrayList<>();
        this.invPicks = new ArrayList<>();
        this.invSwords = new ArrayList<>();
        infoMap.put(player.getUniqueId(), this);
    }

    public PlayerInfo(List<Pickaxe> pickaxes, List<Sword> swords, Player player) {
        this.pickaxes = pickaxes;
        this.swords = swords;
        if(this.swords == null) {
            this.swords = new ArrayList<>();
        }
        if(this.pickaxes == null) {
            this.pickaxes = new ArrayList<>();
        }
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
        for(ItemStack i : player.getInventory()) {
            Pickaxe p = Pickaxe.tryFromItem(i);
            if(p != null) {
                info.invPicks.add(p);
                continue;
            }
            Sword s = Sword.tryFromItem(i);
            if(s != null) {
                info.invSwords.add(s);
            }
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
        for (int j = 0; j < player.getInventory().getContents().length; j++) {
            ItemStack i = player.getInventory().getItem(j);
            Pickaxe p = Pickaxe.tryFromItem(i);
            if(p == null) {
                Sword s = Sword.tryFromItem(i);
                if(s != null) {
                    player.getInventory().setItem(j, Utils.serializeSword(s));
                }
            } else {
                player.getInventory().setItem(j, Utils.serializePick(p));
            }
        }
        infoMap.remove(this.getPlayer().getUniqueId());
        SQLManager sqlManager = PickaxesRevamped.getInstance().getSqlManager();
        sqlManager.logoff(this);
    }
}
