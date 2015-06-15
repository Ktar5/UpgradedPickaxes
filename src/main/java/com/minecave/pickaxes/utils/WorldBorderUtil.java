/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.utils;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketListener;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;

public class WorldBorderUtil {

    public static void sendRedScreen(Player player, int time) {
        PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder();
        packet.setAction(Action.INITIALIZE);
        packet.setWarningBlocks(60000000); //value taken from mc wiki
        packet.setRadius(60000000); //value taken from mc wiki
        packet.setX(player.getWorld().getSpawnLocation().getX());
        packet.setZ(player.getWorld().getSpawnLocation().getZ());
        packet.setOldradius(60000000); //value taken from mc wiki
        packet.setSpeed(0);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    private static void setCenter(Player player) {
        PacketPlayOutWorldBorder setcenter = new PacketPlayOutWorldBorder();
        setcenter.setAction(Action.SET_CENTER);
        setcenter.setX(player.getLocation().getX());
        setcenter.setZ(player.getLocation().getZ());
        CraftPlayer cp = (CraftPlayer) player;
        cp.getHandle().playerConnection.sendPacket(setcenter);
    }

    private static void setSize(Player player) {
        PacketPlayOutWorldBorder setsize = new PacketPlayOutWorldBorder();
        setsize.setAction(Action.SET_SIZE);
        setsize.setRadius(20000.0);
        CraftPlayer cp = (CraftPlayer) player;
        cp.getHandle().playerConnection.sendPacket(setsize);
    }

    public static void setWarningBlocks(Player player, int distance) {
        setCenter(player);
        setSize(player);
        PacketPlayOutWorldBorder setwarningblocks = new PacketPlayOutWorldBorder(Action.SET_WARNING_BLOCKS);
        setwarningblocks.setWarningBlocks(getDistance(distance));
        CraftPlayer cp = (CraftPlayer) player;
        cp.getHandle().playerConnection.sendPacket(setwarningblocks);
    }

    public static int getDistance(int redness) {
        switch (redness) {
            case 0:
                return 10000;
            case 1:
                return 15000;
            case 2:
                return 24000;
            case 3:
                return 29000;
            case 4:
                return 35000;
            case 5:
                return 40000;
            default:
                return 40000;
        }
    }

    @Getter
    @Setter
    public static class PacketPlayOutWorldBorder implements Packet {
        private Action action;
        private double radius = 0;
        private double oldradius = 0;
        private long speed = 0;
        private double x = 0;
        private double z = 0;
        private int warningTime = 0;
        private int warningBlocks = 0;
        private int portalBoundary = 0;

        public PacketPlayOutWorldBorder() {
        }

        public PacketPlayOutWorldBorder(Action action) {
            this.setAction(action);
        }

        public void a(PacketDataSerializer packetdataserializer) throws IOException {
            this.action = Action.values()[packetdataserializer.e()];
            switch (this.action) {
                case SET_SIZE:
                    this.radius = packetdataserializer.readDouble();
                    break;
                case LERP_SIZE:
                    this.oldradius = packetdataserializer.readDouble();
                    this.radius = packetdataserializer.readDouble();
                    this.speed = packetdataserializer.readLong();
                    break;
                case SET_CENTER:
                    this.x = packetdataserializer.readDouble();
                    this.z = packetdataserializer.readDouble();
                    break;
                case INITIALIZE:
                    this.x = packetdataserializer.readDouble();
                    this.z = packetdataserializer.readDouble();
                    this.oldradius = packetdataserializer.readDouble();
                    this.radius = packetdataserializer.readDouble();
                    this.speed = packetdataserializer.readLong();
                    this.portalBoundary = packetdataserializer.readInt();
                    this.warningTime = packetdataserializer.readInt();
                    this.warningBlocks = packetdataserializer.readInt();
                    break;
                case SET_WARNING_TIME:
                    this.warningTime = packetdataserializer.readInt();
                    break;
                case SET_WARNING_BLOCKS:
                    this.warningBlocks = packetdataserializer.readInt();
                    break;
                default:
                    break;
            }
        }

        public void b(PacketDataSerializer serializer) {
            serializer.b(this.action.ordinal());
            switch (action) {
                case SET_SIZE: {
                    serializer.writeDouble(this.radius);
                    break;
                }
                case LERP_SIZE: {
                    serializer.writeDouble(this.oldradius);
                    serializer.writeDouble(this.radius);
                    serializer.b((int) this.speed);
                    break;
                }
                case SET_CENTER: {
                    serializer.writeDouble(this.x);
                    serializer.writeDouble(this.z);
                    break;
                }
                case SET_WARNING_BLOCKS: {
                    serializer.b(this.warningBlocks);
                    break;
                }
                case SET_WARNING_TIME: {
                    serializer.b(this.warningTime);
                    break;
                }
                case INITIALIZE: {
                    serializer.writeDouble(this.x);
                    serializer.writeDouble(this.z);
                    serializer.writeDouble(this.oldradius);
                    serializer.writeDouble(this.radius);
                    serializer.b((int) this.speed);
                    serializer.b(this.portalBoundary);
                    serializer.b(this.warningBlocks);
                    serializer.b(this.warningTime);
                }
            }
        }

        @Override
        public void a(PacketListener packetListener) {

        }

        public void handle(PacketListener packetlistener) {
        }
    }

    public enum Action {
        SET_SIZE,
        LERP_SIZE,
        SET_CENTER,
        INITIALIZE,
        SET_WARNING_TIME,
        SET_WARNING_BLOCKS
    }
}
