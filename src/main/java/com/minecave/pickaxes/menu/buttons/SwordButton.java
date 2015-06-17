package com.minecave.pickaxes.menu.buttons;

import com.minecave.pickaxes.menu.Button;
import com.minecave.pickaxes.menu.Menu;
import com.minecave.pickaxes.pitem.Sword;
import com.minecave.pickaxes.sql.PlayerInfo;
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
        PlayerInfo info = PlayerInfo.get(player);
        if(info == null) {
            return;
        }
        info.removeSword(Sword.tryFromItem(getItem()));
        player.getInventory().addItem(getItem());
        menu.remove(slot, player);
    }
}
