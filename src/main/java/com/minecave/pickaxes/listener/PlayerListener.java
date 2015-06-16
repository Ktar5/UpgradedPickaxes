package com.minecave.pickaxes.listener;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.pitem.Sword;
import com.minecave.pickaxes.sql.PlayerInfo;
import com.minecave.pickaxes.utils.Utils;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Timothy Andis
 */
public class PlayerListener implements Listener {

    private PickaxesRevamped plugin = PickaxesRevamped.getInstance();

    public PlayerListener() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getSqlManager().init(player);
        player.getInventory().forEach(this::tryGetPItem);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = PlayerInfo.get(player);
        if (info == null) {
            return;
        }
        for (int j = 0; j < player.getInventory().getContents().length; j++) {
            ItemStack i = player.getInventory().getItem(j);
            Pickaxe p = Pickaxe.tryFromItem(i);
            if(p == null) {
                Sword s = Sword.tryFromItem(i);
                if(s != null) {
                    player.getInventory().setItem(j, Utils.serializeSword(s));
                }
            } else {
                player.getInventory().setItem(j, Utils.serializePick(p));
            }
        }
        info.logOff();
    }

    public void tryGetPItem(ItemStack i) {
        Pickaxe p = Pickaxe.tryFromItem(i);
        if(p == null) {
            Sword.tryFromItem(i);
        }
    }

    @EventHandler
    public void onProjectileHit(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE &&
                !(event.getEntity() instanceof Player) &&
                event.getEntity() instanceof LivingEntity) {
            if (event.getDamager() instanceof Snowball) {
                Snowball entity = (Snowball) event.getDamager();
                if (entity.getCustomName().equals("shotgun")) {
                    event.setDamage(((LivingEntity) event.getEntity()).getMaxHealth());
                }
            } else if (event.getDamager() instanceof EnderPearl) {
                EnderPearl entity = (EnderPearl) event.getDamager();
                if (entity.getCustomName().equals("acid")) {
                    event.setDamage(((LivingEntity) event.getEntity()).getMaxHealth());
                }
            }
        }
    }

    @EventHandler
    public void onProjectileLand(ProjectileHitEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            EnderPearl entity = (EnderPearl) event.getEntity();
            if (entity.getCustomName().equals("acid")) {
                int radius = plugin.getConfigValues().getAcid().getRadius();
                entity.getNearbyEntities(radius, radius, radius).stream()
                        .filter(e -> !(e instanceof Player) && e instanceof LivingEntity)
                        .forEach(e -> {
                            LivingEntity le = (LivingEntity) e;
                            le.damage(le.getMaxHealth(), entity);
                        });
            }
        }
        if(event.getEntity() instanceof Fireball) {
            Fireball entity = (Fireball) event.getEntity();
            if(entity.getCustomName().equals("fireball")) {
                entity.getWorld().createExplosion(entity.getLocation(), 2.0f, false);
            }
        }
    }
}
