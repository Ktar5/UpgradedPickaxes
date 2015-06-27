/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.menu.menus;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.item.PItemType;
import com.minecave.pickaxes.menu.items.BasicItem;
import com.minecave.pickaxes.menu.items.Item;
import com.minecave.pickaxes.menu.menu.Menu;
import com.minecave.pickaxes.menu.menu.ScrollingMenu;
import com.minecave.pickaxes.player.PlayerInfo;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.config.CustomConfig;
import com.minecave.pickaxes.util.item.ItemBuilder;
import com.minecave.pickaxes.util.message.Strings;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuCreator {

    private static Item BLACK = BasicItem.createFiller(new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.BLACK.getWoolData()));
    private static Item AIR   = BasicItem.createFiller(new ItemStack(Material.AIR));

    public static void createMainPick(Player player) {
        CustomConfig config = EnhancedPicks.getInstance().getConfig("menus");
        String name = Strings.color(config.get("mainPickMenu", String.class, "Main Pick Menu"));
        Menu mainPick = Menu.createMenu(name, 9);
        for (int i = 0; i < mainPick.size(); i++) {
            switch (i) {
                case 2:
                    ItemStack chest = ItemBuilder.wrap(new ItemStack(Material.CHEST))
                            .name(ChatColor.YELLOW + "Pick Chest")
                            .lore(" ",
                                    ChatColor.GRAY + "Click to view all your current pickaxes.")
                            .build();
                    Item item = BasicItem.create(chest, (p, c) -> {
                        ScrollingMenu menu = createPickMenu(p);
                        if (menu != null) {
                            menu.setParent(mainPick);
                            menu.showTo(p);
                        }
                    });
                    mainPick.setItem(i, item);
                    break;
                case 4:
                    ItemStack upgrades = ItemBuilder.wrap(new ItemStack(Material.ENCHANTED_BOOK))
                            .name(ChatColor.YELLOW + "Upgrades")
                            .lore(" ",
                                    ChatColor.GRAY + "Click to manage your Pick's upgrades.")
                            .build();
                    item = BasicItem.create(upgrades, (p, c) -> {
                        Menu menu = createUpgradesMenu(p);
                        if (menu != null) {
                            menu.setParent(mainPick);
                            menu.showTo(p);
                        }
                    });
                    mainPick.setItem(i, item);
                    break;
                case 6:
                    ItemStack skills = ItemBuilder.wrap(new ItemStack(Material.EMERALD))
                            .name(ChatColor.YELLOW + "Skills")
                            .lore(" ",
                                    ChatColor.GRAY + "Click to manage your Pick's skills.")
                            .build();
                    item = BasicItem.create(skills, (p, c) -> {
                        Menu menu = createSkillsMenu(p);
                        if (menu != null) {
                            menu.setParent(mainPick);
                            menu.showTo(p);
                        }
                    });
                    mainPick.setItem(i, item);
                    break;
                default:
                    mainPick.setItem(i, BLACK);
                    break;
            }
        }
        mainPick.showTo(player);
    }

    public static void createMainSword(Player player) {
        CustomConfig config = EnhancedPicks.getInstance().getConfig("menus");
        String name = Strings.color(config.get("mainSwordMenu", String.class, "Main Sword Menu"));
        Menu mainSword = Menu.createMenu(name, 9);
        for (int i = 0; i < mainSword.size(); i++) {
            switch (i) {
                case 2:
                    ItemStack chest = ItemBuilder.wrap(new ItemStack(Material.CHEST))
                            .name(ChatColor.YELLOW + "Sword Chest")
                            .lore(" ",
                                    ChatColor.GRAY + "Click to view all your current swords.")
                            .build();
                    Item item = BasicItem.create(chest, (p, c) -> {
                        ScrollingMenu menu = createSwordMenu(p);
                        if (menu != null) {
                            menu.setParent(mainSword);
                            menu.showTo(p);
                        }
                    });
                    mainSword.setItem(i, item);
                    break;
                case 4:
                    ItemStack upgrades = ItemBuilder.wrap(new ItemStack(Material.ENCHANTED_BOOK))
                            .name(ChatColor.YELLOW + "Upgrades")
                            .lore(" ",
                                    ChatColor.GRAY + "Click to manage your Sword's upgrades.")
                            .build();
                    item = BasicItem.create(upgrades, (p, c) -> {
                        Menu menu = createUpgradesMenu(p);
                        if (menu != null) {
                            menu.setParent(mainSword);
                            menu.showTo(p);
                        }
                    });
                    mainSword.setItem(i, item);
                    break;
                case 6:
                    ItemStack skills = ItemBuilder.wrap(new ItemStack(Material.EMERALD))
                            .name(ChatColor.YELLOW + "Skills")
                            .lore(" ",
                                    ChatColor.GRAY + "Click to manage your Sword's skills.")
                            .build();
                    item = BasicItem.create(skills, (p, c) -> {
                        Menu menu = createSkillsMenu(p);
                        if (menu != null) {
                            menu.setParent(mainSword);
                            menu.showTo(p);
                        }
                    });
                    mainSword.setItem(i, item);
                    break;
                default:
                    mainSword.setItem(i, BLACK);
                    break;
            }
        }
        mainSword.showTo(player);
    }

    public static ScrollingMenu createPickMenu(Player player) {
        CustomConfig config = EnhancedPicks.getInstance().getConfig("menus");
        String name = Strings.color(config.get("pickaxeMenu", String.class, "Pickaxe Menu"));
        PlayerInfo info = EnhancedPicks.getInstance().getPlayerManager().get(player);
        if (info == null) {
            return null;
        }

        ScrollingMenu menu = ScrollingMenu.create(name);
        List<PItem<BlockBreakEvent>> picksClone = new ArrayList<>(info.getPickaxes());

        int k = 0;
        while(!picksClone.isEmpty()) {
            PItem<BlockBreakEvent> pick = picksClone.remove(0);
            pick.updateMeta();
            menu.setItem(k, BasicItem.create(pick.getItem(), (p, c) -> {
                int emptySlot = -1;
                for (int i = 0; i < p.getInventory().getContents().length; i++) {
                    if (p.getInventory().getItem(i) == null || p.getInventory().getItem(i).getType() == Material.AIR) {
                        emptySlot = i;
                        break;
                    }
                }
                if (emptySlot == -1) {
                    p.sendMessage(ChatColor.RED + "You cannot take out a pick with a full inventory.");
                    p.closeInventory();
                } else {
                    info.removePickaxe(pick);
                    pick.update(p);
                    p.getInventory().setItem(emptySlot, pick.getItem());
                    pick.setItem(p.getInventory().getItem(emptySlot));
                    pick.updateManually(p, p.getInventory().getItem(emptySlot));
                    Menu newMenu = createPickMenu(p);
                    if (newMenu != null) {
                        newMenu.setParent(menu.getParent());
                        menu.setParent(null);
                        p.closeInventory();
                        newMenu.showTo(p);
                    }
                }
            }));
            k++;
        }
        menu.setLowerInventoryListener((p, i) -> {
            ItemStack stack = p.getInventory().getItem(i);
            PItem<BlockBreakEvent> pItem = EnhancedPicks.getInstance()
                    .getPItemManager().getPItem(BlockBreakEvent.class, stack);
            if (pItem == null) {
                return;
            }
            if (pItem.getEClass() == BlockBreakEvent.class && pItem.getType() == PItemType.PICK) {
                p.getInventory().setItem(i, null);
                info.addPickaxe(pItem);
                Menu newMenu = createPickMenu(p);
                if (newMenu != null) {
                    newMenu.setParent(menu.getParent());
                    menu.setParent(null);
                    menu.close(p);
                    newMenu.showTo(p);
                }
            }
        });
        menu.flush();
        return menu;
    }

    public static ScrollingMenu createSwordMenu(Player player) {
        CustomConfig config = EnhancedPicks.getInstance().getConfig("menus");
        String name = Strings.color(config.get("swordMenu", String.class, "Sword Menu"));
        PlayerInfo info = EnhancedPicks.getInstance().getPlayerManager().get(player);
        if (info == null) {
            return null;
        }

        ScrollingMenu menu = ScrollingMenu.create(name);
        List<PItem<EntityDamageByEntityEvent>> swordsClone = new ArrayList<>(info.getSwords());

        int k = 0;
        while(!swordsClone.isEmpty()) {
            PItem<EntityDamageByEntityEvent> sword = swordsClone.remove(0);
            sword.updateMeta();
            menu.setItem(k, BasicItem.create(sword.getItem(), (p, c) -> {
                int emptySlot = -1;
                for (int i = 0; i < p.getInventory().getContents().length; i++) {
                    if (p.getInventory().getItem(i) == null || p.getInventory().getItem(i).getType() == Material.AIR) {
                        emptySlot = i;
                        break;
                    }
                }
                if (emptySlot == -1) {
                    p.sendMessage(ChatColor.RED + "You cannot take out a sword with a full inventory.");
                    p.closeInventory();
                } else {
                    info.removeSword(sword);
                    sword.update(p);
                    p.getInventory().setItem(emptySlot, sword.getItem());
                    sword.setItem(p.getInventory().getItem(emptySlot));
                    sword.updateManually(p, p.getInventory().getItem(emptySlot));
                    Menu newMenu = createPickMenu(p);
                    if (newMenu != null) {
                        newMenu.setParent(menu.getParent());
                        menu.setParent(null);
                        p.closeInventory();
                        newMenu.showTo(p);
                    }
                }
            }));
            k++;
        }
        menu.setLowerInventoryListener((p, i) -> {
            ItemStack stack = p.getInventory().getItem(i);
            PItem<EntityDamageByEntityEvent> pItem = EnhancedPicks.getInstance()
                    .getPItemManager().getPItem(EntityDamageByEntityEvent.class, stack);
            if (pItem == null) {
                return;
            }
            if (pItem.getEClass() == EntityDamageByEntityEvent.class && pItem.getType() == PItemType.PICK) {
                p.getInventory().setItem(i, null);
                info.addSword(pItem);
                Menu newMenu = createPickMenu(p);
                if (newMenu != null) {
                    newMenu.setParent(menu.getParent());
                    menu.setParent(null);
                    menu.close(p);
                    newMenu.showTo(p);
                }
            }
        });
        menu.flush();
        return menu;
    }

    public static Menu createUpgradesMenu(Player player) {
        PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
        if (pItem == null) {
            return null;
        }
        int enchantCount = pItem.getEnchants().size();
        if (enchantCount == 0) {
            return null;
        }
        CustomConfig config = EnhancedPicks.getInstance().getConfig("menus");
        String name = Strings.color(config.get("upgradeMenu", String.class, "Upgrade Menu"));
        Menu menu = Menu.createMenu(name, (int) (Math.ceil((double) enchantCount / 2D) * 9));

        updateMenuItems(menu, buildUpgradeItems(menu, player, pItem));

        return menu;
    }

    private static void updateMenuItems(Menu menu, List<Item> refreshed) {
//        menu.getItems().clear();
        for (int i = 0; i < menu.size(); i++) {
            if (i < refreshed.size()) {
                menu.setItem(i, refreshed.get(i));
            }
        }
    }

    private static List<Item> buildUpgradeItems(Menu menu, Player player, PItem<?> pItem) {
        List<Item> itemList = new ArrayList<>();

        ItemStack points = new ItemStack(Material.DIAMOND, pItem.getPoints());
        ItemMeta pointsMeta = points.getItemMeta();
        pointsMeta.setDisplayName(ChatColor.GOLD + "Current Points: " + ChatColor.WHITE + pItem.getPoints());
        points.setItemMeta(pointsMeta);
        menu.setItem(menu.size() - 1, BasicItem.createFiller(points));

        for (int i = 1; i <= pItem.getEnchants().size(); i++) {
            PEnchant enchant = pItem.getEnchants().get(i - 1);
            itemList.add(AIR);

            int cost = enchant.getCost();
            int prevCost = enchant.getLevelCost(enchant.getLevel() - 1);

            ItemStack redWool = new Wool(DyeColor.RED).toItemStack(1);
            ItemMeta meta = redWool.getItemMeta();
            meta.setLore(Collections.singletonList(ChatColor.GOLD + "You get " + prevCost + " points back."));
            meta.setDisplayName(enchant.getLevel() > 0 ?
                    ChatColor.RED + "Click to remove a level." :
                    ChatColor.DARK_RED + "Level 0. Cannot decrease.");
            redWool.setItemMeta(meta);
            itemList.add(BasicItem.create(redWool, (p, c) -> {
                if (enchant.getLevel() > 0) {
                    pItem.setItem(p.getItemInHand());
                    enchant.decreaseLevel(p, pItem);
                    updateMenuItems(menu, buildUpgradeItems(menu, player, pItem));
                    pItem.update(p);
                }
            }));

            ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, enchant.getLevel());
            meta = book.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + enchant.getDisplayName() + " Level: " + enchant.getLevel());
            book.setItemMeta(meta);
            itemList.add(BasicItem.createFiller(book));

            ItemStack greenWool = new Wool(DyeColor.GREEN).toItemStack(enchant.getCost());
            meta = greenWool.getItemMeta();
            meta.setLore(Collections.singletonList(ChatColor.GOLD + "Costs " + cost + " points."));
            meta.setDisplayName(enchant.getLevel() < enchant.getMaxLevel() ?
                    ChatColor.GREEN + "Click to add a level." :
                    ChatColor.DARK_GREEN + "Max level. Cannot increase.");
            greenWool.setItemMeta(meta);
            itemList.add(BasicItem.create(greenWool, (p, c) -> {
                if (enchant.getLevel() >= 0) {
                    if (enchant.getLevel() < enchant.getMaxLevel() &&
                            enchant.getCost() <= pItem.getPoints()) {
                        pItem.setItem(p.getItemInHand());
                        enchant.increaseLevel(p, pItem);
                        p.sendMessage(String.valueOf(enchant.getLevel()));
                        updateMenuItems(menu, buildUpgradeItems(menu, player, pItem));
                        pItem.update(p);
                        return;
                    }
                    if (enchant.getCost() > pItem.getPoints()) {
                        player.sendMessage(ChatColor.RED + "You don't have enough points on this item.");
                        player.sendMessage(ChatColor.GOLD + "Current Item Points: " + pItem.getPoints());
                    }
                }
            }));

            if (i % 2 == 0) {
                itemList.add(AIR);
            }
        }

        return itemList;
    }

    public static Menu createSkillsMenu(Player player) {
        PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
        if (pItem == null) {
            return null;
        }
        CustomConfig config = EnhancedPicks.getInstance().getConfig("menus");
        String name = Strings.color(config.get("skillsMenu", String.class, "Skills Menu"));
        Menu menu = Menu.createMenu(name, 9);

        updateMenuItems(menu, buildSkillItems(menu, player, pItem));

        return menu;
    }

    private static List<Item> buildSkillItems(Menu menu, Player player, PItem<?> pItem) {
        List<Item> itemList = new ArrayList<>();

        ItemStack points = new ItemStack(Material.DIAMOND, pItem.getPoints());
        ItemMeta pointsMeta = points.getItemMeta();
        pointsMeta.setDisplayName(ChatColor.GOLD + "Current Points: " + ChatColor.WHITE + pItem.getPoints());
        points.setItemMeta(pointsMeta);
        menu.setItem(menu.size() - 1, BasicItem.createFiller(points));

        for (PSkill skill : pItem.getAvailableSkills()) {
            if (!player.hasPermission(skill.getPerm())) {
                continue;
            }
            boolean purchased = pItem.getPurchasedSkills().contains(skill);
            boolean isHighEnough = skill.highEnough(pItem);
            ItemStack item = new ItemStack(purchased ?
                    pItem.getCurrentSkill() != null && pItem.getCurrentSkill().equals(skill) ?
                            Material.REDSTONE : Material.SULPHUR :
                    Material.STAINED_GLASS_PANE, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName((purchased ? ChatColor.GOLD : ChatColor.RED) + skill.getName());
            List<String> lore = new ArrayList<String>() {
                {
                    add(purchased ? ChatColor.DARK_GREEN + "Click to activate." :
                            isHighEnough ? ChatColor.DARK_RED + "Click to purchase." :
                                    ChatColor.DARK_RED + "You need level " + skill.getLevel() + ".");
                    add(purchased ? "" : ChatColor.GOLD + "Cost: " + ChatColor.WHITE + skill.getCost());
                }
            };
            meta.setLore(lore);
            item.setItemMeta(meta);
            itemList.add(BasicItem.create(item, (p, c) -> {
                if (!isHighEnough) {
                    p.sendMessage(ChatColor.RED + "You need level " + skill.getLevel());
                    return;
                }
                if (!purchased) {
                    if (skill.getCost() > pItem.getPoints()) {
                        player.sendMessage(ChatColor.RED + "You don't have enough points on this item.");
                        player.sendMessage(ChatColor.GOLD + "Current Item Points: " + pItem.getPoints());
                    } else {
                        pItem.setItem(p.getItemInHand());
                        pItem.getPurchasedSkills().add(skill);
                        pItem.setPoints(pItem.getPoints() - skill.getCost());
                        pItem.setCurrentSkill(skill);
                        p.sendMessage(ChatColor.GOLD + "You activated " + skill.getName() + ".");
                        pItem.update(p);
                    }
                } else {
                    pItem.setItem(p.getItemInHand());
                    pItem.setCurrentSkill(skill);
                    p.sendMessage(ChatColor.GOLD + "You activated " + skill.getName() + ".");
                    pItem.update(p);
                }
                updateMenuItems(menu, buildSkillItems(menu, player, pItem));
            }));
        }

        return itemList;
    }
}
