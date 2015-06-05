package com.minecave.pickaxes.menu.buttons;

import com.tadahtech.pub.menu.Button;
import com.tadahtech.pub.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author Timothy Andis
 */
public class SwordButton extends Button {

    public SwordButton(ItemStack itemStack) {
        super(itemStack, null);
    }

    public void click(Player player, ClickType type, int slot, Menu menu) {
        player.getInventory().addItem(getItem());
        menu.remove(slot, player);
    }
}
