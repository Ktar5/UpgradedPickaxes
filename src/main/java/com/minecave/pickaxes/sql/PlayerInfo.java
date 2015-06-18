package com.minecave.pickaxes.sql;

import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.pitem.Sword;
import com.minecave.pickaxes.utils.CustomConfig;
import com.minecave.pickaxes.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author Timothy Andis
 */
public class PlayerInfo {

    private List<Pickaxe> pickaxes;
    private List<Sword> swords;
    private Player player;
    private CustomConfig config;
    private static Map<UUID, PlayerInfo> infoMap = new HashMap<>();

    public PlayerInfo(Player player) {
        this.player = player;
        this.pickaxes = new ArrayList<>();
        this.swords = new ArrayList<>();
        this.config = new CustomConfig(player.getUniqueId().toString());
        infoMap.put(player.getUniqueId(), this);
    }

    public PlayerInfo(List<Pickaxe> pickaxes, List<Sword> swords, Player player) {
        this.pickaxes = pickaxes;
        this.swords = swords;
        if (this.swords == null) {
            this.swords = new ArrayList<>();
        }
        if (this.pickaxes == null) {
            this.pickaxes = new ArrayList<>();
        }
        this.player = player;
        this.config = new CustomConfig(player.getUniqueId().toString());
        infoMap.put(player.getUniqueId(), this);
    }

    public void init() {
        PlayerInfo info = new PlayerInfo(player);
        if (config.has("swordData")) {
            byte[] swordData = Base64.getDecoder().decode(config.get("swordData", String.class));
            Inventory swords = Utils.deserializeInventory(swordData);
            List<Sword> swordList = new ArrayList<>();
            for (ItemStack stack : swords.getContents()) {
                if (stack == null) {
                    continue;
                }
                Sword sword = Utils.deserializeSword(stack);
                swordList.add(sword);
            }
            swordList.forEach(info::addSword);
        }
        if (config.has("pickData")) {
            byte[] pickData = Base64.getDecoder().decode(config.get("pickData", String.class));
            Inventory pickaxes = Utils.deserializeInventory(pickData);
            List<Pickaxe> pickList = new ArrayList<>();
            for (ItemStack stack : pickaxes.getContents()) {
                if (stack == null) {
                    continue;
                }
                Pickaxe pick = Utils.deserializePick(stack);
                pickList.add(pick);
            }
            pickList.forEach(info::addPickaxe);
        }
    }

    public static PlayerInfo get(Player player) {
        PlayerInfo info = infoMap.get(player.getUniqueId());
        if (info == null) {
            info = new PlayerInfo(player);
        }
        return info;
    }

    public static void init(Player player) {
        PlayerInfo info = PlayerInfo.get(player);
        if (info == null) {
            return;
        }
        info.init();
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
        for (int j = 0; j < player.getInventory().getContents().length; j++) {
            ItemStack i = player.getInventory().getItem(j);
            Pickaxe p = Pickaxe.tryFromItem(i);
            if (p == null) {
                Sword s = Sword.tryFromItem(i);
                if (s != null) {
                    player.getInventory().setItem(j, Utils.serializeSword(s));
                    tryGetPItem(player, i);
                }
            } else {
                player.getInventory().setItem(j, Utils.serializePick(p));
                tryGetPItem(player, i);
            }
        }
        byte[] swordData = Utils.serialSwords(swords);
        byte[] pickData = Utils.serialPicks(pickaxes);
        String pickString = pickData == null ?
                null : Base64.getEncoder().encodeToString(pickData);
        String swordString = swordData == null ?
                null : Base64.getEncoder().encodeToString(swordData);
        config.set("pickData", pickString);
        config.set("swordData", swordString);
        config.saveConfig();
        infoMap.remove(this.getPlayer().getUniqueId());
    }

    public void tryGetPItem(Player player, ItemStack i) {
        Pickaxe p = Pickaxe.tryFromItem(i);
        if (p == null) {
            Sword s;
            if ((s = Sword.tryFromItem(i)) != null) {
                s.update(player);
            }
        } else {
            p.update(player);
        }
    }
}
