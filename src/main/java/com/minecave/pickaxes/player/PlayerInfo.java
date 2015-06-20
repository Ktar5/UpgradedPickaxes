/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.player;

import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.item.PItemSerializer;
import com.minecave.pickaxes.util.config.CustomConfig;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Getter
public class PlayerInfo {

    private List<PItem<BlockBreakEvent>> pickaxes;
    private List<PItem<EntityDamageByEntityEvent>> swords;
    private Player player;
    private CustomConfig config;

    public PlayerInfo(Player player) {
        this.player = player;
        this.pickaxes = new ArrayList<>();
        this.swords = new ArrayList<>();
        this.config = new CustomConfig(player);
    }

    public void load() {
        if (config.has("swordData")) {
            byte[] swordData = Base64.getDecoder().decode(config.get("swordData", String.class));
            Inventory swords = PItemSerializer.deserializeInventory(swordData);
            List<PItem<EntityDamageByEntityEvent>> swordList = new ArrayList<>();
            for (ItemStack stack : swords.getContents()) {
                if (stack == null) {
                    continue;
                }
                PItem<EntityDamageByEntityEvent> sword = PItemSerializer.deserializePItem(stack);
                swordList.add(sword);
            }
            swordList.forEach(this::addSword);
        }
        if (config.has("pickData")) {
            byte[] pickData = Base64.getDecoder().decode(config.get("pickData", String.class));
            Inventory pickaxes = PItemSerializer.deserializeInventory(pickData);
            List<PItem<BlockBreakEvent>> pickList = new ArrayList<>();
            for (ItemStack stack : pickaxes.getContents()) {
                if (stack == null) {
                    continue;
                }
                PItem<BlockBreakEvent> pick = PItemSerializer.deserializePItem(stack);
                pickList.add(pick);
            }
            pickList.forEach(this::addPickaxe);
        }
    }

    public void save() {

    }

    public void addSword(PItem<EntityDamageByEntityEvent> sword) {
        this.swords.add(sword);
    }

    public void removeSword(PItem<EntityDamageByEntityEvent> sword) {
        this.swords.remove(sword);
    }

    public void addPickaxe(PItem<BlockBreakEvent> pickaxe) {
        this.pickaxes.add(pickaxe);
    }

    public void removePickaxe(PItem<BlockBreakEvent> pickaxe) {
        this.pickaxes.remove(pickaxe);
    }
}
