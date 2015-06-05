package com.minecave.pickaxes.menu.menus;

import com.minecave.pickaxes.menu.Button;
import com.minecave.pickaxes.menu.InteractiveMenu;
import com.minecave.pickaxes.menu.Page;
import com.minecave.pickaxes.menu.buttons.PickaxeButton;
import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.sql.PlayerInfo;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Timothy Andis
 */
public class PickMenu extends InteractiveMenu {

    private int lastId = 1;
    private Map<UUID, Page> currentPages = new HashMap<>();

    public PickMenu(String name) {
        super(name);
    }

    @Override
    public Page getCurrentPage(Player player) {
        return currentPages.get(player.getUniqueId());
    }

    @Override
    public Button[] fill(Player player) {
        PlayerInfo info = PlayerInfo.get(player);
        List<Pickaxe> picks = info.getPickaxes();
        List<Pickaxe> copy = new ArrayList<>(picks);
        Button[] buttons = new Button[27];
        buttons[26] = PREV_PAGE;
        buttons[25] = NEXT_PAGE;
        for(int i = 0; i < picks.size(); i++) {
            if(i != 25) {
                buttons[i] = new PickaxeButton(picks.get(i).getItemStack());
                copy.remove(picks.get(i));
                continue;
            }
            int size = copy.size();
            Button[] newButtons = new Button[27];
            for(int r = 0; r < size; r++) {
                newButtons[r] = new PickaxeButton(copy.get(r).getItemStack());
            }
            Page page = new Page(this, newButtons, lastId);
            lastId++;
            currentPages.put(player.getUniqueId(), page);
        }
        return buttons;
    }
}
