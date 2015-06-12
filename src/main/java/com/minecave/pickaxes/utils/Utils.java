package com.minecave.pickaxes.utils;

import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.items.ItemBuilder;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.menu.Menu;
import com.minecave.pickaxes.menu.Menu.FillerButton;
import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.pitem.Sword;
import com.minecave.pickaxes.skill.Skill;
import com.minecave.pickaxes.skill.Skills;
import com.minecave.pickaxes.utils.nbt.AttributeStorage;
import org.bukkit.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author Tim [calebbfmv]
 */
public class Utils {

    public static Menu.FillerButton BLACK;
    public static FillerButton RED;
    public static FillerButton GREEN;
    public static FillerButton BLUE;
    public static FillerButton DENY;

    private Utils() {
    }

    static {
        BLACK = new FillerButton(ItemBuilder.wrap(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData())).name(" ").build());
        RED = new FillerButton(ItemBuilder.wrap(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.RED.getWoolData())).name(" ").build());
        GREEN = new FillerButton(ItemBuilder.wrap(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.GREEN.getWoolData())).name(" ").build());
        BLUE = new FillerButton(ItemBuilder.wrap(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLUE.getWoolData())).name(" ").build());
        DENY = new FillerButton(ItemBuilder.wrap(new ItemStack(Material.REDSTONE_BLOCK, 1)).name(ChatColor.DARK_RED + "You cannot afford this!").build());
    }

    public static String serialze(List<String> buttons) {
        StringBuilder builder = new StringBuilder();
        for (String button : buttons) {
            builder.append(button).append(",");
        }
        return builder.toString();
    }

    public static List<String> deserialize(String base) {
        List<String> list = new ArrayList<>();
        String[] str = base.split(",");
        Collections.addAll(list, str);
        return list;
    }

    public static byte[] serialSwords(List<Sword> inventory) {
        ItemStack[] items = new ItemStack[inventory.size()];
        int i = 0;
        for (Sword p : inventory) {
            ItemStack item = p.getItemStack();
            Skill skill = p.getSkill();
            int points = p.getPoints();
            int level = p.getLevel().getId();
            int xp = p.getXp();
            Map<PEnchant, Integer> enchantMap = new HashMap<>();
            for (PEnchant enchant : p.getEnchants().values()) {
                enchantMap.put(enchant, enchant.getLevel());
            }
            AttributeStorage storage;
            storage = AttributeStorage.newTarget(item, UUIDs.getUUIDFromString("skill"));
            storage.setData(skill.getName());
            storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("name"));
            storage.setData(p.getName());
            storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("points"));
            storage.setData(String.valueOf(points));
            storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("level"));
            storage.setData(String.valueOf(level));
            storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("experience"));
            storage.setData(String.valueOf(xp));
            for (Map.Entry<PEnchant, Integer> entry : enchantMap.entrySet()) {
                storage = AttributeStorage.newTarget(storage.getTarget(),
                        UUIDs.getUUIDFromString(entry.getKey().getTrueName()));
                storage.setData(String.valueOf(entry.getValue()));
            }
            for(Map.Entry<String, Skill> entry : Skills.skills.entrySet()) {
                if(!p.getPurchasedSkills().contains(entry.getValue())) {
                    continue;
                }
                storage = AttributeStorage.newTarget(storage.getTarget(),
                        UUIDs.getUUIDFromString("purchased_" + entry.getKey()));
                storage.setData(entry.getKey());
            }
            items[i++] = storage.getTarget();
        }
        return ItemSerialization.toBlob(ItemSerialization.getInventoryFromArray(items));
    }

    public static byte[] serialPicks(List<Pickaxe> inventory) {
        ItemStack[] items = new ItemStack[inventory.size()];
        int i = 0;
        for (Pickaxe p : inventory) {
            ItemStack item = p.getItemStack();
            Skill skill = p.getSkill();
            int blocks = p.getBlocksBroken();
            int points = p.getPoints();
            int level = p.getLevel().getId();
            int xp = p.getXp();
            Map<PEnchant, Integer> enchantMap = new HashMap<>();
            for (PEnchant enchant : p.getEnchants().values()) {
                enchantMap.put(enchant, enchant.getLevel());
            }
            AttributeStorage storage;
            storage = AttributeStorage.newTarget(item, UUIDs.getUUIDFromString("skill"));
            storage.setData(skill.getName());
            storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("name"));
            storage.setData(p.getName());
            storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("blocks"));
            storage.setData(String.valueOf(blocks));
            storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("points"));
            storage.setData(String.valueOf(points));
            storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("level"));
            storage.setData(String.valueOf(level));
            storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("experience"));
            storage.setData(String.valueOf(xp));
            for (Map.Entry<PEnchant, Integer> entry : enchantMap.entrySet()) {
                storage = AttributeStorage.newTarget(storage.getTarget(),
                        UUIDs.getUUIDFromString(entry.getKey().getTrueName()));
                storage.setData(String.valueOf(entry.getValue()));
            }
            for(Map.Entry<String, Skill> entry : Skills.skills.entrySet()) {
                if(!p.getPurchasedSkills().contains(entry.getValue())) {
                    continue;
                }
                storage = AttributeStorage.newTarget(storage.getTarget(),
                        UUIDs.getUUIDFromString("purchased_" + entry.getKey()));
                storage.setData(entry.getKey());
            }
            items[i++] = storage.getTarget();
        }
        return ItemSerialization.toBlob(ItemSerialization.getInventoryFromArray(items));
    }

    public static Pickaxe deserializePick(ItemStack item) {
        AttributeStorage storage = AttributeStorage.newTarget(item, UUIDs.getUUIDFromString("name"));
        String name = storage.getData("name");
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("skill"));
        Skill skill = Skills.getSkill(storage.getData(null));
        Pickaxe pick = new Pickaxe(item, Level.ONE, 0, name, skill);
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("blocks"));
        pick.setBlocksBroken(Integer.parseInt(storage.getData("0")));
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("points"));
        pick.setPoints(Integer.parseInt(storage.getData("0")));
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("level"));
        pick.setLevel(Integer.parseInt(storage.getData("1")));
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("experience"));
        pick.setXP(Integer.parseInt(storage.getData("0")));
        for (PEnchant enchant : pick.getEnchants().values()) {
            storage = AttributeStorage.newTarget(storage.getTarget(),
                    UUIDs.getUUIDFromString(enchant.getTrueName()));
            enchant.setLevel(Integer.parseInt(storage.getData("0")));
        }
        for(Map.Entry<String, Skill> entry : Skills.skills.entrySet()) {
            storage = AttributeStorage.newTarget(storage.getTarget(),
                    UUIDs.getUUIDFromString("purchased_" + entry.getKey()));
            String s = storage.getData(null);
            if(s != null && s.equals(entry.getKey())) {
                pick.getPurchasedSkills().add(entry.getValue());
            }
        }
        return pick;
    }

    public static Sword deserializeSword(ItemStack item) {
        AttributeStorage storage = AttributeStorage.newTarget(item, UUIDs.getUUIDFromString("name"));
        String name = storage.getData("name");
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("skill"));
        Skill skill = Skills.getSkill(storage.getData(null));
        Sword sword = new Sword(item, Level.ONE, 0, name, skill);
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("points"));
        sword.setPoints(Integer.parseInt(storage.getData("0")));
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("level"));
        sword.setLevel(Integer.parseInt(storage.getData("1")));
        storage = AttributeStorage.newTarget(storage.getTarget(), UUIDs.getUUIDFromString("experience"));
        sword.setXP(Integer.parseInt(storage.getData("0")));
        for (PEnchant enchant : sword.getEnchants().values()) {
            storage = AttributeStorage.newTarget(storage.getTarget(),
                    UUIDs.getUUIDFromString(enchant.getTrueName()));
            enchant.setLevel(Integer.parseInt(storage.getData("0")));
        }
        for(Map.Entry<String, Skill> entry : Skills.skills.entrySet()) {
            storage = AttributeStorage.newTarget(storage.getTarget(),
                    UUIDs.getUUIDFromString("purchased_" + entry.getKey()));
            String s = storage.getData(null);
            if(s != null && s.equals(entry.getKey())) {
                sword.getPurchasedSkills().add(entry.getValue());
            }
        }
        return sword;
    }

    public static Inventory deserializeInventory(byte[] data) {
        return ItemSerialization.fromBlob(data);
    }


    public static String friendlyName(String name) {
        StringBuilder builder = new StringBuilder();
        if (!name.contains("_")) {
            builder.append(name.substring(0, 1).toUpperCase()).append(name.substring(1).toLowerCase());
        } else {
            String[] str = name.split("_");
            for (int i = 0; i < str.length; i++) {
                String s = str[i];
                builder.append(s.substring(0, 1).toUpperCase()).append(s.substring(1).toLowerCase());
                if ((i + 1) != str.length) {
                    builder.append(" ");
                }
            }
        }
        return builder.toString();

    }

    public static String locToString(Location location) {
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "," + location.getYaw() + "," + location.getPitch();
    }

    public static Location locFromString(String s) {
        if (s == null) {
            return null;
        }
        String[] str = s.split(",");
        World world = Bukkit.getWorld(str[0]);
        int x = parse(str[1]);
        int y = parse(str[2]);
        int z = parse(str[3]);
        float yaw = Float.parseFloat(str[4]);
        float pitch = Float.parseFloat(str[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public static int parse(String s) {
        return Integer.parseInt(s);
    }

    public static List<Location> circle(Location loc, Integer r, Integer h, Boolean hollow, Boolean sphere, int plus_y) {
        List<Location> circleblocks = new ArrayList<>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx + r; x++)
            for (int z = cz - r; z <= cz + r; z++)
                for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                }

        return circleblocks;
    }

    public static String ordinal(int i) {
        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + suffixes[i % 10];

        }
    }

    public static String toRoman(int i) {
        StringBuilder builder = new StringBuilder();
        while (i >= 1000) {
            builder.append("M");
            i -= 1000;
        }
        while (i >= 900) {
            builder.append("CM");
            i -= 900;
        }
        while (i >= 500) {
            builder.append("D");
            i -= 500;
        }
        while (i >= 400) {
            builder.append("CD");
            i -= 400;
        }
        while (i >= 100) {
            builder.append("C");
            i -= 100;
        }
        while (i >= 90) {
            builder.append("XC");
            i = -90;
        }
        while (i >= 50) {
            builder.append("L");
            i -= 50;
        }
        while (i >= 40) {
            builder.append("XL");
            i -= 40;
        }
        while (i >= 10) {
            builder.append("X");
            i -= 10;
        }
        while (i >= 9) {
            builder.append("IX");
            i -= 9;
        }
        while (i >= 5) {
            builder.append("V");
            i -= 5;
        }
        while (i >= 4) {
            builder.append("IV");
            i -= 4;
        }
        while (i >= 1) {
            builder.append("I");
            i--;
        }
        return builder.toString();
    }

}
