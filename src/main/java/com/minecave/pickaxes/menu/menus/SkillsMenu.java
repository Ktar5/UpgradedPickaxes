package com.minecave.pickaxes.menu.menus;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.menu.Button;
import com.minecave.pickaxes.menu.Menu;
import com.minecave.pickaxes.skill.PSkill;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

/**
 * @author Timothy Andis
 */
public class SkillsMenu extends Menu {

    public SkillsMenu(String name) {
        super(name);
    }

    @Override
    public Button[] fill(Player player) {
        Button[] buttons = new Button[9];

        PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
        if(pItem == null) {
            return new Button[9];
        }
        int i = 0;
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
            meta.setLore(Collections.singletonList(purchased ?
                    ChatColor.DARK_GREEN + "Click to activate." :
                    isHighEnough ? ChatColor.DARK_RED + "Click to purchase." :
                            ChatColor.DARK_RED + "You need level " + skill.getLevel() + "."));
            item.setItemMeta(meta);
            buttons[i] = new Button(item, (p, clickType) -> {
                if (!isHighEnough) {
                    p.sendMessage(ChatColor.RED + "You need level " + skill.getLevel());
                    return;
                }
                if (!purchased) {
                    pItem.getPurchasedSkills().add(skill);
                    pItem.setPoints(pItem.getPoints() - skill.getCost());
                }
                pItem.setCurrentSkill(skill);
                p.sendMessage(ChatColor.GOLD + "You activated " + skill.getName() + ".");
                this.display(p);
            });
            i++;
        }

        return buttons;
    }
}
