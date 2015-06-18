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
        List<Pickaxe> clone = new ArrayList<>(picks);

        double pageCount = Math.ceil(picks.size() / 25);
        if(pageCount == 0) pageCount = 1;
        List<Page> pageList = new ArrayList<>();
        for(int i = 1; i <= pageCount; i++) {
            Button[] buttons = new Button[27];
            buttons[25] = PREV_PAGE;
            buttons[26] = NEXT_PAGE;
            for(int j = 0; j < 25; j++) {
                if(clone.isEmpty()) {
                    break;
                }
                Pickaxe p = clone.remove(0);
                buttons[j] = new PickaxeButton(p.getItemStack());
            }
            Page page = new Page(this, buttons, pageList.size() + 1);
            pageList.add(page);
        }
        for(int i = 1; i < pageList.size(); i++) {
            Page page = pageList.get(i);
            pages.put(page.getId(), page);
        }
        if(this.getCurrentPage(player) == null) {
            this.currentPages.put(player.getUniqueId(), pageList.get(0));
        }

        return getCurrentPage(player).fill(player);
    }
}
