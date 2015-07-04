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
        return Base64.getEncoder().encodeToString(pItem.toString().getBytes("utf-8"));
    }

    public static String base64PItemWUUID(PItem<?> pItem) throws UnsupportedEncodingException {
        if (pItem == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(pItem.toStringWUUID().getBytes("utf-8"));
    }


    public static PItem<?> singlePItemBase64(String base64) throws UnsupportedEncodingException {
        if (base64 == null || base64.equals("")) {
            return null;
        }
        EnhancedPicks plugin = EnhancedPicks.getInstance();
        String decoded = new String(Base64.getDecoder().decode(base64), "utf-8");
        if(decoded.endsWith(";")) {
            decoded = decoded.substring(0, decoded.length() - 2);
        }
        String[] data = decoded.split(",");
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
        switch (data.length) {
            case 12:
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

                pItemType = PItemType.valueOf(type);
                if (pItemSettingsKey == null || pItemSettingsKey.equals("")) {
                    return null;
                }
                Collection<PItemSettings> settingsCollection = plugin.getPItemManager().getSettings(pItemSettingsKey);
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
                break;
            case 13:
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
                if(plugin.getPItemManager().getPItemMap().containsKey(uuid)) {
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
                break;
        }
        return pItem;
    }

    public static PItem<?> singlePItemBase64WUUID(String base64) throws UnsupportedEncodingException {
        if (base64 == null || base64.equals("")) {
            return null;
        }
        EnhancedPicks plugin = EnhancedPicks.getInstance();
        String decoded = new String(Base64.getDecoder().decode(base64), "utf-8");
        String[] data = decoded.split(",");
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
        switch (data.length) {
            case 12:
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

                pItemType = PItemType.valueOf(type);
                if (pItemSettingsKey == null || pItemSettingsKey.equals("")) {
                    return null;
                }
                Collection<PItemSettings> settingsCollection = plugin.getPItemManager().getSettings(pItemSettingsKey);
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
                break;
            case 13:
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
                break;
        }
        return pItem;
    }


    public static String base64PItemsWUUID(Collection<PItem<?>> pItems) throws UnsupportedEncodingException {
        if (pItems == null || pItems.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder("");
        for (PItem<?> pItem : pItems) {
            builder.append(pItem.toStringWUUID());
            EnhancedPicks.getInstance().getPItemManager().getPItemMap().remove(pItem.getUuid().toString());
        }
        return Base64.getEncoder().encodeToString(builder.toString().getBytes("utf-8"));
    }

    public static <E extends Event> String base64PItems(List<PItem<E>> pItems) throws UnsupportedEncodingException {
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
            if(item.endsWith(";")) {
                item = item.substring(0, item.length() - 2);
            }
            String[] data = item.split(",");
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
            switch (data.length) {
                case 12:
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

                    pItemType = PItemType.valueOf(type);
                    if (pItemSettingsKey == null || pItemSettingsKey.equals("")) {
                        continue;
                    }
                    Collection<PItemSettings> settingsCollection = plugin.getPItemManager().getSettings(pItemSettingsKey);
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
                    break;
                case 13:
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
                    if(plugin.getPItemManager().getPItemMap().containsKey(uuid)) {
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
                    break;
            }
        }
        return pItemList;
    }

    public static List<PItem<?>> pItemsBase64WUUID(String base64) throws UnsupportedEncodingException {
        if (base64 == null || base64.equals("")) {
            return Collections.emptyList();
        }
        EnhancedPicks plugin = EnhancedPicks.getInstance();
        List<PItem<?>> pItemList = new ArrayList<>();
        String decoded = new String(Base64.getDecoder().decode(base64), "utf-8");
        String[] items = decoded.split(";");
        for (String item : items) {
            String[] data = item.split(",");
            if (data.length != 13) {
                plugin.getLogger().warning("Base64 String contains invalid serialized PItem.");
                continue;
            }
            String uuid = data[0];
            String type = data[1];
            String pItemSettingsKey = data[2];
            String durability = data[3];
            String xp = data[4];
            String points = data[5];
            String curLevel = data[6];
            String maxLevel = data[7];
            String availSkills = data[8];
            String purSkills = data[9];
            String curSkill = data[10];
            String blocksBroken = data[11];
            String aEnchants = data[12];
            if (plugin.getPItemManager().getPItemMap().containsKey(uuid)) {
                continue;
            }

            PItemType pItemType = PItemType.valueOf(type);
            if (pItemSettingsKey == null || pItemSettingsKey.equals("")) {
                continue;
            }
            Collection<PItemSettings> settingsCollection = plugin.getPItemManager().getSettings(pItemSettingsKey);
            if (settingsCollection == null) {
                continue;
            }
            PItemSettings pItemSettings = null;
            for (PItemSettings ps : settingsCollection) {
                if (pItemType == ps.getType()) {
                    pItemSettings = ps;
                }
            }
            if (pItemSettings == null) {
                continue;
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
