package com.minecave.pickaxes.menu.menus;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.items.ItemBuilder;
import com.minecave.pickaxes.menu.Button;
import com.minecave.pickaxes.menu.Menu;
import com.minecave.pickaxes.pitem.PItem;
import com.minecave.pickaxes.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Timothy Andis
 */
public class MainMenu extends Menu {

    public static Map<UUID, PItem> ITEMS = new HashMap<>();

    public MainMenu(String name) {
        super(name);
    }

    @Override
    public Button[] fill(Player player) {
        Button[] buttons = new Button[9];
        fillPanes(buttons, PaneType.ALL, Utils.BLACK);

        ItemStack chest = ItemBuilder.wrap(new ItemStack(Material.CHEST))
          .name(ChatColor.YELLOW + "Pick Chest")
          .lore(" ",
            ChatColor.GRAY + "Click to view all your current pickaxes.")
          .build();
        buttons[2] = new Button(chest, (player1, clickType) -> {
            Menu menu = PickaxesRevamped.getInstance().getConfigValues().getPickMenu();
            menu.display(player);
        });

        ItemStack upgrades = ItemBuilder.wrap(new ItemStack(Material.ENCHANTED_BOOK))
          .name(ChatColor.YELLOW + "Upgrades")
          .lore(" ",
            ChatColor.GRAY + "Click to manage your Pick's upgrades.")
          .build();
        buttons[4] = new Button(upgrades, (player1, clickType) -> {
            Menu menu = PickaxesRevamped.getInstance().getConfigValues().getUpgradesMenu();
            menu.display(player);
        });

        ItemStack skills = ItemBuilder.wrap(new ItemStack(Material.EMERALD))
          .name(ChatColor.YELLOW + "Skills")
          .lore(" ",
            ChatColor.GRAY + "Click to manage your Pick's upgrades.")
          .build();
        buttons[6] = new Button(skills, (player1, clickType) -> {
            Menu menu = PickaxesRevamped.getInstance().getConfigValues().getSkillsMenu();
            menu.display(player);
        });
        return buttons;
    }
}
