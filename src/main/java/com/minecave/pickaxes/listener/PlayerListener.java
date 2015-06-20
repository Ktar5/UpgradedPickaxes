package com.minecave.pickaxes.listener;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.player.PlayerInfo;
import com.minecave.pickaxes.skill.sword.Acid;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Timothy Andis
 */
public class PlayerListener implements Listener {

    private EnhancedPicks plugin = EnhancedPicks.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = plugin.getPlayerManager().add(player);
        info.load();
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = plugin.getPlayerManager().get(player);
        info.save();
        plugin.getPlayerManager().remove(player);
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
                int radius = plugin.getPSkillManager().getSkill(Acid.class).getRadius();
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
