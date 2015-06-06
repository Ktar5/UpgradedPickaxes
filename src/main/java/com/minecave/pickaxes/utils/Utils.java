package com.minecave.pickaxes.utils;

import com.minecave.pickaxes.items.ItemBuilder;
import com.minecave.pickaxes.menu.Menu;
import com.minecave.pickaxes.menu.Menu.FillerButton;
import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.pitem.Sword;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public static String serialSwords(List<Sword> inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            for (int i = 0; i < inventory.size(); i++) {
                DataItem dataItem = new DataItem(i, inventory.get(i));
                dataOutput.writeObject(dataItem);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static String serialPicks(List<Pickaxe> inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            for (int i = 0; i < inventory.size(); i++) {
                DataItem dataItem = new DataItem(i, inventory.get(i));
                dataOutput.writeObject(dataItem);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    public static DataItem[] deserializeInventory(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            List<DataItem> items = new ArrayList<>();

            while (dataInput.readObject() != null) {
                items.add((DataItem) dataInput.readObject());
            }

            dataInput.close();
            return items.toArray(new DataItem[items.size()]);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        return null;
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
        StringBuilder builder = new StringBuilder();
        builder.append(location.getWorld().getName()).append(",").append(location.getBlockX()).append(",").append(location.getBlockY()).append(",").append(location.getBlockZ()).append(",").append(location.getYaw()).append(",").append(location.getPitch());
        return builder.toString();
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
        while(i >= 1000) {
            builder.append("M");
            i -= 1000;
        }
        while(i >= 900) {
            builder.append("CM");
            i -= 900;
        }
        while(i >= 500) {
            builder.append("D");
            i -= 500;
        }
        while(i >= 400) {
            builder.append("CD");
            i -= 400;
        }
        while(i >= 100) {
            builder.append("C");
            i -= 100;
        }
        while(i >= 90) {
            builder.append("XC");
            i =- 90;
        }
        while(i >= 50) {
            builder.append("L");
            i -= 50;
        }
        while(i >= 40) {
            builder.append("XL");
            i -= 40;
        }
        while(i >= 10) {
            builder.append("X");
            i -= 10;
        }
        while(i >= 9) {
            builder.append("IX");
            i -= 9;
        }
        while(i >= 5) {
            builder.append("V");
            i-= 5;
        }
        while(i >= 4) {
            builder.append("IV");
            i -= 4;
        }
        while(i >= 1) {
            builder.append("I");
            i--;
        }
        return builder.toString();
    }

}
