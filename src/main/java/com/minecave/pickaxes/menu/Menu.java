package com.minecave.pickaxes.menu;

import org.bukkit.Bukkit;
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

    private String name;
    protected Button[] buttons;
    protected final Button[] EMPTY = new Button[45];
    private static Map<String, Menu> menus = new HashMap<>();
    private Inventory inventory;

    public Menu(String name) {
        this.name = name;
        this.buttons = EMPTY;
        menus.put(name, this);
    }

    public Button getButton(int slot) {
        try {
            return buttons[slot];
        } catch (IndexOutOfBoundsException e ) {
            return null;
        }
    }

    public abstract Button[] fill(Player player);

    public static Menu get(String name) {
        return menus.get(name);
    }

    public void close(Player player) {
        this.inventory = null;
        player.closeInventory();
    }

    public void display(Player player) {
        this.buttons = fill(player);
        int size = (buttons.length + 8) / 9 * 9;
        Inventory inventory = Bukkit.createInventory(player, size, name);
        for (int i = 0; i < buttons.length; i++) {
            Button burton = buttons[i];
            inventory.setItem(i, burton.getItem());
        }
        player.closeInventory();
        this.inventory = inventory;
        player.openInventory(inventory);
    }

    public void update(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        for (int i = 0; i < buttons.length; i++) {
            Button button = buttons[i];
            if(button == null) {
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
            super(itemStack, (player, clickType) -> {});
        }

    }

}
