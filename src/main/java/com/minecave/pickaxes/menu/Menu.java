package com.minecave.pickaxes.menu;

import com.minecave.pickaxes.util.item.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Timothy Andis
 */
public abstract class Menu {

    public static FillerButton BLACK;
    public static FillerButton RED;
    public static FillerButton GREEN;
    public static FillerButton BLUE;
    public static FillerButton DENY;

    static {
        BLACK = new FillerButton(ItemBuilder.wrap(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData())).name(" ").build());
        RED = new FillerButton(ItemBuilder.wrap(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.RED.getWoolData())).name(" ").build());
        GREEN = new FillerButton(ItemBuilder.wrap(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.GREEN.getWoolData())).name(" ").build());
        BLUE = new FillerButton(ItemBuilder.wrap(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLUE.getWoolData())).name(" ").build());
        DENY = new FillerButton(ItemBuilder.wrap(new ItemStack(Material.REDSTONE_BLOCK, 1)).name(ChatColor.DARK_RED + "You cannot afford this!").build());
    }

    private String name;
    protected Button[] buttons;
    protected final Button[] EMPTY = new Button[45];
    private static Map<String, Menu> menus = new HashMap<>();

    public Menu(String name) {
        this.name = name;
        this.buttons = EMPTY;
        menus.put(name, this);
    }

    public Button getButton(int slot) {
        try {
            return buttons[slot];
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public abstract Button[] fill(Player player);

    public static Menu get(String name) {
        Menu menu = menus.get(name);
        if (menu == null) {
            for (Menu menu1 : menus.values()) {
                if (menu1.getTitle().equalsIgnoreCase(name)) {
                    return menu1;
                }
            }
        }
        return menu;
    }

    public void close(Player player) {
        player.closeInventory();
    }

    public void display(Player player) {
        this.buttons = fill(player);
        int size = (buttons.length + 8) / 9 * 9;
        Inventory inventory = Bukkit.createInventory(player, size, name);
        for (int i = 0; i < buttons.length; i++) {
            Button burton = buttons[i];
            if (burton != null) {
                inventory.setItem(i, burton.getItem());
            }
        }
        player.closeInventory();
        player.openInventory(inventory);
    }

    public void update(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        for (int i = 0; i < buttons.length; i++) {
            Button button = buttons[i];
            if (button == null) {
                continue;
            }
            inventory.setItem(i, button.getItem());
        }
    }

    protected void fillPanes(Button[] buttons, PaneType type, FillerButton fillerButton) {
        int size = buttons.length;
        switch (type) {
            case ALL: {
                for (int i = 0; i < buttons.length; i++) {
                    buttons[i] = fillerButton;
                }
                break;
            }
            case OUTLINE: {
                for (int i = 0; i < 9; i++) {
                    buttons[i] = fillerButton;
                    buttons[i + (size - 9)] = fillerButton;
                    if (i == 0 || i == 8) {
                        for (int a = 9; a < size; a += 9) {
                            buttons[i + a] = fillerButton;
                        }
                    }
                }
                break;
            }
            case SEQUENTIAL: {
                for (int i = 0; i < size; i += 2) {
                    buttons[i] = fillerButton;
                }
                break;
            }
        }
    }

    public String getTitle() {
        return name;
    }

    public Button[] getButtons() {
        return buttons;
    }

    public void remove(int slot, Player player) {
        try {
            buttons[slot] = new FillerButton(new ItemStack(Material.AIR));
            update(player);
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    protected void setButton(Button button, int slot, Player player) {
        try {
            buttons[slot] = button;
            update(player);
        } catch (IndexOutOfBoundsException ignored) {
        }
    }

    protected enum PaneType {
        ALL,
        OUTLINE,
        SEQUENTIAL
    }

    public static class FillerButton extends Button {
        public FillerButton(ItemStack itemStack) {
            super(itemStack, (player, clickType) -> {
            });
        }
    }
}
