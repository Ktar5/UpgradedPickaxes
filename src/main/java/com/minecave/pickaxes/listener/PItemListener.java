package com.minecave.pickaxes.listener;

import com.earth2me.essentials.User;
import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.item.PItemSettings;
import com.minecave.pickaxes.item.PItemType;
import com.minecave.pickaxes.kit.Kit;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.skill.pick.Earthquake;
import com.minecave.pickaxes.skill.pick.Nuker;
import com.minecave.pickaxes.util.message.Strings;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Map;

/**
 * @author Timothy Andis
 */
public class PItemListener implements Listener {

    private EnhancedPicks plugin = EnhancedPicks.getInstance();
    private String           pickChest;
    private String           swordChest;
    private String           errorMessage;
    private WorldGuardPlugin wg;

    public PItemListener() {
        pickChest = ChatColor.stripColor(Strings.color(plugin.getConfig("menus").get("pickaxeMenu", String.class, "Pickaxe Menu")));
        swordChest = ChatColor.stripColor(Strings.color(plugin.getConfig("menus").get("swordMenu", String.class, "Sword Menu")));
        errorMessage = Strings.color(plugin.getConfig("config").get("chest-pitem-error", String.class, ""));
        wg = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
    }

    @EventHandler
    public void consoleCommand(ServerCommandEvent event) {
        if (!(event.getSender() instanceof ConsoleCommandSender)) {
            return;
        }
        String message = event.getCommand();
        if (message.toLowerCase().startsWith("kit")) {
            //kit message
            String[] split = message.split(" ");
            if (split.length <= 2) {
                return;
            }
            Player player = Bukkit.getPlayer(split[2]);
            if (player == null || !player.isOnline()) {
                return;
            }
            if (plugin.getKitManager().getKitMap().containsKey(split[1].toLowerCase())) {
                Kit kit = plugin.getKitManager().getKitMap().get(split[1].toLowerCase());
                User user = plugin.getEssentials().getUser(player);
                if (kit == null || user == null) {
                    return;
                }
                try {
//                    if(!player.hasPermission("essentials.kit")) {
//                        return;
//                    }
//                    com.earth2me.essentials.Kit essKit = new com.earth2me.essentials.Kit(split[1], plugin.getEssentials());
//                    try {
//                        essKit.checkPerms(user);
//                    } catch(Exception e) {
//                        return;
//                    }
//                    if (essKit.getNextUse(user) != 0) {
//                        return;
//                    }
                    Collection<PItemSettings> settings = plugin.getPItemManager().getSettings(kit.getPSettingsKey());
                    if (settings != null && !settings.isEmpty()) {
                        if (kit.isPick()) {
                            for (PItemSettings setting : settings) {
                                if (setting.getType() == PItemType.PICK) {
                                    PItem<BlockBreakEvent> pItem = setting.generate(BlockBreakEvent.class);
                                    ItemStack stack = pItem.getItem();
                                    pItem.updateManually(player, stack);
                                    for (PEnchant pEnchant : pItem.getEnchants()) {
                                        pEnchant.apply(pItem);
                                    }
                                    Map<Integer, ItemStack> fail = player.getInventory().addItem(stack);
                                    if(fail != null && !fail.isEmpty()) {
                                        fail.values().forEach(i -> player.getWorld().dropItem(player.getLocation(), i));
                                    }
                                    plugin.getPItemManager().addPItem(pItem);
                                    break;
                                }
                            }
                        }
                        if (kit.isSword()) {
                            for (PItemSettings setting : settings) {
                                if (setting.getType() == PItemType.SWORD) {
                                    PItem<EntityDamageByEntityEvent> pItem = setting.generate(EntityDamageByEntityEvent.class);
                                    ItemStack stack = pItem.getItem();
                                    pItem.updateManually(player, stack);
                                    for (PEnchant pEnchant : pItem.getEnchants()) {
                                        pEnchant.apply(pItem);
                                    }
                                    Map<Integer, ItemStack> fail = player.getInventory().addItem(stack);
                                    if(fail != null && !fail.isEmpty()) {
                                        fail.values().forEach(i -> player.getWorld().dropItem(player.getLocation(), i));
                                    }
                                    plugin.getPItemManager().addPItem(pItem);
                                    break;
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @EventHandler
    public void preprocessCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        if (message.toLowerCase().startsWith("/kit")) {
            //kit message
            String[] split = message.split(" ");
            if (split.length <= 1) {
                return;
            }
            if (split.length == 2) {
                if (plugin.getKitManager().getKitMap().containsKey(split[1].toLowerCase())) {
                    Kit kit = plugin.getKitManager().getKitMap().get(split[1].toLowerCase());
                    User user = plugin.getEssentials().getUser(player);
                    if (kit == null || user == null) {
                        return;
                    }
                    try {
                        if (!player.hasPermission("essentials.kit")) {
                            return;
                        }
                        com.earth2me.essentials.Kit essKit = new com.earth2me.essentials.Kit(split[1].toLowerCase(), plugin.getEssentials());
                        try {
                            essKit.checkPerms(user);
                        } catch (Exception e) {
                            return;
                        }
                        if (essKit.getNextUse(user) != 0) {
                            return;
                        }
                        Collection<PItemSettings> settings = plugin.getPItemManager().getSettings(kit.getPSettingsKey());
                        if (settings != null && !settings.isEmpty()) {
                            if (kit.isPick()) {
                                for (PItemSettings setting : settings) {
                                    if (setting.getType() == PItemType.PICK) {
                                        PItem<BlockBreakEvent> pItem = setting.generate(BlockBreakEvent.class);
                                        ItemStack stack = pItem.getItem();
                                        pItem.updateManually(player, stack);
                                        for (PEnchant pEnchant : pItem.getEnchants()) {
                                            pEnchant.apply(pItem);
                                        }
                                        Map<Integer, ItemStack> fail = player.getInventory().addItem(stack);
                                        if(fail != null && !fail.isEmpty()) {
                                            final Player finalPlayer = player;
                                            fail.values().forEach(i -> finalPlayer.getWorld().dropItem(finalPlayer.getLocation(), i));
                                        }
                                        plugin.getPItemManager().addPItem(pItem);
                                        break;
                                    }
                                }
                            }
                            if (kit.isSword()) {
                                for (PItemSettings setting : settings) {
                                    if (setting.getType() == PItemType.SWORD) {
                                        PItem<EntityDamageByEntityEvent> pItem = setting.generate(EntityDamageByEntityEvent.class);
                                        ItemStack stack = pItem.getItem();
                                        pItem.updateManually(player, stack);
                                        for (PEnchant pEnchant : pItem.getEnchants()) {
                                            pEnchant.apply(pItem);
                                        }
                                        Map<Integer, ItemStack> fail = player.getInventory().addItem(stack);
                                        if(fail != null && !fail.isEmpty()) {
                                            final Player finalPlayer1 = player;
                                            fail.values().forEach(i -> finalPlayer1.getWorld().dropItem(finalPlayer1.getLocation(), i));
                                        }
                                        plugin.getPItemManager().addPItem(pItem);
                                        break;
                                    }
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (split.length > 2) {
                player = Bukkit.getPlayer(split[2]);
                if (event.getPlayer().hasPermission("pickaxes.admin")) {
                    if (player == null || !player.isOnline()) {
                        return;
                    }
                    if (plugin.getKitManager().getKitMap().containsKey(split[1].toLowerCase())) {
                        Kit kit = plugin.getKitManager().getKitMap().get(split[1].toLowerCase());
                        User user = plugin.getEssentials().getUser(player);
                        if (kit == null || user == null) {
                            return;
                        }
                        try {
                            com.earth2me.essentials.Kit essKit = new com.earth2me.essentials.Kit(split[1].toLowerCase(), plugin.getEssentials());
                            Collection<PItemSettings> settings = plugin.getPItemManager().getSettings(kit.getPSettingsKey());
                            if (settings != null && !settings.isEmpty()) {
                                if (kit.isPick()) {
                                    for (PItemSettings setting : settings) {
                                        if (setting.getType() == PItemType.PICK) {
                                            PItem<BlockBreakEvent> pItem = setting.generate(BlockBreakEvent.class);
                                            ItemStack stack = pItem.getItem();
                                            pItem.updateManually(player, stack);
                                            for (PEnchant pEnchant : pItem.getEnchants()) {
                                                pEnchant.apply(pItem);
                                            }
                                            Map<Integer, ItemStack> fail = player.getInventory().addItem(stack);
                                            if(fail != null && !fail.isEmpty()) {
                                                final Player finalPlayer2 = player;
                                                fail.values().forEach(i -> finalPlayer2.getWorld().dropItem(finalPlayer2.getLocation(), i));
                                            }
                                            plugin.getPItemManager().addPItem(pItem);
                                            break;
                                        }
                                    }
                                }
                                if (kit.isSword()) {
                                    for (PItemSettings setting : settings) {
                                        if (setting.getType() == PItemType.SWORD) {
                                            PItem<EntityDamageByEntityEvent> pItem = setting.generate(EntityDamageByEntityEvent.class);
                                            ItemStack stack = pItem.getItem();
                                            pItem.updateManually(player, stack);
                                            for (PEnchant pEnchant : pItem.getEnchants()) {
                                                pEnchant.apply(pItem);
                                            }
                                            Map<Integer, ItemStack> fail = player.getInventory().addItem(stack);
                                            if(fail != null && !fail.isEmpty()) {
                                                final Player finalPlayer3 = player;
                                                fail.values().forEach(i -> finalPlayer3.getWorld().dropItem(finalPlayer3.getLocation(), i));
                                            }
                                            plugin.getPItemManager().addPItem(pItem);
                                            break;
                                        }
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (!plugin.getWhitelistWorlds().contains(event.getBlock().getWorld().getName())) {
            return;
        }
        if (event.getBlock().hasMetadata("skip")) {
            event.getBlock().removeMetadata("skip", EnhancedPicks.getInstance());
            return;
        }
        Player player = event.getPlayer();
        ItemStack inhand = player.getItemInHand();
        if (inhand == null || inhand.getType() != Material.DIAMOND_PICKAXE) {
            return;
        }
        Location loc = player.getLocation();
        if(!plugin.getBlacklistedRegions().isEmpty()) {
            for (ProtectedRegion protectedRegion : wg.getRegionManager(player.getWorld()).getApplicableRegions(loc)) {
                if(plugin.getBlacklistedRegions().contains(protectedRegion.getId())) {
                    return;
                }
            }
        }
        PItem<BlockBreakEvent> pItem = EnhancedPicks.getInstance().getPItemManager()
                                                    .getPItem(BlockBreakEvent.class, player.getItemInHand());
        if (pItem != null) {
            pItem.setItem(inhand);
            pItem.onAction(event);
            if (pItem.getCurrentSkill() != null &&
                pItem.getCurrentSkill() instanceof Nuker) {
                pItem.getCurrentSkill().onBreak(event);
            }
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onRightClick(PlayerInteractEvent event) {
        if (!plugin.getWhitelistWorlds().contains(event.getPlayer().getWorld().getName())) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
            event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack inhand = player.getItemInHand();
        if (inhand == null || inhand.getType() == Material.AIR) {
            return;
        }
        Location loc = player.getLocation();
        if(!plugin.getBlacklistedRegions().isEmpty()) {
            for (ProtectedRegion protectedRegion : wg.getRegionManager(player.getWorld()).getApplicableRegions(loc)) {
                if(plugin.getBlacklistedRegions().contains(protectedRegion.getId())) {
                    return;
                }
            }
        }
        PItem<?> pItem = null;
        if (inhand.getType() == PItemType.PICK.getType()) {
            pItem = EnhancedPicks.getInstance().getPItemManager()
                                 .getPItem(BlockBreakEvent.class, player.getItemInHand());
        } else if (inhand.getType() == PItemType.SWORD.getType()) {
            pItem = EnhancedPicks.getInstance().getPItemManager()
                                 .getPItem(EntityDamageByEntityEvent.class, player.getItemInHand());
        }
        if (pItem != null && pItem.getCurrentSkill() != null) {
            PSkill skill = pItem.getCurrentSkill();
            if (!skill.canUse(player, pItem)) {
                if (player.hasPermission(skill.getPerm())) {
                    player.sendMessage(ChatColor.RED + "You cannot use " + skill.getName() +
                                       " for another " + skill.getTimeLeft(player) + "s.");
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have permission for this skill.");
                }
            } else {
                skill.use(player, event);
            }
        }
    }

    //ent.setMetadata("skip",
    // new FixedMetadataValue(EnhancedPicks.getInstance(), true));
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamaage(EntityDamageByEntityEvent event) {
        if (!plugin.getWhitelistWorlds().contains(event.getDamager().getWorld().getName())) {
            return;
        }
        Entity entity = event.getEntity();
        Location loc = entity.getLocation();
        if(!plugin.getBlacklistedRegions().isEmpty()) {
            for (ProtectedRegion protectedRegion : wg.getRegionManager(loc.getWorld()).getApplicableRegions(loc)) {
                if(plugin.getBlacklistedRegions().contains(protectedRegion.getId())) {
                    return;
                }
            }
        }
        if (entity instanceof Player) {
            return;
        }
        if (entity.hasMetadata("skipTNT")) {
            entity.removeMetadata("skipTNT", EnhancedPicks.getInstance());
            return;
        }
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }
        Player player = (Player) damager;
        ItemStack inhand = player.getItemInHand();
        if (inhand == null || inhand.getType() == Material.AIR) {
            return;
        }
        PItem<EntityDamageByEntityEvent> pItem = EnhancedPicks.getInstance().getPItemManager()
                                                              .getPItem(EntityDamageByEntityEvent.class, player.getItemInHand());
        if (pItem != null) {
            pItem.onAction(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEarthquake(EntityChangeBlockEvent event) {
        if (event.getEntity() == null) {
            return;
        }
        if (event.getEntity() instanceof FallingBlock) {
            //TODO: earthquake give players item
            Block block = event.getBlock();
            FallingBlock fallingBlock = (FallingBlock) event.getEntity();
            Earthquake earthquake = ((Earthquake) plugin.getPSkillManager().getPSkill("earthquake"));
            if ((fallingBlock.getCustomName() != null &&
                 fallingBlock.getCustomName().contains("earthquake")) ||
                earthquake.getFallingBlockList().contains(fallingBlock)) {
                block.setType(Material.AIR);
                event.setCancelled(true);
                event.getEntity().remove();
                earthquake.getFallingBlockList().remove(fallingBlock);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHopperPickup(InventoryPickupItemEvent event) {
        ItemStack clicked = event.getItem().getItemStack();
        if (plugin.getPItemManager().getPItem(clicked) != null) {
            event.setCancelled(true);
            Vector v = event.getItem().getVelocity();
            v.setX(0);
            v.setY(0);
            v.setZ(0);
            event.getItem().setVelocity(v);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestPut(InventoryClickEvent event) {
        if (event.getWhoClicked().isOp() ||
            event.getWhoClicked().hasPermission("pickaxes.admin")) {
            return;
        }
        ItemStack clicked = event.getCurrentItem();
        if(event.getAction() == InventoryAction.HOTBAR_SWAP &&
           event.getClick() == ClickType.NUMBER_KEY) {
            clicked = event.getWhoClicked().getInventory()
                           .getItem(event.getHotbarButton());
        }
        if (event.getInventory().getTitle().toLowerCase().contains("vault")) {
            if (plugin.getPItemManager().getPItem(clicked) != null) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(errorMessage);
            }
            return;
        }
        if (event.getInventory().getType() == InventoryType.CHEST) {
            String stripped = ChatColor.stripColor(event.getInventory().getTitle());
            if (!stripped.equals(pickChest) && !stripped.equals(swordChest)) {
                if (plugin.getPItemManager().getPItem(clicked) != null) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(errorMessage);
                }
            }
        } else {
            if (event.getInventory().getType() != InventoryType.PLAYER &&
                event.getInventory().getType() != InventoryType.CRAFTING) {
                if (plugin.getPItemManager().getPItem(clicked) != null) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage(errorMessage);
                }
            }
        }
    }
}
