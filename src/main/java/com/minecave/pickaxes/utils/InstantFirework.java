package com.minecave.pickaxes.utils;

import net.minecraft.server.v1_8_R3.EntityFireworks;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 * @author Tim [calebbfmv]
 */
public class InstantFirework extends EntityFireworks {

    private Player[] players;
    private boolean gone = false;

    public InstantFirework(World world, Player... p) {
        super(world);
        this.players = p;
        this.a(0.25F, 0.25F);
    }


    @Override
    public boolean s_() {
        if (gone) {
            return false;
        }
        if (!this.world.isClientSide) {
            this.gone = true;

            if (players != null) {
                if (players.length > 0) {
                    for (Player player : players) {
                        (((CraftPlayer) player).getHandle()).playerConnection.sendPacket(new PacketPlayOutEntityStatus(this, (byte) 17));
                    }
                } else {
                    world.broadcastEntityEffect(this, (byte) 17);
                }
                this.die();
            }
        }
        return false;
    }

    public static void spawn(Location location, FireworkEffect effect, Player... players) {
        try {
            InstantFirework firework = new InstantFirework(((CraftWorld) location.getWorld()).getHandle(), players);
            FireworkMeta meta = ((Firework) firework.getBukkitEntity()).getFireworkMeta();
            meta.addEffect(effect);
            ((Firework) firework.getBukkitEntity()).setFireworkMeta(meta);
            firework.setPosition(location.getX(), location.getY(), location.getZ());

            if ((((CraftWorld) location.getWorld()).getHandle()).addEntity(firework)) {
                firework.setInvisible(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

