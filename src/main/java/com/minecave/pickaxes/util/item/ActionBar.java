/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.item;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ActionBar {

    private static String nmsVersion;

    static {
        nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);
    }

    /*
    copy pasted from some actionbar api lol
     */
    public static void sendActionBar(Player player, String message) {
        try {
            Class<?> c1 = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
            Object p = c1.cast(player);
            Object ppoc = null;
            Class<?> c4 = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
            Class<?> c5 = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
            if (nmsVersion.equalsIgnoreCase("v1_8_R1") || !nmsVersion.startsWith("v1_8_")) {
                Class<?> c2 = Class.forName("net.minecraft.server." + nmsVersion + ".ChatSerializer");
                Class<?> c3 = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");
                Method m3 = c2.getDeclaredMethod("a", String.class);
                Object cbc = c3.cast(m3.invoke(c2, "{\"text\": \"" + message + "\"}"));
                ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(cbc, (byte) 2);
            } else {
                Class<?> c2 = Class.forName("net.minecraft.server." + nmsVersion + ".ChatComponentText");
                Class<?> c3 = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");
                Object o = c2.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                ppoc = c4.getConstructor(new Class<?>[]{c3, byte.class}).newInstance(o, (byte) 2);
            }
            Method m1 = c1.getDeclaredMethod("getHandle");
            Object h = m1.invoke(p);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m5.invoke(pc, ppoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
