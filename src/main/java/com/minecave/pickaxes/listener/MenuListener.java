package com.minecave.pickaxes.listener;

import com.minecave.pickaxes.EnhancedPicks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {

    private EnhancedPicks plugin = EnhancedPicks.getInstance();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
//        Player player = (Player) event.getWhoClicked();
//        ClickType clickType = event.getClick();
//        CustomConfig config = EnhancedPicks.getInstance().getConfig("menus");
//        String pickName = Strings.color(config.get("pickaxeMenu", String.class, "Pickaxe Menu"));
//        String swordName = Strings.color(config.get("swordMenu", String.class, "Sword Menu"));
//        if (event.getInventory().getTitle().equalsIgnoreCase(pickName)) {
//            Inventory inventory = event.getView().getBottomInventory();
//            ItemStack clickedItem = event.getCurrentItem();
//            if (clickedItem == null || clickedItem.getType() != Material.DIAMOND_PICKAXE) {
//                return;
//            }
//            int slot = event.getSlot();
//            inventory.setItem(slot, null);
//            pickMenu.addButton(new PickaxeButton(clickedItem), player);
//            info.addPickaxe(plugin.getPItemManager().getPItem(BlockBreakEvent.class, clickedItem));
//            pickMenu.update(player);
//            event.setCancelled(true);
//            event.setResult(Event.Result.DENY);
//        } else if (event.getInventory().getTitle().equalsIgnoreCase(swordName)) {
//
//            event.setCancelled(true);
//            event.setResult(Event.Result.DENY);
//        }
//
//        event.setCancelled(true);
//        event.setResult(Result.DENY);
//
//        PlayerInfo info = plugin.getPlayerManager().get(player);
//        if (info == null) {
//            throw new RuntimeException(player.getName() + " doesn't have PlayerInfo.");
//        }
//        if (menu instanceof Page) {
//            menu = ((Page) menu).getMenu();
//        }
//
//        if (menu instanceof PickMenu) {
//            PickMenu pickMenu = (PickMenu) menu;
//            Button button = menu.getButton(event.getRawSlot());
//            if (event.getRawSlot() < event.getView().getTopInventory().getSize() &&
//                    button != null) {
//                if (!(button instanceof PickaxeButton)) {
//                    button.click(player, clickType);
//                } else {
//                    ((PickaxeButton) button).click(player, clickType, event.getRawSlot(), pickMenu);
//                }
//            }
//            Inventory inventory = event.getView().getBottomInventory();
//            ItemStack inhand = event.getCurrentItem();
//            if (inhand == null || inhand.getType() != Material.DIAMOND_PICKAXE) {
//                return;
//            }
//            int slot = event.getSlot();
//            inventory.setItem(slot, null);
//            pickMenu.addButton(new PickaxeButton(inhand), player);
//            info.addPickaxe(plugin.getPItemManager().getPItem(BlockBreakEvent.class, inhand));
//            pickMenu.update(player);
//            return;
//        }
//
//        if (menu instanceof SwordMenu) {
//            SwordMenu swordMenu = (SwordMenu) menu;
//            Button button = menu.getButton(event.getRawSlot());
//            if (event.getRawSlot() < event.getView().getTopInventory().getSize() &&
//                    button != null) {
//                if (!(button instanceof SwordButton)) {
//                    button.click(player, clickType);
//                } else {
//                    ((SwordButton) button).click(player, clickType, event.getRawSlot(), swordMenu);
//                }
//            }
//            Inventory inventory = event.getView().getBottomInventory();
//            ItemStack inhand = event.getCurrentItem();
//            if (inhand == null || inhand.getType() != Material.DIAMOND_SWORD) {
//                return;
//            }
//            int slot = event.getSlot();
//            inventory.setItem(slot, null);
//            swordMenu.addButton(new SwordButton(inhand), player);
//            info.addSword(plugin.getPItemManager().getPItem(EntityDamageByEntityEvent.class, inhand));
//            swordMenu.update(player);
//            return;
//        }
//
//        Button button = menu.getButton(event.getRawSlot());
//        if (button == null) {
//            return;
//        }
//
//        button.click(player, clickType);
    }
}
