package com.minecave.pickaxes.listener;

import com.minecave.dropparty.custom.IDable;
import com.minecave.minesell.nms.ShopVillager_v1_8_R3;
import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.drops.MobValue;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.enchant.enchants.LuckEnchant;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.item.PItemType;
import com.minecave.pickaxes.player.PlayerInfo;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.skill.sword.Acid;
import com.minecave.pickaxes.skill.sword.Rage;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
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
        if (!player.hasPlayedBefore()) {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), String.format("pitem pick starter %s", player.getName()));
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), String.format("pitem sword starter %s", player.getName()));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPSkillManager().getPSkill(Acid.class).removePlayer(player);
        plugin.getPSkillManager().getPSkill(Rage.class).removePlayer(player);
        PlayerInfo info = plugin.getPlayerManager().add(player);
        info.save();
        plugin.getPlayerManager().remove(player);
        plugin.getDebugger().remove(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void creatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.ENDERMITE) {
            event.setCancelled(true);
            event.getEntity().remove();
        }
    }

//    @EventHandler
//    public void onDrop(PlayerDropItemEvent event) {
//        if(plugin.getPItemManager().getPItem(event.getItemDrop().getItemStack()) != null) {
//            event.setCancelled(true);
//        }
//    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProjectileHit(EntityDamageByEntityEvent event) {
        Entity nonDamager = event.getEntity();
        if (event.getDamager() instanceof Player && !(event.getEntity() instanceof Player)) {
            //rage
            Player player = (Player) event.getDamager();
            ItemStack inHand = player.getItemInHand();
            PItem pitem = plugin.getPItemManager().getPItem(inHand);
            if (pitem == null) {
                return;
            }
            if (plugin.getPSkillManager().getPSkill(Rage.class).getRagePlayers().contains(player) &&
                pitem.getCurrentSkill() instanceof Rage) {
                if (event.getEntity() instanceof LivingEntity &&
                    !(((CraftEntity) nonDamager).getHandle() instanceof ShopVillager_v1_8_R3)) {
                    LivingEntity le = (LivingEntity) event.getEntity();
                    le.removeMetadata("pitemplayer", EnhancedPicks.getInstance());
                    le.setMetadata("pitemplayer", new FixedMetadataValue(plugin, player.getUniqueId().toString()));
                    if (event.isCancelled()) {
                        event.setCancelled(false);
                    }
                    event.setDamage(plugin.getDamageFromSkill());
//                    le.setMetadata("LastDamageNameauta", new FixedMetadataValue(plugin, player.getName()));
//                    le.damage(le.getMaxHealth() * 10, player);
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
            if (event.getDamager() instanceof Fireball &&
                event.getDamager().getCustomName() != null &&
                event.getDamager().getCustomName().equals("fireball")) {
                event.setCancelled(true);
                event.setDamage(0);
            }
            return;
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE &&
            event.getEntity() instanceof LivingEntity) {
            event.setCancelled(true);
            event.setDamage(0);
            if (event.getDamager() instanceof Snowball) {
                Snowball entity = (Snowball) event.getDamager();
                if (entity.getCustomName() != null &&
                    entity.getCustomName().equals("shotgun") &&
                    !(((CraftEntity) nonDamager).getHandle() instanceof ShopVillager_v1_8_R3)) {
                    LivingEntity le = (LivingEntity) event.getEntity();
                    le.removeMetadata("pitemplayer", EnhancedPicks.getInstance());
                    le.setMetadata("pitemplayer", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
//                    le.damage(plugin.getDamageFromSkill(), (Player) entity.getShooter());
                    if (event.isCancelled()) {
                        event.setCancelled(false);
                    }
                    event.setDamage(plugin.getDamageFromSkill());
//                    le.setMetadata("LastDamageNameauta", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getName()));
                    plugin.getDebugger().debugMessage((Player) entity.getShooter(), String.format("Shotgun | Health: %f, Entity: %s",
                                                                                                  ((LivingEntity) event.getEntity()).getHealth(), event.getEntity().getType().name()));
                }
            } else if (event.getDamager() instanceof EnderPearl) {
                EnderPearl entity = (EnderPearl) event.getDamager();
                if (entity.getCustomName() != null &&
                    entity.getCustomName().equals("acid") &&
                    !(((CraftEntity) nonDamager).getHandle() instanceof ShopVillager_v1_8_R3)) {
                    LivingEntity le = (LivingEntity) event.getEntity();
                    le.removeMetadata("pitemplayer", EnhancedPicks.getInstance());
                    le.setMetadata("pitemplayer", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
                    if (event.isCancelled()) {
                        event.setCancelled(false);
                    }
                    event.setDamage(plugin.getDamageFromSkill());

//                    le.setMetadata("LastDamageNameauta", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getName()));
//                    le.damage(plugin.getDamageFromSkill(), (Player) entity.getShooter());
                    plugin.getDebugger().debugMessage((Player) entity.getShooter(), String.format("Acid | Health: %f, Entity: %s", le.getHealth(), le.getType().name()));
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
                              le.removeMetadata("pitemplayer", EnhancedPicks.getInstance());
                              le.setMetadata("pitemplayer", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
//                              le.damage(plugin.getDamageFromSkill(), (Player) entity.getShooter());
                              if (event.isCancelled()) {
                                  event.setCancelled(false);
                              }
                              event.setDamage(plugin.getDamageFromSkill());
//                              le.setMetadata("LastDamageNameauta", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getName()));
                              plugin.getDebugger().debugMessage((Player) entity.getShooter(), String.format("Fireball | Health: %f, Entity: %s", le.getHealth(), le.getType().name()));
                          });
                    Location location = entity.getLocation();
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.playSound(location, Sound.EXPLODE, 1.0F, 1.0F);
                        player.playEffect(location, Effect.LARGE_SMOKE, 1);
                    });
                }
            } else if (event.getDamager() instanceof Arrow) {
//                if (event.getFinalDamage() >= ((LivingEntity) event.getEntity()).getHealth()) {
                Arrow entity = (Arrow) event.getDamager();
//                if (entity.getShooter() instanceof Player) {
                if (entity.getCustomName() != null &&
                    entity.getCustomName().equals("rain") || entity.hasMetadata("rain")) {
                    Entity e = event.getEntity();
                    if (!(e instanceof Player) &&
                        !(((CraftEntity) e).getHandle() instanceof ShopVillager_v1_8_R3) &&
                        e instanceof LivingEntity) {
                        LivingEntity le = (LivingEntity) e;
                        le.removeMetadata("pitemplayer", EnhancedPicks.getInstance());
                        le.setMetadata("pitemplayer", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
//                        le.damage(plugin.getDamageFromSkill(), (Player) entity.getShooter());
                        if (event.isCancelled()) {
                            event.setCancelled(false);
                        }
                        event.setDamage(plugin.getDamageFromSkill());
//                        le.setMetadata("LastDamageNameauta", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getName()));
                        plugin.getDebugger().debugMessage((Player) entity.getShooter(), String.format("Rain | Health: %f, Entity: %s", le.getHealth(), le.getType().name()));
                    }
                }
//                }
//                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (event.getEntity().hasMetadata("pitemplayer")) {
            for (MetadataValue mv : event.getEntity().getMetadata("pitemplayer")) {
                if (mv.value() instanceof String) {
                    String s = mv.asString();
                    if (s != null) {
//                plugin.getDebugger().debugBroadcast("Player | Death | " + s);
                        Player player = Bukkit.getPlayer(UUID.fromString(s));
                        if (player != null) {
                            if (!player.isOnline()) {
                                plugin.getDebugger().debugBroadcast("Player " + player.getName() + " not online.");
                                return;
                            }
                            ItemStack item = player.getItemInHand();
                            plugin.getDebugger().debugMessage(player, "You are tagged with entity | " + entity.getType());
                            plugin.getDebugger().debugMessage(player, "Item | Enchants | " + Arrays.toString(item.getEnchantments().keySet().toArray()));
                            PItem<?> pItem = plugin.getPItemManager().getPItem(item);
                            if (pItem != null) {
                                if (pItem.getType() != PItemType.SWORD || item.getType() != Material.DIAMOND_SWORD) {
                                    plugin.getDebugger().debugBroadcast(player.getName() + " failed to have a PITEM in hand.");
                                    return;
                                }
                                if (((CraftEntity) entity).getHandle() instanceof IDable) {
                                    int xp = MobValue.getXp(event.getEntity());
                                    pItem.incrementXp(xp, player);
                                    pItem.update(player);
                                    plugin.getDebugger().debugMessage(player,
                                                                      String.format("Death | IDable | Damage: %f, Player: %s",
                                                                                    event.getEntity().getLastDamage(),
                                                                                    player.getUniqueId().toString()));
                                    return;
                                }
                                event.setDroppedExp(0);
                                final List<ItemStack> items = new ArrayList<>();
                                items.addAll(event.getDrops());

                                plugin.getDebugger().debugMessage(player, event.getDrops().toString());
                                event.getDrops().clear();
                                plugin.getDebugger().debugMessage(player, "About to enter task");
                                plugin.getDebugger().debugMessage(player, "Firing fortune + xp task");
                                if (item.getEnchantments().containsKey(Enchantment.LOOT_BONUS_MOBS)) {
                                    final int factor = item.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
                                    plugin.getDebugger().debugMessage(player, "Item | Factor | " + factor);
                                    plugin.getDebugger().debugMessage(player, "Item Count | " + items.size());
                                    for (final ItemStack i : items) {
                                        i.setAmount(i.getAmount() + PSkill.itemsDropped(factor));
                                    }
                                }
                                for (ItemStack i : items) {
                                    player.getInventory().addItem(i);
                                }
                                items.clear();
                                player.updateInventory();
                                int xp = MobValue.getXp(event.getEntity());
                                pItem.incrementXp(xp, player);
                                PEnchant luck = pItem.getEnchant("luck");
                                if (luck != null && luck instanceof LuckEnchant) {
                                    ((LuckEnchant) luck).activate(player);
                                }
                                pItem.update(player);
                                plugin.getDebugger().debugMessage(player,
                                                                  String.format("Death | Damage: %f, Player: %s",
                                                                                event.getEntity().getLastDamage(),
                                                                                player.getUniqueId().toString()));
                            } else {
                                plugin.getDebugger().debugMessage(player, "You don't have a valid PITEM in hand.");
                                plugin.getDebugger().debugMessage(player, "PItem Type | " + player.getItemInHand().getType().toString());
                                plugin.getDebugger().debugMessage(player, "PItem UUID | " + plugin.getPItemManager().pullUUID(player.getItemInHand()));
                            }
                        } else {
                            plugin.getDebugger().debugBroadcast("Player | Non-existant | " + s);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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
                          le.removeMetadata("pitemplayer", EnhancedPicks.getInstance());
                          le.setMetadata("pitemplayer", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
//                          le.setMetadata("LastDamageNameauta", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getName()));
                          le.damage(plugin.getDamageFromSkill(), entity);
//                          event.setDamage(le.getMaxHealth() * 10);
                          plugin.getDebugger().debugMessage((Player) entity.getShooter(), String.format("Acid | Health: %f, Entity: %s", le.getHealth(), le.getType().name()));
                      });
                entity.remove();
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
                          le.removeMetadata("pitemplayer", EnhancedPicks.getInstance());
                          le.setMetadata("pitemplayer", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
//                          le.setMetadata("LastDamageNameauta", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getName()));
                          le.damage(plugin.getDamageFromSkill(), entity);
                          plugin.getDebugger().debugMessage((Player) entity.getShooter(), String.format("Fireball | Health: %f, Entity: %s", le.getHealth(), le.getType().name()));
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
                entity.getCustomName().equals("rain") || entity.hasMetadata("rain")) {
                entity.getNearbyEntities(1, 1, 1).stream()
                      .filter(e -> !(e instanceof Player) &&
                                   !(((CraftEntity) e).getHandle() instanceof ShopVillager_v1_8_R3) &&
                                   e instanceof LivingEntity).forEach(e -> {
                    LivingEntity le = (LivingEntity) e;
                    le.removeMetadata("pitemplayer", EnhancedPicks.getInstance());
                    le.setMetadata("pitemplayer", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getUniqueId().toString()));
//                    le.setMetadata("LastDamageNameauta", new FixedMetadataValue(plugin, ((Player) entity.getShooter()).getName()));
                    le.damage(plugin.getDamageFromSkill(), entity);
                    plugin.getDebugger().debugMessage((Player) entity.getShooter(), String.format("Rain | Health: %f, Entity: %s", le.getHealth(), le.getType().name()));
                });
                entity.remove();
            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
//            if (plugin.getPSkillManager().getPSkill(Acid.class).hasPlayer(event.getPlayer())) {
            event.setCancelled(true);
            PItem<?> pItem = plugin.getPItemManager().getPItem(event.getPlayer().getItemInHand());
            if (pItem != null) {
                event.setCancelled(true);
            }
        }
//        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onExplosion(EntityExplodeEvent event) {
        if (event.getEntityType() == EntityType.FIREBALL) {
            Fireball entity = (Fireball) event.getEntity();
            if (entity.getCustomName() != null &&
                entity.getCustomName().equals("fireball")) {
                event.setYield(0);
            }
        }
    }
}
