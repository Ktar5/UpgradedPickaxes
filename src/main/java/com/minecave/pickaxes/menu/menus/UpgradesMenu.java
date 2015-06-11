package com.minecave.pickaxes.menu.menus;

import com.minecave.pickaxes.menu.Button;
import com.minecave.pickaxes.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Timothy Andis
 */
public class UpgradesMenu extends Menu {

    public UpgradesMenu(String name) {
        super(name);
    }

    @Override
    public Button[] fill(Player player) {
        ItemStack handPick = player.getItemInHand();
        if (handPick == null) {
            return new Button[0];
        }


        return new Button[0];
    }
}
