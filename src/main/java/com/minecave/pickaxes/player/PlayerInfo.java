/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.player;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.item.PItemSerializer;
import com.minecave.pickaxes.util.config.CustomConfig;
import com.minecave.pickaxes.util.item.ItemSerialization;
import lombok.Getter;
import org.bukkit.Material;
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
        //load items in player's inventory
        for (int i = 0; i < this.player.getInventory().getContents().length; i++) {
            ItemStack item = this.player.getInventory().getItem(i);
            if (item != null) {
                if (item.getType() == Material.DIAMOND_PICKAXE) {
                    PItem<BlockBreakEvent> pItem = PItemSerializer.deserializePItem(item);
                    if (pItem != null) {
                        this.player.getInventory().setItem(i, pItem.getItem());
                        this.player.updateInventory();
                    }
                } else if (item.getType() == Material.DIAMOND_SWORD) {
                    PItem<EntityDamageByEntityEvent> pItem = PItemSerializer.deserializePItem(item);
                    if (pItem != null) {
                        this.player.getInventory().setItem(i, pItem.getItem());
                        this.player.updateInventory();
                    }
                }
            }
        }
        //load items in player's virtual chests
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
        if (config.has("swordData")) {
            byte[] swordData = ItemSerialization.toBytes(config.get("swordData", String.class));
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
    }

    public void save() {
        //save items in player's inventory
        for (int i = 0; i < this.player.getInventory().getContents().length; i++) {
            ItemStack item = this.player.getInventory().getItem(i);
            if (item != null) {
                if (item.getType() == Material.DIAMOND_PICKAXE) {
                    PItem<BlockBreakEvent> pItem = EnhancedPicks.getInstance().getPItemManager()
                            .getPItem(BlockBreakEvent.class, item);
                    if (pItem != null) {
                        ItemStack newItem = PItemSerializer.serializePItem(pItem);
                        this.player.getInventory().setItem(i, newItem);
                        this.player.updateInventory();
                    }
                } else if (item.getType() == Material.DIAMOND_SWORD) {
                    PItem<EntityDamageByEntityEvent> pItem = EnhancedPicks.getInstance().getPItemManager()
                            .getPItem(EntityDamageByEntityEvent.class, item);
                    if (pItem != null) {
                        ItemStack newItem = PItemSerializer.serializePItem(pItem);
                        this.player.getInventory().setItem(i, newItem);
                        this.player.updateInventory();
                    }
                }
            }
        }
        //save items in player's virtual chests
        byte[] pickData = PItemSerializer.serialPItems(this.pickaxes);
        byte[] swordData = PItemSerializer.serialPItems(this.swords);
        if (pickData != null) {
            config.set("pickData", ItemSerialization.toBase64(pickData));
        }
        if (swordData != null) {
            config.set("swordData", ItemSerialization.toBase64(swordData));
        }
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
