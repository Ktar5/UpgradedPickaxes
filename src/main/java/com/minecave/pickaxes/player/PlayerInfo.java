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
import net.md_5.bungee.api.ChatColor;
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
    private int unspentPoints = 0;
    private Player       player;
    private CustomConfig config;

    public PlayerInfo(Player player) {
        this.player = player;
        this.unspentPoints = 0;
        this.pickaxes = new ArrayList<>();
        this.swords = new ArrayList<>();
        this.config = new CustomConfig(player);
    }

    @SuppressWarnings("unchecked")
    public void load() {
        //load items in player's inventory
        List<String> invUUIDs = new ArrayList<>();
        if (config.has("invData")) {
            String saved = config.get("invData", String.class, "");
            if (!saved.equals("")) {
                String[] items = saved.split("&");
                for (String item : items) {
                    String[] data = item.split("%");
                    int i = Integer.parseInt(data[0]);
                    String encoded = data[1];
                    try {
                        PItem<?> pItem = PItemSerializer.singlePItemBase64(encoded);
                        if (pItem == null) {
                            continue;
                        }
                        EnhancedPicks.getInstance().getPItemManager().addPItemForce(pItem);
                        invUUIDs.add(pItem.getUuid().toString());
                        this.player.getInventory().setItem(i, pItem.getItem());
                        ItemStack stack = player.getInventory().getItem(i);
                        pItem.setItem(stack);
                        pItem.updateManually(player, stack);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        List<String> dupes = new ArrayList<>();
        //check inventory for "dead" items
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack != null &&
                (stack.getType() == Material.DIAMOND_PICKAXE ||
                 stack.getType() == Material.DIAMOND_SWORD)) {
                if (!stack.hasItemMeta() && !stack.getItemMeta().hasDisplayName() &&
                    !stack.getItemMeta().hasLore()) {
                    continue;
                }
                boolean isPItem = false;
                for (String s : stack.getItemMeta().getLore()) {
                    if (s.replace(ChatColor.COLOR_CHAR + "", "").startsWith("UUID:")) {
                        isPItem = true;
                    }
                }
                PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(stack);
                if (pItem == null) {
                    if (isPItem) {
                        EnhancedPicks.getInstance().getLogger()
                                     .severe(String.format("Player: %s, Slot: %d. Dead pItem located.", player.getName(), i));
                        player.getInventory().setItem(i, null);
                    }
                } else {
                    if (invUUIDs.contains(pItem.getUuid().toString())) {
                        if (!dupes.contains(pItem.getUuid().toString())) {
                            dupes.add(pItem.getUuid().toString());
                        } else {
                            //dupe
                            player.getInventory().setItem(i, null);
                        }
                    }
                }
            }
        }
        //load items in player's virtual chests
        if (config.has("pickData")) {
            if(config.getConfig().isList("pickData"))
            {
                List<PItem<BlockBreakEvent>> pickList = new ArrayList<>();
                for(String s : config.getConfig().getStringList("pickData")) {
                    try {
                        PItem<?> pItem = PItemSerializer.singlePItemBase64(s);
                        if (pItem == null) {
                            continue;
                        }
                        PItem<BlockBreakEvent> pick = (PItem<BlockBreakEvent>) pItem;
                        pickList.add(pick);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                pickList.forEach(this::addPickaxe);
            } else { //assume string
                try {
                    List<PItem<?>> pickaxes = PItemSerializer.pItemsBase64(config.get("pickData", String.class));
                    if (pickaxes != null) {
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
        }
        if (config.has("swordData")) {
            if(config.getConfig().isList("swordData"))
            {
                List<PItem<EntityDamageByEntityEvent>> swordList = new ArrayList<>();
                for(String s : config.getConfig().getStringList("swordData")) {
                    try {
                        PItem<?> pItem = PItemSerializer.singlePItemBase64(s);
                        if (pItem == null) {
                            continue;
                        }
                        PItem<EntityDamageByEntityEvent> sword = (PItem<EntityDamageByEntityEvent>) pItem;
                        swordList.add(sword);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                swordList.forEach(this::addSword);
            } else { //assume string
                try {
                    List<PItem<?>> swords = PItemSerializer.pItemsBase64(config.get("swordData", String.class));
                    if (swords != null) {
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
        if (config.has("unspentPoints")) {
            this.unspentPoints = config.get("unspentPoints", Integer.class, 0);
        }

    }

    public void addPoints(int points) {
        this.unspentPoints += points;
    }

    public boolean subtractPoints(int points) {
        if (this.unspentPoints < points) {
            return false;
        }
        this.unspentPoints -= points;
        return true;
    }

    public void save() {
        //save items in player's inventory
        StringBuilder inventory = new StringBuilder("");
        for (int i = 0; i < this.player.getInventory().getContents().length; i++) {
            ItemStack item = this.player.getInventory().getItem(i);
            if (item != null) {
                PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(item);
                if (pItem != null) {
                    try {
                        String encoded = PItemSerializer.base64PItem(pItem);
                        inventory.append(String.valueOf(i)).append("%").append(encoded).append("&");
                        player.getInventory().setItem(i, null);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    EnhancedPicks.getInstance().getPItemManager().getPItemMap().remove(pItem.getUuid().toString());
                }
            }
        }
        config.set("invData", "");
        config.set("invData", inventory.toString());
        try {
            config.set("pickData", PItemSerializer.base64PItems(this.pickaxes));
            config.set("swordData", PItemSerializer.base64PItems(this.swords));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        config.set("unspentPoints", this.unspentPoints);
        config.saveConfig();
    }

    public void softSave() {
        StringBuilder inventory = new StringBuilder("");
        for (int i = 0; i < this.player.getInventory().getContents().length; i++) {
            ItemStack item = this.player.getInventory().getItem(i);
            if (item != null) {
                PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(item);
                if (pItem != null) {
//                    EnhancedPicks.getInstance().getPItemManager().getPItemMap().remove(pItem.getUuid().toString());
                    try {
                        String encoded = PItemSerializer.base64PItem(pItem);
                        inventory.append(String.valueOf(i)).append("%").append(encoded).append("&");
//                        player.getInventory().setItem(i, null);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        config.set("invData", "");
        config.set("invData", inventory.toString());
//            player.sendMessage("Saved your enhanced inventory items.");
        config.saveConfig();
    }

    public void softSaveChests() {
        try {
            config.set("pickData", PItemSerializer.base64PItemsNoRemove(this.pickaxes));
            config.set("swordData", PItemSerializer.base64PItemsNoRemove(this.swords));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        config.saveConfig();
//        this.player.sendMessage(ChatColor.DARK_GREEN + "Saved your virtual enhanced item chests.");
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
