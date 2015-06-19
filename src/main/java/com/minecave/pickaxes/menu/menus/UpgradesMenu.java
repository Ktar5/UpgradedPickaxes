package com.minecave.pickaxes.menu.menus;

import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.menu.Button;
import com.minecave.pickaxes.menu.Menu;
import com.minecave.pickaxes.pitem.PItem;
import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.pitem.Sword;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import java.util.Collections;

/**
 * @author Timothy Andis
 */
public class UpgradesMenu extends Menu {

    public UpgradesMenu(String name) {
        super(name);
    }

    boolean start = true;
    @Override
    public Button[] fill(Player player) {
        ItemStack handPick = player.getItemInHand();
        if (handPick == null) {
            return new Button[0];
        }
        ItemStack item = player.getItemInHand();
        PItem pItem = Pickaxe.tryFromItem(item);
        if (pItem == null) {
            pItem = Sword.tryFromItem(item);
        }
        if(pItem == null) {
            return new Button[0];
        }
        final PItem fItem = pItem;
        int enchantCount = pItem.getEnchants().size();
        Button[] buttons = new Button[(int) (Math.ceil((double)enchantCount / 2D) * 9)];
        fillPanes(buttons, PaneType.ALL, new FillerButton(new ItemStack(Material.AIR)));
        int c = 0;
        for (PEnchant enchant : pItem.getEnchants().values()) {
            int redWoolIndex = c + 1;
            int bookIndex = c + 2;
            int greenWoolIndex = c + 3;
            int cost = enchant.getCost();
            int prevCost = enchant.getLevelCost(enchant.getLevel() - 1);

            ItemStack redWool = new Wool(DyeColor.RED).toItemStack(1);
            ItemMeta meta = redWool.getItemMeta();
            meta.setLore(Collections.singletonList(ChatColor.GOLD + "You get " + prevCost + " points back."));
            meta.setDisplayName(enchant.getLevel() > 0 ?
                    ChatColor.RED + "Click to remove a level." :
                    ChatColor.DARK_RED + "Level 0. Cannot decrease.");
            redWool.setItemMeta(meta);
            buttons[redWoolIndex] = new Button(redWool,
                    (p, clickType) -> {
                        if (enchant.getLevel() > 0) {
                            enchant.decreaseLevel(p, fItem);
                            this.display(p);
                        }
                    });

            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, enchant.getLevel());
            meta = book.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + enchant.getName() + " Level: " + enchant.getLevel());
            book.setItemMeta(meta);
            buttons[bookIndex] = new FillerButton(book);

            ItemStack greenWool = new Wool(DyeColor.GREEN).toItemStack(enchant.getCost());
            meta = greenWool.getItemMeta();
            meta.setLore(Collections.singletonList(ChatColor.GOLD + "Costs " + cost + " points."));
            meta.setDisplayName(enchant.getLevel() < enchant.getMaxLevel() ?
                    ChatColor.GREEN + "Click to add a level." :
                    ChatColor.DARK_GREEN + "Max level. Cannot increase.");
            greenWool.setItemMeta(meta);
            buttons[greenWoolIndex] = new Button(greenWool,
                    (p, clickType) -> {
                        if (enchant.getLevel() < enchant.getMaxLevel() &&
                                enchant.getCost() <= fItem.getPoints()) {
                            enchant.increaseLevel(p, fItem);
                            this.display(p);
                            return;
                        }
                        if(enchant.getCost() > fItem.getPoints()) {
                            player.sendMessage(ChatColor.RED + "You don't have enough points on this item.");
                            player.sendMessage(ChatColor.GOLD + "Current Item Points: " + fItem.getPoints());
                        }
                    });

            c += start ? 4 : 5;
            start = !start;
        }
        return buttons;
    }
}
