package com.minecave.pickaxes.menu.menus;

import com.minecave.pickaxes.menu.Button;
import com.minecave.pickaxes.menu.InteractiveMenu;
import com.minecave.pickaxes.menu.Page;
import com.minecave.pickaxes.menu.buttons.SwordButton;
import com.minecave.pickaxes.pitem.Sword;
import com.minecave.pickaxes.sql.PlayerInfo;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Timothy Andis
 */
public class SwordMenu extends InteractiveMenu {

    private int lastId = 1;
    private Map<UUID, Page> currentPages = new HashMap<>();

    public SwordMenu(String name) {
        super(name);
    }

    @Override
    public Page getCurrentPage(Player player) {
        return currentPages.get(player.getUniqueId());
    }

    @Override
    public Button[] fill(Player player) {
        PlayerInfo info = PlayerInfo.get(player);
        List<Sword> swords = info.getSwords();
        List<Sword> copy = new ArrayList<>(swords);
        Button[] buttons = new Button[27];
        buttons[26] = PREV_PAGE;
        buttons[25] = NEXT_PAGE;
        for(int i = 0; i < swords.size(); i++) {
            if(i != 25) {
                buttons[i] = new SwordButton(swords.get(i).getItemStack());
                copy.remove(swords.get(i));
                continue;
            }
            int size = copy.size();
            Button[] newButtons = new Button[27];
            for(int r = 0; r < size; r++) {
                newButtons[r] = new SwordButton(copy.get(r).getItemStack());
            }
            Page page = new Page(this, newButtons, lastId);
            lastId++;
            currentPages.put(player.getUniqueId(), page);
        }
        return buttons;
    }
}
