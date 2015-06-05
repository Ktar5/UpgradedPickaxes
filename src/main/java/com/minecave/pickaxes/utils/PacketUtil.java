package com.minecave.pickaxes.utils;

import net.minecraft.server.v1_8_R1.*;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/**
 * Creation Date: 19, 08, 2014
 * Programmed for the BaseCore project.
 */
public class PacketUtil {

    private PacketUtil() {
    }

    public static void sendTitleToPlayer(Player player, String text, String subtitle) {
        EntityPlayer p = ((CraftPlayer) player).getHandle();

        if (text != null) {
            p.playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, makeComponent(text)));
        }

        if (subtitle != null) {
            p.playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, makeComponent(text)));
        }
    }

    private static Field bField;

    private static PacketPlayOutPlayerListHeaderFooter getHeaderFooterPacket(IChatBaseComponent header, IChatBaseComponent footer) {
        PacketPlayOutPlayerListHeaderFooter headerFooter = new PacketPlayOutPlayerListHeaderFooter(header);
        try {
            if (bField == null) {
                bField = headerFooter.getClass().getDeclaredField("b");
                bField.setAccessible(true);
            }
            bField.set(headerFooter, footer);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return headerFooter;
    }

    public static void sendTabToPlayer(Player player, String header, String footer) {
        EntityPlayer p = ((CraftPlayer) player).getHandle();
        PacketPlayOutPlayerListHeaderFooter packet = getHeaderFooterPacket(makeComponent(header), makeComponent(footer));
        p.playerConnection.sendPacket(packet);
    }

    public static void sendActionBarMessage(Player player, String message) {
        EntityPlayer p = ((CraftPlayer) player).getHandle();

        PacketPlayOutChat packet = new PacketPlayOutChat(makeComponent(message), (byte) 2);

        p.playerConnection.sendPacket(packet);
    }

    public static IChatBaseComponent makeComponent(String text) {
        return ChatSerializer.a(convert(text));
    }

    public static String convert(String text) {
        if (text == null || text.length() == 0) {
            return "\"\"";
        }

        char c;
        int i;
        int len = text.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            c = text.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '/':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    if (c < ' ') {
                        t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }

}

