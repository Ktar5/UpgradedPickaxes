package com.minecave.pickaxes.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author Timothy Andis
 */
public class Button {

    private ItemStack itemStack;
    private ClickExecutor clickExecutor;

    public Button(ItemStack itemStack, ClickExecutor clickExecutor) {
        this.itemStack = itemStack;
        this.clickExecutor = clickExecutor;
    }

    public void click(Player player, ClickType type) {
        clickExecutor.click(player, type);
    }

    public ItemStack getItem() {
        return itemStack;
    }

    public interface ClickExecutor {

        void click(Player player, ClickType clickType);

    }

}
