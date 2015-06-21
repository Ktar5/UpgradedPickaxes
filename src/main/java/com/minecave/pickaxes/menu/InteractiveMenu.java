package com.minecave.pickaxes.menu;

import com.minecave.pickaxes.util.item.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Timothy Andis
 */
public abstract class InteractiveMenu extends Menu {

    protected final Button NEXT_PAGE, PREV_PAGE;
    protected Map<Integer, Page> pages = new HashMap<>();

    public InteractiveMenu(String name) {
        super(name);
        ItemStack green = new Wool(DyeColor.GREEN).toItemStack(1);
        ItemStack red = new Wool(DyeColor.RED).toItemStack(1);
        NEXT_PAGE = new Button(ItemBuilder.wrap(green)
                .name(ChatColor.GREEN.toString() + ChatColor.BOLD + "Next Page").build(), (player, clickType) -> {
            Page curPage = getCurrentPage(player);
            if (curPage == null) {
                display(player);
            } else {
                Page page = pages.get(curPage.getId() - 1);
                if (page == null) {
                    display(player);
                } else {
                    page.display(player);
                }
            }
        });
        PREV_PAGE = new Button(ItemBuilder.wrap(red)
                .name(ChatColor.RED.toString() + ChatColor.BOLD + "Previous Page").build(), (player, clickType) -> {
            Page curPage = getCurrentPage(player);
            if (curPage == null) {
                display(player);
            } else {
                Page page = pages.get(curPage.getId() + 1);
                if (page == null) {
                    display(player);
                } else {
                    page.display(player);
                }
            }
        });
        this.pages = new HashMap<>();
    }

    public abstract Page getCurrentPage(Player player);

    public void addPage(int id, Page page) {
        pages.put(id, page);
    }

    public void addButton(Button button, Player player) {
        int slot = -1;
        for (int i = 0; i < getButtons().length; i++) {
            if (buttons[i] == null) {
                slot = i;
            }
        }
        if (slot >= 0) {
            setButton(button, slot, player);
        }
    }

//    public void display(Player player) {
//        this.buttons = fill(player);
//        int size = (buttons.length + 8) / 9 * 9;
//        Inventory inventory = Bukkit.createInventory(player, size, name);
//        for (int i = 0; i < buttons.length; i++) {
//            Button burton = buttons[i];
//            inventory.setItem(i, burton.getItem());
//        }
//        player.openInventory(inventory);
//    }

    @Override
    public void update(Player player) {
        Button[] buttons = fill(player);
        Inventory inventory = player.getOpenInventory().getTopInventory();
        for (int i = 0; i < buttons.length; i++) {
            Button button = buttons[i];
            if (button == null) {
                continue;
            }
            if (i > inventory.getSize()) {
                fill(player);
                return;
            }
            inventory.setItem(i, button.getItem());
        }
    }
}
