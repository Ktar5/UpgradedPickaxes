package com.minecave.pickaxes.menu.menus;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.menu.Button;
import com.minecave.pickaxes.menu.InteractiveMenu;
import com.minecave.pickaxes.menu.Page;
import com.minecave.pickaxes.menu.buttons.PickaxeButton;
import com.minecave.pickaxes.player.PlayerInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

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
        PlayerInfo info = EnhancedPicks.getInstance().getPlayerManager().get(player);
        List<PItem<BlockBreakEvent>> picks = info.getPickaxes();
        List<PItem<BlockBreakEvent>> clone = new ArrayList<>(picks);

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
                PItem<BlockBreakEvent> p = clone.remove(0);
                buttons[j] = new PickaxeButton(p.getItem());
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
