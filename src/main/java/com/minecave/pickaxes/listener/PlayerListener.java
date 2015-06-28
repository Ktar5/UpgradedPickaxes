package com.minecave.pickaxes.listener;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.drops.MobValue;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.player.PlayerInfo;
import com.minecave.pickaxes.skill.sword.Acid;
import com.minecave.pickaxes.skill.sword.Rage;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.UUID;

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
        if(event.getDamager() instanceof Player && !(event.getEntity() instanceof Player)) {
            //rage
            Player player = (Player) event.getDamager();
            if(plugin.getPSkillManager().getPSkill(Rage.class).getRagePlayers().contains(player)) {
                if(event.getEntity() instanceof LivingEntity) {
                    LivingEntity le = (LivingEntity) event.getEntity();
                    le.setMetadata("player", new FixedMetadataValue(plugin, player.getUniqueId().toString()));
                    le.damage(le.getMaxHealth() * 2);
                }
            }
            return;
        }
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Arrow &&
                    event.getDamager().getCustomName().equals("rain")) {
                event.setCancelled(true);
                event.setDamage(0);
            }
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE &&
                !(event.getEntity() instanceof Player) &&
                event.getEntity() instanceof LivingEntity) {
            if (event.getDamager() instanceof Snowball) {
                Snowball entity = (Snowball) event.getDamager();
                if (entity.getCustomName().equals("shotgun")) {
                    event.getEntity().setMetadata("player", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
                    event.setDamage(((LivingEntity) event.getEntity()).getMaxHealth() * 2);
                }
            } else if (event.getDamager() instanceof EnderPearl) {
                EnderPearl entity = (EnderPearl) event.getDamager();
                if (entity.getCustomName().equals("acid")) {
                    event.getEntity().setMetadata("player", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
                    event.setDamage(((LivingEntity) event.getEntity()).getMaxHealth() * 2);
                }
            } else if (event.getDamager() instanceof Fireball) {
                Fireball entity = (Fireball) event.getDamager();
                if (entity.getCustomName().equals("fireball")) {
                    entity.getNearbyEntities(3, 3, 3).stream()
                            .filter(e -> !(e instanceof Player) && e instanceof LivingEntity)
                            .forEach(e -> {
                                LivingEntity le = (LivingEntity) e;
                                le.setMetadata("player", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
                                le.damage(((LivingEntity) e).getMaxHealth() * 2);
                            });
                    Location location = entity.getLocation();
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.playSound(location, Sound.EXPLODE, 1.0F, 1.0F);
                        player.playEffect(location, Effect.LARGE_SMOKE, 1);
                    });
                }
            } else if (event.getDamager() instanceof Arrow) {
                if (event.getFinalDamage() >= ((LivingEntity) event.getEntity()).getHealth()) {
                    Arrow entity = (Arrow) event.getDamager();
                    if (entity.getShooter() instanceof Player) {
                        if (entity.getCustomName().equals("rain")) {
                            event.getEntity().setMetadata("player", new FixedMetadataValue(plugin,
                                    ((Player) entity.getShooter()).getUniqueId().toString()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (event.getEntity().hasMetadata("player")) {
            List<MetadataValue> m = event.getEntity().getMetadata("player");
            for (MetadataValue mv : m) {
                if (mv.value() instanceof String) {
                    String s = mv.asString();
                    Player player = Bukkit.getPlayer(UUID.fromString(s));
                    if (player != null) {
                        System.out.println(player.getName() + " " + player.getUniqueId());
                        player.getInventory().addItem(event.getDrops().toArray(new ItemStack[event.getDrops().size()]));
                        PItem<?> pItem = plugin.getPItemManager().getPItem(player.getItemInHand());
                        if (pItem != null) {
                            int xp = MobValue.getXp(event.getEntityType());
                            pItem.incrementXp(xp, player);
                        }
                        event.getDrops().clear();
                        event.getEntity().remove();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileLand(ProjectileHitEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            EnderPearl entity = (EnderPearl) event.getEntity();
            if (entity.getCustomName().equals("acid") || entity.hasMetadata("acid")) {
                int radius = plugin.getPSkillManager().getPSkill(Acid.class).getRadius();
                entity.getNearbyEntities(radius, radius, radius).stream()
                        .filter(e -> !(e instanceof Player) && e instanceof LivingEntity)
                        .forEach(e -> {
                            LivingEntity le = (LivingEntity) e;
                            le.setMetadata("player", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
                            le.damage(((LivingEntity) e).getMaxHealth() * 2);
                        });
            }
        }
        if (event.getEntity() instanceof Fireball) {
            Fireball entity = (Fireball) event.getEntity();
            if (entity.getCustomName().equals("fireball")) {
                entity.getNearbyEntities(3, 3, 3).stream()
                        .filter(e -> !(e instanceof Player) && e instanceof LivingEntity)
                        .forEach(e -> {
                            LivingEntity le = (LivingEntity) e;
                            le.setMetadata("player", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
                            le.damage(((LivingEntity) e).getMaxHealth() * 2);
                        });
                Location location = entity.getLocation();
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.playSound(location, Sound.EXPLODE, 1.0F, 1.0F);
                    player.playEffect(location, Effect.LARGE_SMOKE, 1);
                });
            }
        }
        if (event.getEntity() instanceof Arrow) {
            Arrow entity = (Arrow) event.getEntity();
            if (entity.getCustomName().equals("rain")) {
                Bukkit.getScheduler().runTaskLater(EnhancedPicks.getInstance(), entity::remove, 5L);
            }
        }
    }

    @EventHandler
    public void onEntityTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            PItem<?> pItem = plugin.getPItemManager().getPItem(event.getPlayer().getItemInHand());
            if (pItem != null) {
                event.setCancelled(true);
            }
        }
    }
}
