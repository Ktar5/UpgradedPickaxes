package com.minecave.pickaxes.menu.menus;

import com.minecave.pickaxes.menu.Button;
import com.minecave.pickaxes.menu.Menu;
import org.bukkit.entity.Player;

/**
 * @author Timothy Andis
 */
public class UpgradesMenu extends Menu {

    public UpgradesMenu(String name) {
        super(name);
    }

    @Override
    public Button[] fill(Player player) {
        return new Button[0];
    }
}