package com.minecave.pickaxes.menu;

import org.bukkit.entity.Player;

/**
 * @author Timothy Andis
 */
public class Page extends InteractiveMenu {

    private InteractiveMenu menu;
    private Button[]        buttons;
    private int             id;

    public Page(InteractiveMenu name, Button[] buttons, int id) {
        super(name.getTitle());
        this.menu = name;
        this.buttons = buttons;
        this.id = id;
    }

    @Override
    public Page getCurrentPage(Player player) {
        return menu.getCurrentPage(player);
    }

    @Override
    public Button[] fill(Player player) {
        return buttons;
    }

    public int getId() {
        return id;
    }

    public InteractiveMenu getMenu() {
        return menu;
    }
}
