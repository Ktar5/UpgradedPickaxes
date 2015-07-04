package com.minecave.pickaxes.listener;

import com.minecave.minesell.nms.ShopVillager_v1_8_R3;
import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.drops.MobValue;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.enchant.enchants.LuckEnchant;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.item.PItemType;
import com.minecave.pickaxes.player.PlayerInfo;
import com.minecave.pickaxes.skill.sword.Acid;
import com.minecave.pickaxes.skill.sword.Rage;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.List;
import java.util.UUID;

/**
 * @author Timothy Andis
 */
public class PlayerListener implements Listener {

    private EnhancedPicks plugin = EnhancedPicks.getInstance();

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = plugin.getPlayerManager().add(player);
        info.load();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPSkillManager().getPSkill(Acid.class).removePlayer(player);
        plugin.getPSkillManager().getPSkill(Rage.class).removePlayer(player);
        PlayerInfo info = plugin.getPlayerManager().get(player);
        info.save();
        plugin.getPlayerManager().remove(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void creatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Endermite) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onProjectileHit(EntityDamageByEntityEvent event) {
        Entity nonDamager = event.getEntity();
        if (event.getDamager() instanceof Player && !(event.getEntity() instanceof Player)) {
            //rage
            Player player = (Player) event.getDamager();
            if (plugin.getPSkillManager().getPSkill(Rage.class).getRagePlayers().contains(player)) {
                if (event.getEntity() instanceof LivingEntity &&
                        !(((CraftEntity) nonDamager).getHandle() instanceof ShopVillager_v1_8_R3)) {
                    LivingEntity le = (LivingEntity) event.getEntity();
                    le.setMetadata("player", new FixedMetadataValue(plugin, player.getUniqueId().toString()));
                    le.damage(le.getMaxHealth() * 2, player);
                }
            }
            return;
        }
        if (event.getEntity() instanceof Player) {
            if (event.getDamager() instanceof Arrow &&
                    event.getDamager().getCustomName() != null &&
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
                if (entity.getCustomName() != null &&
                        entity.getCustomName().equals("shotgun") &&
                        !(((CraftEntity) nonDamager).getHandle() instanceof ShopVillager_v1_8_R3)) {
                    event.getEntity().setMetadata("player", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
                    event.setDamage(((LivingEntity) event.getEntity()).getMaxHealth() * 2);
                }
            } else if (event.getDamager() instanceof EnderPearl) {
                EnderPearl entity = (EnderPearl) event.getDamager();
                if (entity.getCustomName() != null &&
                        entity.getCustomName().equals("acid") &&
                        !(((CraftEntity) nonDamager).getHandle() instanceof ShopVillager_v1_8_R3)) {
                    event.getEntity().setMetadata("player", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
                    LivingEntity le = (LivingEntity) event.getEntity();
                    le.damage(le.getMaxHealth() * 2, (Player) entity.getShooter());
                    event.setDamage(0);
                }
            } else if (event.getDamager() instanceof Fireball) {
                Fireball entity = (Fireball) event.getDamager();
                if (entity.getCustomName() != null &&
                        entity.getCustomName().equals("fireball")) {
                    entity.getNearbyEntities(3, 3, 3).stream()
                            .filter(e -> !(e instanceof Player) &&
                                    !(((CraftEntity) nonDamager).getHandle() instanceof ShopVillager_v1_8_R3) && e instanceof LivingEntity)
                            .forEach(e -> {
                                LivingEntity le = (LivingEntity) e;
                                le.setMetadata("player", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
                                le.damage(((LivingEntity) e).getMaxHealth() * 2, (Player) entity.getShooter());
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
                        if (entity.getCustomName() != null &&
                                entity.getCustomName().equals("rain")) {
                            event.getEntity().setMetadata("player", new FixedMetadataValue(plugin,
                                    ((Player) entity.getShooter()).getUniqueId().toString()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (event.getEntity().hasMetadata("player")) {
            List<MetadataValue> m = event.getEntity().getMetadata("player");
            for (MetadataValue mv : m) {
                if (mv.value() instanceof String) {
                    String s = mv.asString();
                    Player player = Bukkit.getPlayer(UUID.fromString(s));
                    if (player != null) {
                        PItem<?> pItem = plugin.getPItemManager().getPItem(player.getItemInHand());
                        if (pItem != null) {
                            if(pItem.getEClass() != EntityDamageByEntityEvent.class ||
                               pItem.getType() != PItemType.SWORD) {
                                return;
                            }
                            int xp = MobValue.getXp(event.getEntity());
                            pItem.incrementXp(xp, player);
                            PEnchant luck = pItem.getEnchant("luck");
                            if(luck != null && luck instanceof LuckEnchant) {
                                ((LuckEnchant) luck).activate(player);
                            }
                            pItem.update(player);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onProjectileLand(ProjectileHitEvent event) {
        if (event.getEntity() instanceof EnderPearl) {
            EnderPearl entity = (EnderPearl) event.getEntity();
            if (entity.getCustomName() != null &&
                    entity.getCustomName().equals("acid") || entity.hasMetadata("acid")) {
                int radius = plugin.getPSkillManager().getPSkill(Acid.class).getRadius();
                entity.getNearbyEntities(radius, radius, radius).stream()
                        .filter(e -> !(e instanceof Player) &&
                                !(((CraftEntity) e).getHandle() instanceof ShopVillager_v1_8_R3) && e instanceof LivingEntity)
                        .forEach(e -> {
                            LivingEntity le = (LivingEntity) e;
                            le.setMetadata("player", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
                            le.damage(((LivingEntity) e).getMaxHealth() * 2, (Player) entity.getShooter());
                        });
            }
        }
        if (event.getEntity() instanceof Fireball) {
            Fireball entity = (Fireball) event.getEntity();
            if (entity.getCustomName() != null &&
                    entity.getCustomName().equals("fireball")) {
                entity.getNearbyEntities(3, 3, 3).stream()
                        .filter(e -> !(e instanceof Player) &&
                                !(((CraftEntity) e).getHandle() instanceof ShopVillager_v1_8_R3) && e instanceof LivingEntity)
                        .forEach(e -> {
                            LivingEntity le = (LivingEntity) e;
                            le.setMetadata("player", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
                            le.damage(((LivingEntity) e).getMaxHealth() * 2, (Player) entity.getShooter());
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
            if (entity.getCustomName() != null &&
                    entity.getCustomName().equals("rain")) {
                Bukkit.getScheduler().runTaskLater(EnhancedPicks.getInstance(), entity::remove, 5L);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if (plugin.getPSkillManager().getPSkill(Acid.class).hasPlayer(event.getPlayer())) {
                event.setCancelled(true);
                return;
            }
            PItem<?> pItem = plugin.getPItemManager().getPItem(event.getPlayer().getItemInHand());
            if (pItem != null) {
                event.setCancelled(true);
            }
        }
    }
}
