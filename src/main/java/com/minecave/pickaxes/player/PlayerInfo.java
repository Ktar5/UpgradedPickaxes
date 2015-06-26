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
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PlayerInfo {

    private List<PItem<BlockBreakEvent>>           pickaxes;
    private List<PItem<EntityDamageByEntityEvent>> swords;
    private Player                                 player;
    private CustomConfig                           config;

    public PlayerInfo(Player player) {
        this.player = player;
        this.pickaxes = new ArrayList<>();
        this.swords = new ArrayList<>();
        this.config = new CustomConfig(player);
    }

    @SuppressWarnings("unchecked")
    public void load() {
        //load items in player's inventory
        for (int i = 0; i < this.player.getInventory().getContents().length; i++) {
            ItemStack item = this.player.getInventory().getItem(i);
            if (item != null) {
                if (item.getType() == Material.DIAMOND_PICKAXE) {
                    PItem<BlockBreakEvent> pItem = (PItem<BlockBreakEvent>) PItemSerializer.deserializePItem(item);
                    if (pItem != null) {
                        EnhancedPicks.getInstance().getPItemManager().addPItem(pItem);
                        this.player.getInventory().setItem(i, pItem.getItem());
                        pItem.updateManually(player, pItem.getItem());
                    }
                } else if (item.getType() == Material.DIAMOND_SWORD) {
                    PItem<EntityDamageByEntityEvent> pItem = (PItem<EntityDamageByEntityEvent>) PItemSerializer.deserializePItem(item);
                    if (pItem != null) {
                        EnhancedPicks.getInstance().getPItemManager().addPItem(pItem);
                        this.player.getInventory().setItem(i, pItem.getItem());
                        pItem.updateManually(player, pItem.getItem());
                    }
                }
            }
        }
        //load items in player's virtual chests
        if (config.has("pickData")) {
            try {
                List<PItem<?>> pickaxes = PItemSerializer.pItemsBase64(config.get("pickData", String.class));
                if(pickaxes != null) {
                    List<PItem<BlockBreakEvent>> pickList = new ArrayList<>();
                    for (PItem<?> pItem : pickaxes) {
                        if (pItem == null) {
                            continue;
                        }
                        PItem<BlockBreakEvent> pick = (PItem<BlockBreakEvent>) pItem;
                        pickList.add(pick);
                    }
                    pickList.forEach(this::addPickaxe);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (config.has("swordData")) {
            try {
                List<PItem<?>> swords = PItemSerializer.pItemsBase64(config.get("swordData", String.class));
                if(swords != null) {
                    List<PItem<EntityDamageByEntityEvent>> swordList = new ArrayList<>();
                    for (PItem<?> pItem : swords) {
                        if (pItem == null) {
                            continue;
                        }
                        PItem<EntityDamageByEntityEvent> sword = (PItem<EntityDamageByEntityEvent>) pItem;
                        swordList.add(sword);
                    }
                    swordList.forEach(this::addSword);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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
                        EnhancedPicks.getInstance().getPItemManager().getPItemMap().remove(pItem.getUuid().toString());
                        ItemStack newItem = PItemSerializer.serializePItem(pItem);
                        this.player.getInventory().setItem(i, newItem);
                        this.player.updateInventory();
                    }
                } else if (item.getType() == Material.DIAMOND_SWORD) {
                    PItem<EntityDamageByEntityEvent> pItem = EnhancedPicks.getInstance().getPItemManager()
                            .getPItem(EntityDamageByEntityEvent.class, item);
                    if (pItem != null) {
                        EnhancedPicks.getInstance().getPItemManager().getPItemMap().remove(pItem.getUuid().toString());
                        ItemStack newItem = PItemSerializer.serializePItem(pItem);
                        this.player.getInventory().setItem(i, newItem);
                        this.player.updateInventory();
                    }
                }
            }
        }
        try {
            config.set("pickData", PItemSerializer.base64PItems(this.pickaxes));
            config.set("swordData", PItemSerializer.base64PItems(this.swords));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        config.saveConfig();
    }

    public void addSword(PItem<EntityDamageByEntityEvent> sword) {
        this.swords.add(sword);
        sword.updateMeta();
    }

    public void removeSword(PItem<EntityDamageByEntityEvent> sword) {
        this.swords.remove(sword);
    }

    public void addPickaxe(PItem<BlockBreakEvent> pickaxe) {
        this.pickaxes.add(pickaxe);
        pickaxe.updateMeta();
    }

    public void removePickaxe(PItem<BlockBreakEvent> pickaxe) {
        this.pickaxes.remove(pickaxe);
    }
}
