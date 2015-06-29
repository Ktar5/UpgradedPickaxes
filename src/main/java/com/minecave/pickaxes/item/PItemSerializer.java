/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.item;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.item.ItemSerialization;
import com.minecave.pickaxes.util.nbt.EPAttributeStorage;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class PItemSerializer {

    public static final String TYPE            = "86adf7ba-1293-44e5-b9ab-7cea5f72d72f";
    public static final String PITEM_SETTINGS  = "ae8664e8-807e-4fe8-8efe-e957d4d48b7a";
    public static final String XP              = "56e9e4f8-6b2e-41b6-82c0-7081c9bc0d00";
    public static final String POINTS          = "915ff3bf-241a-4439-a83f-baf479135366";
    public static final String CUR_LEVEL       = "c8b3a13b-4477-43ab-bc17-7a0fc24d2c5b";
    public static final String MAX_LEVEL       = "048e65b7-fa4a-4d1a-9b39-d0635b5d3f0a";
    public static final String AVAILABLE_SKILL = "c928bb1d-16f2-4704-a6f5-de897ab5cc91";
    public static final String PURCHASED_SKILL = "72cd97a0-c545-45ed-9e07-b937923696b6";
    public static final String CUR_SKILL       = "23446350-9d62-4af1-b489-5ca1e0449460";
    public static final String BLOCKS          = "efb43eb1-5e71-48e2-aab2-f4d7ab653c28";
    public static final String ENCHANTS        = "22cbcd3e-4bcf-4333-9da4-f79a94cdf386";
    public static final String DURABILITY      = "9cbc9c58-4984-423f-9cff-4666b25d8aa8";

    public static Inventory deserializeInventory(byte[] data) {
        try {
            return ItemSerialization.fromBlob(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Bukkit.createInventory(null, 9);
    }

    public static <E extends Event> byte[] serialPItems(List<PItem<E>> list) {
        if (list.isEmpty()) {
            return null;
        }
        ItemStack[] items = new ItemStack[list.size()];
        int i = 0;
        for (PItem<?> p : list) {
            items[i++] = PItemSerializer.serializePItem(p);
            EnhancedPicks.getInstance().getPItemManager().getPItemMap().remove(p.getUuid().toString());
        }
        return ItemSerialization.toBlob(ItemSerialization.getInventoryFromArray(items));
    }

    public static PItem<?> deserializePItem(ItemStack item) {
        EnhancedPicks plugin = EnhancedPicks.getInstance();
        EPAttributeStorage storage;

        //TYPE
        storage = EPAttributeStorage.newTarget(item, TYPE);
        PItemType pItemType = PItemType.valueOf(storage.getData("PICK"));

        //PITEM_SETTINGS
        storage = EPAttributeStorage.newTarget(storage.getTarget(), PITEM_SETTINGS);
        String pItemSettingsKey = storage.getData("");
        if (pItemSettingsKey == null || pItemSettingsKey.equals("")) {
            return null;
        }
        Collection<PItemManager.PItemSettings> settingsCollection = plugin.getPItemManager().getSettings(pItemSettingsKey);
        if(settingsCollection == null) {
            return null;
        }
        PItemManager.PItemSettings pItemSettings = null;
        for(PItemManager.PItemSettings ps : settingsCollection) {
            if(pItemType == ps.getType()) {
                pItemSettings = ps;
            }
        }
        if (pItemSettings == null || pItemSettings.getType() != pItemType) {
            return null;
        }
        PItem<?> pItem = null;
        switch (pItemType) {
            case PICK:
                pItem = pItemSettings.generate(BlockBreakEvent.class);
                break;
            case SWORD:
                pItem = pItemSettings.generate(EntityDamageByEntityEvent.class);
                break;
        }
        if (pItem == null) {
            return null;
        }
        plugin.getPItemManager().addPItem(pItem);

        //XP
        storage = EPAttributeStorage.newTarget(storage.getTarget(), XP);
        pItem.setXp(Integer.parseInt(storage.getData("0")));

        //POINTS
        storage = EPAttributeStorage.newTarget(storage.getTarget(), POINTS);
        pItem.setPoints(Integer.parseInt(storage.getData("1")));

        //CUR_LEVEL
        storage = EPAttributeStorage.newTarget(storage.getTarget(), CUR_LEVEL);
        pItem.setLevel(plugin.getLevelManager().getLevel(Integer.parseInt(storage.getData("1"))));

        //MAX_LEVEL
        storage = EPAttributeStorage.newTarget(storage.getTarget(), MAX_LEVEL);
        pItem.setMaxLevel(plugin.getLevelManager().getLevel(Integer.parseInt(storage.getData("10"))));

        //AVAILABLE_SKILL
        storage = EPAttributeStorage.newTarget(storage.getTarget(), AVAILABLE_SKILL);
        String availSkills = storage.getData("");
        if (availSkills != null && !availSkills.equals("")) {
            String[] skillSplit = availSkills.split(";");
            for (String key : skillSplit) {
                pItem.addAvailableSkill(plugin.getPSkillManager().getPSkill(key));
            }
        }
        for (PSkill pSkill : pItemSettings.getSkillList()) {
            if(!pItem.getAvailableSkills().contains(pSkill)) {
                pItem.addAvailableSkill(pSkill);
            }
        }

        //PURCHASED_SKILL
        storage = EPAttributeStorage.newTarget(storage.getTarget(), PURCHASED_SKILL);
        String purSkills = storage.getData("");
        if (purSkills != null && !purSkills.equals("")) {
            String[] skillSplit = purSkills.split(";");
            for (String key : skillSplit) {
                pItem.addPurchasedSkill(plugin.getPSkillManager().getPSkill(key));
            }
        }

        //CUR_SKILL
        storage = EPAttributeStorage.newTarget(storage.getTarget(), CUR_SKILL);
        String curSkill = storage.getData("");
        if (curSkill == null || curSkill.equals("") || curSkill.equals("null")) {
            pItem.setCurrentSkill(null);
        } else {
            pItem.setCurrentSkill(plugin.getPSkillManager().getPSkill(curSkill));
        }

        //BLOCKS
        storage = EPAttributeStorage.newTarget(storage.getTarget(), BLOCKS);
        pItem.setBlocksBroken(Integer.parseInt(storage.getData("0")));

        //ENCHANTS
        storage = EPAttributeStorage.newTarget(storage.getTarget(), ENCHANTS);
        String aEnchants = storage.getData("");
        if (aEnchants != null && !aEnchants.equals("")) {
            String[] aEnchantSplit = aEnchants.split(";");
            for (String enchant : aEnchantSplit) {
                String[] enchantSplit = enchant.split(":");
                if (enchantSplit.length != 3) {
                    continue;
                }
                PEnchant pEnchant = plugin.getPEnchantManager().getEnchant(enchantSplit[0].toLowerCase());
                if (pEnchant != null) {
                    if (!pItem.hasEnchant(pEnchant.getName())) {
                        pEnchant = pEnchant.cloneEnchant();
                        pEnchant.setLevel(Integer.parseInt(enchantSplit[1]));
                        pEnchant.setMaxLevel(Integer.parseInt(enchantSplit[2]));
                        pItem.addEnchant(pEnchant);
                    } else {
                        pEnchant = pItem.getEnchant(pEnchant.getName());
                        pEnchant.setLevel(Integer.parseInt(enchantSplit[1]));
                        pEnchant.setMaxLevel(Integer.parseInt(enchantSplit[2]));
                    }
                }
            }
        }
        for (PEnchant pEnchant : pItemSettings.getEnchantList()) {
            if(pItem.getEnchant(pEnchant.getName()) == null) {
                PEnchant clone = pEnchant.cloneEnchant();
                clone.setLevel(pEnchant.getLevel());
                clone.setMaxLevel(pEnchant.getMaxLevel());
                pItem.addEnchant(clone);
            }
        }

        //DURABILITY
        storage = EPAttributeStorage.newTarget(storage.getTarget(), DURABILITY);
        short dura = Short.parseShort(storage.getData("0"));

        ItemStack temp = storage.getTarget();
        temp.setDurability(dura);
        ItemStack clone = new ItemStack(temp.getType(), temp.getAmount());
        clone.setData(temp.getData());
        clone.setDurability(temp.getDurability());
        pItem.setItem(clone);
        pItem.getItem().setDurability(dura);

        return pItem;
    }

    public static <T extends Event> ItemStack serializePItem(PItem<T> pItem) {
        EnhancedPicks plugin = EnhancedPicks.getInstance();
        EPAttributeStorage storage;
        Map<String, String> tagMap = new HashMap<>();

        //TYPE
        storage = EPAttributeStorage.newTarget(pItem.getItem(), TYPE);
        storage.setData(pItem.getType().name());

        //PITEM_SETTINGS
        storage = EPAttributeStorage.newTarget(storage.getTarget(), PITEM_SETTINGS);
        storage.setData(pItem.getPItemSettings());

        //XP
        storage = EPAttributeStorage.newTarget(storage.getTarget(), XP);
        storage.setData(String.valueOf(pItem.getXp()));

        //POINTS
        storage = EPAttributeStorage.newTarget(storage.getTarget(), POINTS);
        storage.setData(String.valueOf(pItem.getPoints()));

        //CUR_LEVEL
        storage = EPAttributeStorage.newTarget(storage.getTarget(), CUR_LEVEL);
        storage.setData(String.valueOf(pItem.getLevel().getId()));

        //MAX_LEVEL
        storage = EPAttributeStorage.newTarget(storage.getTarget(), MAX_LEVEL);
        storage.setData(String.valueOf(pItem.getMaxLevel().getId()));

        //AVAILABLE_SKILL
        storage = EPAttributeStorage.newTarget(storage.getTarget(), AVAILABLE_SKILL);
        StringBuilder availSkills = new StringBuilder("");
        for (PSkill pSkill : pItem.getAvailableSkills()) {
            availSkills.append(plugin.getPSkillManager().getPSkillKey(pSkill)).append(";");
        }
        storage.setData(availSkills.toString());

        //PURCHASED_SKILL
        storage = EPAttributeStorage.newTarget(storage.getTarget(), PURCHASED_SKILL);
        StringBuilder purSkills = new StringBuilder("");
        for (PSkill pSkill : pItem.getPurchasedSkills()) {
            purSkills.append(plugin.getPSkillManager().getPSkillKey(pSkill)).append(";");
        }
        storage.setData(purSkills.toString());

        //CUR_SKILL
        storage = EPAttributeStorage.newTarget(storage.getTarget(), CUR_SKILL);
        storage.setData(pItem.getCurrentSkill() == null ? "null" :
                plugin.getPSkillManager().getPSkillKey(pItem.getCurrentSkill()));

        //BLOCKS
        storage = EPAttributeStorage.newTarget(storage.getTarget(), BLOCKS);
        storage.setData(String.valueOf(pItem.getBlocksBroken()));

        //ENCHANTS
        storage = EPAttributeStorage.newTarget(storage.getTarget(), ENCHANTS);
        StringBuilder enchants = new StringBuilder("");
        for (PEnchant pEnchant : pItem.getEnchants()) {
            enchants.append(pEnchant.getName().toLowerCase()).append(":")
                    .append(pEnchant.getLevel()).append(":")
                    .append(pEnchant.getMaxLevel()).append(";");
        }
        storage.setData(enchants.toString());

        //DURABILITY
        storage = EPAttributeStorage.newTarget(storage.getTarget(), DURABILITY);
        storage.setData(String.valueOf(pItem.getItem().getDurability()));

        return storage.getTarget();
    }

    public static String base64PItem(PItem<?> pItem) throws UnsupportedEncodingException {
        if(pItem == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(pItem.toString().getBytes("utf-8"));
    }

    public static PItem<?> singlePItemBase64(String base64) throws UnsupportedEncodingException {
        if(base64 == null || base64.equals("")) {
            return null;
        }
        EnhancedPicks plugin = EnhancedPicks.getInstance();
        String decoded = new String(Base64.getDecoder().decode(base64), "utf-8");
        String[] data = decoded.split(",");
        if (data.length != 12) {
            throw new IllegalArgumentException("Base64 String contains invalid serialized PItem.");
        }
        String type = data[0];
        String pItemSettingsKey = data[1];
        String durability = data[2];
        String xp = data[3];
        String points = data[4];
        String curLevel = data[5];
        String maxLevel = data[6];
        String availSkills = data[7];
        String purSkills = data[8];
        String curSkill = data[9];
        String blocksBroken = data[10];
        String aEnchants = data[11];

        PItemType pItemType = PItemType.valueOf(type);
        if (pItemSettingsKey == null || pItemSettingsKey.equals("")) {
            return null;
        }
        Collection<PItemManager.PItemSettings> settingsCollection = plugin.getPItemManager().getSettings(pItemSettingsKey);
        if(settingsCollection == null) {
            return null;
        }
        PItemManager.PItemSettings pItemSettings = null;
        for(PItemManager.PItemSettings ps : settingsCollection) {
            if(pItemType == ps.getType()) {
                pItemSettings = ps;
            }
        }
        if(pItemSettings == null) {
            return null;
        }
        PItem<?> pItem = null;
        switch (pItemType) {
            case PICK:
                pItem = pItemSettings.generate(BlockBreakEvent.class);
                break;
            case SWORD:
                pItem = pItemSettings.generate(EntityDamageByEntityEvent.class);
                break;
        }
        if (pItem == null) {
            return null;
        }
        pItem.getItem().setDurability(Short.parseShort(durability));

        pItem.setXp(Integer.parseInt(xp));
        pItem.setPoints(Integer.parseInt(points));
        pItem.setLevel(plugin.getLevelManager().getLevel(Integer.parseInt(curLevel)));
        pItem.setMaxLevel(plugin.getLevelManager().getLevel(Integer.parseInt(maxLevel)));

        if (availSkills != null && !availSkills.equals("")) {
            String[] skillSplit = availSkills.split("-");
            for (String key : skillSplit) {
                pItem.addAvailableSkill(plugin.getPSkillManager().getPSkill(key));
            }
        }
        for (PSkill pSkill : pItemSettings.getSkillList()) {
            if(!pItem.getAvailableSkills().contains(pSkill)) {
                pItem.addAvailableSkill(pSkill);
            }
        }

        if (purSkills != null && !purSkills.equals("")) {
            String[] skillSplit = purSkills.split("-");
            for (String key : skillSplit) {
                pItem.addPurchasedSkill(plugin.getPSkillManager().getPSkill(key));
            }
        }

        if (curSkill == null || curSkill.equals("") || curSkill.equals("null")) {
            pItem.setCurrentSkill(null);
        } else {
            pItem.setCurrentSkill(plugin.getPSkillManager().getPSkill(curSkill));
        }

        pItem.setBlocksBroken(Integer.parseInt(blocksBroken));

        if (aEnchants != null && !aEnchants.equals("")) {
            String[] aEnchantSplit = aEnchants.split("-");
            for (String enchant : aEnchantSplit) {
                String[] enchantSplit = enchant.split(":");
                if (enchantSplit.length != 3) {
                    continue;
                }
                PEnchant pEnchant = plugin.getPEnchantManager().getEnchant(enchantSplit[0].toLowerCase());
                if (pEnchant != null) {
                    if (!pItem.hasEnchant(pEnchant.getName())) {
                        pEnchant = pEnchant.cloneEnchant();
                        pEnchant.setLevel(Integer.parseInt(enchantSplit[1]));
                        pEnchant.setMaxLevel(Integer.parseInt(enchantSplit[2]));
                        pItem.addEnchant(pEnchant);
                    } else {
                        pEnchant = pItem.getEnchant(pEnchant.getName());
                        pEnchant.setLevel(Integer.parseInt(enchantSplit[1]));
                        pEnchant.setMaxLevel(Integer.parseInt(enchantSplit[2]));
                    }
                }
            }
        }
        for (PEnchant pEnchant : pItemSettings.getEnchantList()) {
            if(pItem.getEnchant(pEnchant.getName()) == null) {
                PEnchant clone = pEnchant.cloneEnchant();
                clone.setLevel(pEnchant.getLevel());
                clone.setMaxLevel(pEnchant.getMaxLevel());
                pItem.addEnchant(clone);
            }
        }
        ItemStack temp = pItem.getItem();
        ItemStack clone = new ItemStack(temp.getType(), temp.getAmount());
        clone.setData(temp.getData());
        clone.setDurability(temp.getDurability());
        pItem.setItem(clone);

        EnhancedPicks.getInstance().getPItemManager().addPItem(pItem);
        return pItem;
    }

    public static <E extends Event> String base64PItems(Collection<PItem<E>> pItems) throws UnsupportedEncodingException {
        if (pItems == null || pItems.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder("");
        for (PItem<E> pItem : pItems) {
            builder.append(pItem.toString());
            EnhancedPicks.getInstance().getPItemManager().getPItemMap().remove(pItem.getUuid().toString());
        }
        return Base64.getEncoder().encodeToString(builder.toString().getBytes("utf-8"));
    }

    public static List<PItem<?>> pItemsBase64(String base64) throws UnsupportedEncodingException {
        if (base64 == null || base64.equals("")) {
            return Collections.emptyList();
        }
        EnhancedPicks plugin = EnhancedPicks.getInstance();
        List<PItem<?>> pItemList = new ArrayList<>();
        String decoded = new String(Base64.getDecoder().decode(base64), "utf-8");
        String[] items = decoded.split(";");
        for (String item : items) {
            String[] data = item.split(",");
            if (data.length != 12) {
                throw new IllegalArgumentException("Base64 String contains invalid serialized PItem.");
            }
            String type = data[0];
            String pItemSettingsKey = data[1];
            String durability = data[2];
            String xp = data[3];
            String points = data[4];
            String curLevel = data[5];
            String maxLevel = data[6];
            String availSkills = data[7];
            String purSkills = data[8];
            String curSkill = data[9];
            String blocksBroken = data[10];
            String aEnchants = data[11];

            PItemType pItemType = PItemType.valueOf(type);
            if (pItemSettingsKey == null || pItemSettingsKey.equals("")) {
                return null;
            }
            Collection<PItemManager.PItemSettings> settingsCollection = plugin.getPItemManager().getSettings(pItemSettingsKey);
            if(settingsCollection == null) {
                return null;
            }
            PItemManager.PItemSettings pItemSettings = null;
            for(PItemManager.PItemSettings ps : settingsCollection) {
                if(pItemType == ps.getType()) {
                    pItemSettings = ps;
                }
            }
            if(pItemSettings == null) {
                return null;
            }
            PItem<?> pItem = null;
            switch (pItemType) {
                case PICK:
                    pItem = pItemSettings.generate(BlockBreakEvent.class);
                    break;
                case SWORD:
                    pItem = pItemSettings.generate(EntityDamageByEntityEvent.class);
                    break;
            }
            if (pItem == null) {
                return null;
            }
            pItem.getItem().setDurability(Short.parseShort(durability));

            pItem.setXp(Integer.parseInt(xp));
            pItem.setPoints(Integer.parseInt(points));
            pItem.setLevel(plugin.getLevelManager().getLevel(Integer.parseInt(curLevel)));
            pItem.setMaxLevel(plugin.getLevelManager().getLevel(Integer.parseInt(maxLevel)));

            if (availSkills != null && !availSkills.equals("")) {
                String[] skillSplit = availSkills.split("-");
                for (String key : skillSplit) {
                    pItem.addAvailableSkill(plugin.getPSkillManager().getPSkill(key));
                }
            }
            for (PSkill pSkill : pItemSettings.getSkillList()) {
                if(!pItem.getAvailableSkills().contains(pSkill)) {
                    pItem.addAvailableSkill(pSkill);
                }
            }

            if (purSkills != null && !purSkills.equals("")) {
                String[] skillSplit = purSkills.split("-");
                for (String key : skillSplit) {
                    pItem.addPurchasedSkill(plugin.getPSkillManager().getPSkill(key));
                }
            }

            if (curSkill == null || curSkill.equals("") || curSkill.equals("null")) {
                pItem.setCurrentSkill(null);
            } else {
                pItem.setCurrentSkill(plugin.getPSkillManager().getPSkill(curSkill));
            }

            pItem.setBlocksBroken(Integer.parseInt(blocksBroken));

            if (aEnchants != null && !aEnchants.equals("")) {
                String[] aEnchantSplit = aEnchants.split("-");
                for (String enchant : aEnchantSplit) {
                    String[] enchantSplit = enchant.split(":");
                    if (enchantSplit.length != 3) {
                        continue;
                    }
                    PEnchant pEnchant = plugin.getPEnchantManager().getEnchant(enchantSplit[0].toLowerCase());
                    if (pEnchant != null) {
                        if (!pItem.hasEnchant(pEnchant.getName())) {
                            pEnchant = pEnchant.cloneEnchant();
                            pEnchant.setLevel(Integer.parseInt(enchantSplit[1]));
                            pEnchant.setMaxLevel(Integer.parseInt(enchantSplit[2]));
                            pItem.addEnchant(pEnchant);
                        } else {
                            pEnchant = pItem.getEnchant(pEnchant.getName());
                            pEnchant.setLevel(Integer.parseInt(enchantSplit[1]));
                            pEnchant.setMaxLevel(Integer.parseInt(enchantSplit[2]));
                        }
                    }
                }
            }
            for (PEnchant pEnchant : pItemSettings.getEnchantList()) {
                if(pItem.getEnchant(pEnchant.getName()) == null) {
                    PEnchant clone = pEnchant.cloneEnchant();
                    clone.setLevel(pEnchant.getLevel());
                    clone.setMaxLevel(pEnchant.getMaxLevel());
                    pItem.addEnchant(clone);
                }
            }
            ItemStack temp = pItem.getItem();
            ItemStack clone = new ItemStack(temp.getType(), temp.getAmount());
            clone.setData(temp.getData());
            clone.setDurability(temp.getDurability());
            pItem.setItem(clone);

            pItemList.add(pItem);
            EnhancedPicks.getInstance().getPItemManager().addPItem(pItem);
        }
        return pItemList;
    }
}
