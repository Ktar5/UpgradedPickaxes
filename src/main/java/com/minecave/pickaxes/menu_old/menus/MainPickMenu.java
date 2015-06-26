package com.minecave.pickaxes.menu_old.menus;

public class MainPickMenu {}
//
//import com.minecave.pickaxes.EnhancedPicks;
//import com.minecave.pickaxes.item.PItem;
//import com.minecave.pickaxes.menu.menu.Menu;
//import com.minecave.pickaxes.util.item.ItemBuilder;
//import org.bukkit.ChatColor;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//import org.bukkit.event.block.BlockBreakEvent;
//import org.bukkit.inventory.ItemStack;
//
//import java.awt.*;
//
///**
// * @author Timothy Andis
// */
//public class MainPickMenu extends Menu {
//
//    public MainPickMenu(String name) {
//        super(name);
//    }
//
//    @Override
//    public Button[] fill(Player player) {
//        Button[] buttons = new Button[9];
//        fillPanes(buttons, PaneType.ALL, BLACK);
//
//        PItem<BlockBreakEvent> pItem = EnhancedPicks.getInstance().getPItemManager()
//                .getPItem(BlockBreakEvent.class, player.getItemInHand());
//
//        ItemStack chest = ItemBuilder.wrap(new ItemStack(Material.CHEST))
//                .name(ChatColor.YELLOW + "Pick Chest")
//                .lore(" ",
//                        ChatColor.GRAY + "Click to view all your current pickaxes.")
//                .build();
//        buttons[2] = new Button(chest, (player1, clickType) -> {
//            Menu menu = EnhancedPicks.getInstance().getMenuManager().get(PickMenu.class);
//            menu.display(player);
//        });
//
//        ItemStack upgrades = ItemBuilder.wrap(new ItemStack(Material.ENCHANTED_BOOK))
//                .name(ChatColor.YELLOW + "Upgrades")
//                .lore(" ",
//                        ChatColor.GRAY + "Click to manage your Pick's upgrades.")
//                .build();
//        buttons[4] = new Button(upgrades, (player1, clickType) -> {
//            if (pItem != null) {
//                Menu menu = EnhancedPicks.getInstance().getMenuManager().get(UpgradesMenu.class);
//                menu.display(player);
//            }
//        });
//
//        ItemStack skills = ItemBuilder.wrap(new ItemStack(Material.EMERALD))
//                .name(ChatColor.YELLOW + "Skills")
//                .lore(" ",
//                        ChatColor.GRAY + "Click to manage your Pick's skills.")
//                .build();
//        buttons[6] = new Button(skills, (player1, clickType) -> {
//            if (pItem != null) {
//                Menu menu = EnhancedPicks.getInstance().getMenuManager().get(SkillsMenu.class);
//                menu.display(player);
//            }
//        });
//        return buttons;
//    }
//}
