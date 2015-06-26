package com.minecave.pickaxes.menu_old.menus;
//
public class SwordMenu {}
//import com.avaje.ebean.Page;
//import com.minecave.pickaxes.EnhancedPicks;
//import com.minecave.pickaxes.item.PItem;
//import com.minecave.pickaxes.menu_old.buttons.SwordButton;
//import com.minecave.pickaxes.player.PlayerInfo;
//import org.bukkit.entity.Player;
//import org.bukkit.event.entity.EntityDamageByEntityEvent;
//
//import java.util.*;
//
///**
// * @author Timothy Andis
// */
//public class SwordMenu extends InteractiveMenu {
//
//    private int             lastId       = 1;
//    private Map<UUID, Page> currentPages = new HashMap<>();
//
//    public SwordMenu(String name) {
//        super(name);
//    }
//
//    @Override
//    public Page getCurrentPage(Player player) {
//        return currentPages.get(player.getUniqueId());
//    }
//
//    @Override
//    public Button[] fill(Player player) {
//        PlayerInfo info = EnhancedPicks.getInstance().getPlayerManager().get(player);
//        List<PItem<EntityDamageByEntityEvent>> picks = info.getSwords();
//        List<PItem<EntityDamageByEntityEvent>> clone = new ArrayList<>(picks);
//        this.pages.clear();
//
//        double pageCount = Math.ceil(picks.size() / 25);
//        if (pageCount == 0) pageCount = 1;
//        List<Page> pageList = new ArrayList<>();
//        for (int i = 1; i <= pageCount; i++) {
//            Button[] buttons = new Button[27];
//            buttons[25] = PREV_PAGE;
//            buttons[26] = NEXT_PAGE;
//            for (int j = 0; j < 25; j++) {
//                if (clone.isEmpty()) {
//                    break;
//                }
//                PItem<EntityDamageByEntityEvent> p = clone.remove(0);
//                if(p == null) {
//                    continue;
//                }
//                p.updateMeta();
//                buttons[j] = new SwordButton(p.getItem());
//            }
//            Page page = new Page(this, buttons, pageList.size() + 1);
//            pageList.add(page);
//        }
//        for (int i = 1; i < pageList.size(); i++) {
//            Page page = pageList.get(i);
//            pages.put(page.getId(), page);
//        }
//        if (this.getCurrentPage(player) == null) {
//            this.currentPages.put(player.getUniqueId(), pageList.get(0));
//        }
//
//        return getCurrentPage(player).fill(player);
//    }
//}
