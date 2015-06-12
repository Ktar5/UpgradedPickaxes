package com.minecave.pickaxes.menu.menus;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.config.ConfigValues;
import com.minecave.pickaxes.menu.Button;
import com.minecave.pickaxes.menu.Menu;
import com.minecave.pickaxes.pitem.PItem;
import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.pitem.Sword;
import com.minecave.pickaxes.skill.Skill;
import com.minecave.pickaxes.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

        List<Skill> skills = getSkills(player.getItemInHand());
        int i = 0;
        for (Skill skill : skills) {
            if (!player.hasPermission(skill.getPerm())) {
                continue;
            }
            PItem pItem = Pickaxe.tryFromItem(player.getItemInHand());
            if (pItem == null) {
                pItem = Sword.tryFromItem(player.getItemInHand());
                if (pItem == null) {
                    continue;
                }
            }
            PItem fItem = pItem;
            boolean purchased = pItem.getPurchasedSkills().contains(skill);
            ItemStack item = new ItemStack(purchased ?
                    pItem.getSkill().equals(skill) ?
                            Material.REDSTONE : Material.SULPHUR :
                    Utils.BLACK.getItem().getType());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName((purchased ? ChatColor.GOLD : ChatColor.RED) + skill.getName());
            meta.setLore(Collections.singletonList(purchased ?
                    ChatColor.DARK_GREEN + "Click to activate." :
                    ChatColor.DARK_RED + "Click to purchase."));
            item.setItemMeta(meta);
            buttons[i] = new Button(item, (p, clickType) -> {
                if(!purchased) {
                    fItem.getPurchasedSkills().add(skill);
                }
                fItem.setSkill(skill);
                p.sendMessage(ChatColor.GOLD + "You activated " + skill.getName() + ".");
            });
            i++;
        }

        return buttons;
    }

    public List<Skill> getSkills(ItemStack item) {
        if (Pickaxe.tryFromItem(item) != null) {
            return getPickaxeSkills();
        } else {
            return getSwordSkills();
        }
    }

    public List<Skill> getPickaxeSkills() {
        List<Skill> skills = new ArrayList<>();
        ConfigValues cv = PickaxesRevamped.getInstance().getConfigValues();
        skills.add(cv.getBomber());
        skills.add(cv.getEarthquake());
        skills.add(cv.getIce());
        skills.add(cv.getLightning());
        return skills;
    }

    public List<Skill> getSwordSkills() {
        List<Skill> skills = new ArrayList<>();

        return skills;
    }
}
