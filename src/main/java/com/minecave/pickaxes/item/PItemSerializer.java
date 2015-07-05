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
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

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

    public static String base64PItem(PItem<?> pItem) throws UnsupportedEncodingException {
        if (pItem == null) {
            return null;
        }
//        return Base64.getEncoder().encodeToString(pItem.toString().getBytes("utf-8"));
        return pItem.toString();
    }

    public static PItem<?> singlePItemBase64(String base64) throws UnsupportedEncodingException {
        if (base64 == null || base64.equals("")) {
            return null;
        }
        EnhancedPicks plugin = EnhancedPicks.getInstance();
        String decoded = base64;
        if (decoded.endsWith(";")) {
            decoded = decoded.substring(0, decoded.length() - 2);
        }
        String[] data = decoded.split(",");
        if (data.length != 13) {
            return null;
        }
        String type;
        String pItemSettingsKey;
        String durability;
        String xp;
        String points;
        String curLevel;
        String maxLevel;
        String availSkills;
        String purSkills;
        String curSkill;
        String blocksBroken;
        String aEnchants;
        String uuid;
        PItemType pItemType;
        PItemSettings pItemSettings;
        PItem<?> pItem = null;
        PEnchant clone;
        ItemStack temp;
        ItemStack cloneStack;
        Collection<PItemSettings> settingsCollection;
        type = data[0];
        pItemSettingsKey = data[1];
        durability = data[2];
        xp = data[3];
        points = data[4];
        curLevel = data[5];
        maxLevel = data[6];
        availSkills = data[7];
        purSkills = data[8];
        curSkill = data[9];
        blocksBroken = data[10];
        aEnchants = data[11];
        uuid = data[12];

        pItemType = PItemType.valueOf(type);
        if (pItemSettingsKey == null || pItemSettingsKey.equals("")) {
            return null;
        }
        settingsCollection = plugin.getPItemManager().getSettings(pItemSettingsKey);
        if (settingsCollection == null) {
            return null;
        }
        pItemSettings = null;
        for (PItemSettings ps : settingsCollection) {
            if (pItemType == ps.getType()) {
                pItemSettings = ps;
            }
        }
        if (pItemSettings == null) {
            return null;
        }
        pItem = null;
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
        if (plugin.getPItemManager().getPItemMap().containsKey(uuid)) {
            return null;
        }
        pItem.setUuid(UUID.fromString(uuid));
        pItem.getItem().setDurability(Short.parseShort(durability));

        pItem.setXp(Integer.parseInt(xp));
        pItem.setPoints(Integer.parseInt(points));
        pItem.setLevel(pItemSettings.getLevel(Integer.parseInt(curLevel)));
        pItem.setMaxLevel(pItemSettings.getLevel(Integer.parseInt(maxLevel)));

        if (availSkills != null && !availSkills.equals("")) {
            String[] skillSplit = availSkills.split("-");
            for (String key : skillSplit) {
                pItem.addAvailableSkill(plugin.getPSkillManager().getPSkill(key));
            }
        }
        for (PSkill pSkill : pItemSettings.getSkillList()) {
            if (!pItem.getAvailableSkills().contains(pSkill)) {
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
                    pEnchant.setStartLevel(pItemSettings.getEnchant(pEnchant.getName()).getStartLevel());
                }
            }
        }
        for (PEnchant pEnchant : pItemSettings.getEnchantList()) {
            if (pItem.getEnchant(pEnchant.getName()) == null) {
                clone = pEnchant.cloneEnchant();
                clone.setLevel(pEnchant.getLevel());
                clone.setMaxLevel(pEnchant.getMaxLevel());
                pItem.addEnchant(clone);
            }
        }
        temp = pItem.getItem();
        cloneStack = new ItemStack(temp.getType(), temp.getAmount());
        cloneStack.setData(temp.getData());
        cloneStack.setDurability(temp.getDurability());
        pItem.setItem(cloneStack);

        EnhancedPicks.getInstance().getPItemManager().addPItem(pItem);
        return pItem;
    }

    public static <E extends Event> List<String> base64PItemsNoRemove(List<PItem<E>> pItems) throws UnsupportedEncodingException {
        if (pItems == null || pItems.isEmpty()) {
            return null;
        }
        List<String> builder = new ArrayList<>();
        for (PItem<E> pItem : pItems) {
            builder.add(pItem.toString());
//            EnhancedPicks.getInstance().getPItemManager().getPItemMap().remove(pItem.getUuid().toString());
        }
        return builder;
    }

    public static <E extends Event> List<String> base64PItems(List<PItem<E>> pItems) throws UnsupportedEncodingException {
        if (pItems == null || pItems.isEmpty()) {
            return null;
        }
        List<String> builder = new ArrayList<>();
        for (PItem<E> pItem : pItems) {
            builder.add(pItem.toString());
            EnhancedPicks.getInstance().getPItemManager().getPItemMap().remove(pItem.getUuid().toString());
        }
        return builder;
    }

    public static List<PItem<?>> pItemsBase64(String base64) throws UnsupportedEncodingException {
        if (base64 == null || base64.equals("")) {
            return Collections.emptyList();
        }
        EnhancedPicks plugin = EnhancedPicks.getInstance();
        List<PItem<?>> pItemList = new ArrayList<>();
        String decoded = base64;
        String[] items = decoded.split(";");
        for (String item : items) {
            if (item.endsWith(";")) {
                item = item.substring(0, item.length() - 2);
            }
            String[] data = item.split(",");
            if (data.length != 13) {
                return null;
            }
            String type;
            String pItemSettingsKey;
            String durability;
            String xp;
            String points;
            String curLevel;
            String maxLevel;
            String availSkills;
            String purSkills;
            String curSkill;
            String blocksBroken;
            String aEnchants;
            String uuid;
            PItemType pItemType;
            PItemSettings pItemSettings;
            PItem<?> pItem;
            PEnchant clone;
            ItemStack temp;
            ItemStack cloneStack;
            Collection<PItemSettings> settingsCollection;
            type = data[0];
            pItemSettingsKey = data[1];
            durability = data[2];
            xp = data[3];
            points = data[4];
            curLevel = data[5];
            maxLevel = data[6];
            availSkills = data[7];
            purSkills = data[8];
            curSkill = data[9];
            blocksBroken = data[10];
            aEnchants = data[11];
            uuid = data[12];

            pItemType = PItemType.valueOf(type);
            if (pItemSettingsKey == null || pItemSettingsKey.equals("")) {
                continue;
            }
            settingsCollection = plugin.getPItemManager().getSettings(pItemSettingsKey);
            if (settingsCollection == null) {
                continue;
            }
            pItemSettings = null;
            for (PItemSettings ps : settingsCollection) {
                if (pItemType == ps.getType()) {
                    pItemSettings = ps;
                }
            }
            if (pItemSettings == null) {
                continue;
            }
            pItem = null;
            switch (pItemType) {
                case PICK:
                    pItem = pItemSettings.generate(BlockBreakEvent.class);
                    break;
                case SWORD:
                    pItem = pItemSettings.generate(EntityDamageByEntityEvent.class);
                    break;
            }
            if (pItem == null) {
                continue;
            }
            if (plugin.getPItemManager().getPItemMap().containsKey(uuid)) {
                continue;
            }
            pItem.setUuid(UUID.fromString(uuid));
            pItem.getItem().setDurability(Short.parseShort(durability));

            pItem.setXp(Integer.parseInt(xp));
            pItem.setPoints(Integer.parseInt(points));
            pItem.setLevel(pItemSettings.getLevel(Integer.parseInt(curLevel)));
            pItem.setMaxLevel(pItemSettings.getLevel(Integer.parseInt(maxLevel)));

            if (availSkills != null && !availSkills.equals("")) {
                String[] skillSplit = availSkills.split("-");
                for (String key : skillSplit) {
                    pItem.addAvailableSkill(plugin.getPSkillManager().getPSkill(key));
                }
            }
            for (PSkill pSkill : pItemSettings.getSkillList()) {
                if (!pItem.getAvailableSkills().contains(pSkill)) {
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
                        pEnchant.setStartLevel(pItemSettings.getEnchant(pEnchant.getName()).getStartLevel());
                    }
                }
            }
            for (PEnchant pEnchant : pItemSettings.getEnchantList()) {
                if (pItem.getEnchant(pEnchant.getName()) == null) {
                    clone = pEnchant.cloneEnchant();
                    clone.setLevel(pEnchant.getLevel());
                    clone.setMaxLevel(pEnchant.getMaxLevel());
                    pItem.addEnchant(clone);
                }
            }
            temp = pItem.getItem();
            cloneStack = new ItemStack(temp.getType(), temp.getAmount());
            cloneStack.setData(temp.getData());
            cloneStack.setDurability(temp.getDurability());
            pItem.setItem(cloneStack);

            pItemList.add(pItem);
            EnhancedPicks.getInstance().getPItemManager().addPItem(pItem);
        }
        return pItemList;
    }
}
