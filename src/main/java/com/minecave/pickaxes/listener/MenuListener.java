package com.minecave.pickaxes.listener;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.config.ConfigValues;
import com.minecave.pickaxes.menu.Button;
import com.minecave.pickaxes.menu.Menu;
import com.minecave.pickaxes.menu.Page;
import com.minecave.pickaxes.menu.buttons.PickaxeButton;
import com.minecave.pickaxes.menu.buttons.SwordButton;
import com.minecave.pickaxes.menu.menus.PickMenu;
import com.minecave.pickaxes.menu.menus.SwordMenu;
import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.pitem.Sword;
import com.minecave.pickaxes.sql.PlayerInfo;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * @author Timothy Andis
 */
public class MenuListener implements Listener {

    private PickaxesRevamped plugin = PickaxesRevamped.getInstance();

    public MenuListener() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ConfigValues values = plugin.getConfigValues();
        Player player = (Player) event.getWhoClicked();
        ClickType clickType = event.getClick();
        Menu menu = Menu.get(event.getInventory().getTitle());
        if (menu == null) {
            return;
        }

        event.setCancelled(true);
        event.setResult(Result.DENY);

        PlayerInfo info = PlayerInfo.get(player);
        if (info == null) {
            throw new RuntimeException(player.getName() + " doesn't have PlayerInfo.");
        }
        if(menu instanceof Page) {
            menu = ((Page) menu).getMenu();
        }

        if (menu instanceof PickMenu) {
            PickMenu pickMenu = (PickMenu) menu;
            Button button = menu.getButton(event.getRawSlot());
            if (event.getRawSlot() < event.getView().getTopInventory().getSize() &&
                    button != null) {
                if (!(button instanceof PickaxeButton)) {
                    button.click(player, clickType);
                } else {
                    ((PickaxeButton) button).click(player, clickType, event.getRawSlot(), pickMenu);
                }
            }
            Inventory inventory = event.getView().getBottomInventory();
            ItemStack inhand = event.getCurrentItem();
            if (inhand == null ||
                    (inhand.getType() != Material.DIAMOND_PICKAXE
                            && inhand.getType() != Material.IRON_PICKAXE
                            && inhand.getType() != Material.GOLD_PICKAXE
                            && inhand.getType() != Material.STONE_PICKAXE
                            && inhand.getType() != Material.WOOD_PICKAXE)) {
                return;
            }
            int slot = event.getSlot();
            inventory.setItem(slot, null);
            pickMenu.addButton(new PickaxeButton(inhand), player);
            info.addPickaxe(Pickaxe.tryFromItem(inhand));
//            pickMenu.close(player);
//            pickMenu.display(player);
            pickMenu.update(player);
            return;
        }

        if (menu instanceof SwordMenu) {
            SwordMenu swordMenu = (SwordMenu) menu;
            Button button = menu.getButton(event.getRawSlot());
            if (event.getRawSlot() < event.getView().getTopInventory().getSize() &&
                    button != null) {
                if (!(button instanceof SwordButton)) {
                    button.click(player, clickType);
                } else {
                    ((SwordButton) button).click(player, clickType, event.getRawSlot(), swordMenu);
                }
            }
            Inventory inventory = event.getView().getBottomInventory();
            ItemStack inhand = event.getCurrentItem();
            if (inhand == null ||
                    (inhand.getType() != Material.DIAMOND_SWORD
                            && inhand.getType() != Material.IRON_SWORD
                            && inhand.getType() != Material.GOLD_SWORD
                            && inhand.getType() != Material.STONE_SWORD
                            && inhand.getType() != Material.WOOD_SWORD)) {
                return;
            }
            int slot = event.getSlot();
            inventory.setItem(slot, null);
            swordMenu.addButton(new SwordButton(inhand), player);
            info.addSword(Sword.tryFromItem(inhand));
//            swordMenu.close(player);
//            swordMenu.display(player);
            swordMenu.update(player);
            return;
        }

        Button button = menu.getButton(event.getRawSlot());
        if (button == null) {
            return;
        }

        button.click(player, clickType);
    }
}
