/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.pitem;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.Skill;
import com.minecave.pickaxes.skill.Skills;
import com.minecave.pickaxes.utils.ItemSerialization;
import com.minecave.pickaxes.utils.UUIDs;
import com.minecave.pickaxes.utils.nbt.AttributeStorage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PItemSerializer {

    public static Inventory deserializeInventory(byte[] data) {
        try {
            return ItemSerialization.fromBlob(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Bukkit.createInventory(null, 9);
    }

    public static byte[] serialPicks(List<Pickaxe> inventory) {
        if (inventory.isEmpty()) {
            return null;
        }
        ItemStack[] items = new ItemStack[inventory.size()];
        int i = 0;
        for (Pickaxe p : inventory) {
            items[i++] = serializePick(p);
        }
        return ItemSerialization.toBlob(ItemSerialization.getInventoryFromArray(items));
    }

    public static byte[] serialSwords(List<Sword> inventory) {
        if (inventory.isEmpty()) {
            return null;
        }
        ItemStack[] items = new ItemStack[inventory.size()];
        int i = 0;
        for (Sword p : inventory) {
            items[i++] = serializeSword(p);
        }
        return ItemSerialization.toBlob(ItemSerialization.getInventoryFromArray(items));
    }

    public static ItemStack serializeSword(Sword p) {
        ItemStack item = p.getItemStack();
        Skill skill = p.getSkill();
        int points = p.getPoints();
        int level = p.getLevel().getId();
        int xp = p.getXp();
        Map<PEnchant, String> enchantMap = new HashMap<>();
        for (PEnchant enchant : p.getEnchants().values()) {
            enchantMap.put(enchant, enchant.getLevel() + ";" + enchant.getMaxLevel());
        }
        StringBuilder enchantBuilder = new StringBuilder();
        enchantMap.keySet().forEach(e -> enchantBuilder.append(e.getTrueName()).append(";"));

        AttributeStorage storage;
        //current skill
        storage = AttributeStorage.newTarget(item, UUIDs.getUUIDFromString("skill"));
        storage.setData(skill == null ? "null" : skill.getName());
        //config name
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("name"));
        storage.setData(p.getPSettings());
        //current points
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("points"));
        storage.setData(String.valueOf(points));
        //current level
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("level"));
        storage.setData(String.valueOf(level));
        //current exp
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("experience"));
        storage.setData(String.valueOf(xp));
        //set the attached enchants
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("enchants"));
        storage.setData(enchantBuilder.toString());
        //set the enchants as well
        for (Map.Entry<PEnchant, String> entry : enchantMap.entrySet()) {
            storage = AttributeStorage.newTarget(storage.getTarget(),
                    UUIDs.getUUIDFromString(entry.getKey().getTrueName()));
            storage.setData(String.valueOf(entry.getValue()));
        }
        //purchased skills
        for (Map.Entry<String, Skill> entry : Skills.skills.entrySet()) {
            storage = AttributeStorage.newTarget(storage.getTarget(),
                    UUIDs.getUUIDFromString("purchased_" + entry.getKey()));
            storage.setData(String.valueOf(p.getPurchasedSkills().contains(entry.getValue())));
        }
        return storage.getTarget();
    }

    public static ItemStack serializePick(Pickaxe p) {
        ItemStack item = p.getItemStack();
        Skill skill = p.getSkill();
        int blocks = p.getBlocksBroken();
        int points = p.getPoints();
        int level = p.getLevel().getId();
        int xp = p.getXp();
        Map<PEnchant, String> enchantMap = new HashMap<>();
        for (PEnchant enchant : p.getEnchants().values()) {
            enchantMap.put(enchant, enchant.getLevel() + ";" + enchant.getMaxLevel());
        }
        StringBuilder enchantBuilder = new StringBuilder();
        enchantMap.keySet().forEach(e -> enchantBuilder.append(e.getTrueName()).append(";"));

        AttributeStorage storage;
        //current skill
        storage = AttributeStorage.newTarget(item, UUIDs.getUUIDFromString("skill"));
        storage.setData(skill == null ? "null" : skill.getName());
        //config name
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("name"));
        storage.setData(p.getPSettings());
        //current points
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("points"));
        storage.setData(String.valueOf(points));
        //blocks broken
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("blocks"));
        storage.setData(String.valueOf(blocks));
        //current level
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("level"));
        storage.setData(String.valueOf(level));
        //current exp
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("experience"));
        storage.setData(String.valueOf(xp));
        //set the attached enchants
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("enchants"));
        storage.setData(enchantBuilder.toString());
        //set the enchants as well
        for (Map.Entry<PEnchant, String> entry : enchantMap.entrySet()) {
            storage = AttributeStorage.newTarget(storage.getTarget(),
                    UUIDs.getUUIDFromString(entry.getKey().getTrueName()));
            storage.setData(String.valueOf(entry.getValue()));
        }
        //purchased skills
        for (Map.Entry<String, Skill> entry : Skills.skills.entrySet()) {
            storage = AttributeStorage.newTarget(storage.getTarget(),
                    UUIDs.getUUIDFromString("purchased_" + entry.getKey()));
            storage.setData(String.valueOf(p.getPurchasedSkills().contains(entry.getValue())));
        }
        return storage.getTarget();
    }

    public static Pickaxe deserializePick(ItemStack item) {
        //config name
        AttributeStorage storage = AttributeStorage.newTarget(item, UUIDs.getUUIDFromString("name"));
        String name = storage.getData("name");
        if (!PickaxesRevamped.getInstance().getPItemCreator().has(name)) {
            return new Pickaxe(item, Level.ONE, 0, name, null);
        }

        PItemCreator.PItemSettings settings = PickaxesRevamped.getInstance().getPItemCreator().get(name);
        //generate item
        Pickaxe pick = settings.generate(storage.getTarget(), Pickaxe.class);

        //current skill
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("skill"));
        String skillName = storage.getData(null);
        Skill skill = Skills.getSkill(skillName);

        System.out.println("deserialize " + pick.getName());
        //set current skill
        pick.setSkill(skill);
        //blocks broken
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("blocks"));
        pick.setBlocksBroken(Integer.parseInt(storage.getData("0")));
        //points
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("points"));
        pick.setPoints(Integer.parseInt(storage.getData("0")));
        //level
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("level"));
        pick.setLevel(Integer.parseInt(storage.getData("1")));
        //exp
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("experience"));
        pick.setXp(Integer.parseInt(storage.getData("0")));
        //available enchants
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("enchants"));
        String enchants = storage.getData("");
        String[] enchantArray = enchants.split(";");
        //enchants
        for (String s : enchantArray) {
            PEnchant enchant = PItem.getEnchantMap().get(s);
            if (enchant == null) {
                continue;
            }
            enchant = enchant.cloneEnchant();
            storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString(s));
            String enchantLevels = storage.getData("0;" + enchant.getMaxLevel());
            String[] levels = enchantLevels.split(";");
            enchant.setLevel(Integer.parseInt(levels[0]));
            enchant.setMaxLevel(Integer.parseInt(levels[1]));
            pick.addEnchant(enchant);
        }
        //skills
        for (Map.Entry<String, Skill> entry : Skills.skills.entrySet()) {
            storage = AttributeStorage.newTarget(storage.getTarget(),
                    UUIDs.getUUIDFromString("purchased_" + entry.getKey()));
            String s = storage.getData(null);
            if (s != null && Boolean.parseBoolean(s)) {
                if (entry.getKey().equalsIgnoreCase(skillName)) {
                    pick.setSkill(entry.getValue());
                }
                pick.getPurchasedSkills().add(entry.getValue());
            }
        }
        return pick;
    }

    public static Sword deserializeSword(ItemStack item) {
        //config name
        AttributeStorage storage = AttributeStorage.newTarget(item, UUIDs.getUUIDFromString("name"));
        String name = storage.getData("name");
        if (!PickaxesRevamped.getInstance().getPItemCreator().has(name)) {
            return new Sword(item, Level.ONE, 0, name, null);
        }

        PItemCreator.PItemSettings settings = PickaxesRevamped.getInstance().getPItemCreator().get(name);
        //generate item
        Sword sword = settings.generate(storage.getTarget(), Sword.class);

        //current skill
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("skill"));
        String skillName = storage.getData(null);
        Skill skill = Skills.getSkill(skillName);
        //set current skill
        sword.setSkill(skill);
        //points
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("points"));
        sword.setPoints(Integer.parseInt(storage.getData("0")));
        //level
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("level"));
        sword.setLevel(Integer.parseInt(storage.getData("1")));
        //exp
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("experience"));
        sword.setXp(Integer.parseInt(storage.getData("0")));
        //available enchants
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("enchants"));
        String enchants = storage.getData("");
        String[] enchantArray = enchants.split(";");
        //enchants
        for (String s : enchantArray) {
            PEnchant enchant = PItem.getEnchantMap().get(s);
            if (enchant == null) {
                continue;
            }
            enchant = enchant.cloneEnchant();
            storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString(s));
            String enchantLevels = storage.getData("0;" + enchant.getMaxLevel());
            String[] levels = enchantLevels.split(";");
            enchant.setLevel(Integer.parseInt(levels[0]));
            enchant.setMaxLevel(Integer.parseInt(levels[1]));
            sword.addEnchant(enchant);
        }
        //skills
        for (Map.Entry<String, Skill> entry : Skills.skills.entrySet()) {
            storage = AttributeStorage.newTarget(storage.getTarget(),
                    UUIDs.getUUIDFromString("purchased_" + entry.getKey()));
            String s = storage.getData(null);
            if (s != null && Boolean.parseBoolean(s)) {
                if (entry.getKey().equalsIgnoreCase(skillName)) {
                    sword.setSkill(entry.getValue());
                }
                sword.getPurchasedSkills().add(entry.getValue());
            }
        }
        return sword;
    }
}
