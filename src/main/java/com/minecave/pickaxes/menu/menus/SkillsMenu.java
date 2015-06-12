package com.minecave.pickaxes.menu.menus;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.config.ConfigValues;
import com.minecave.pickaxes.menu.Button;
import com.minecave.pickaxes.menu.Menu;
import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.skill.Skill;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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
        Button[] buttons = new Button[18];

        List<Skill> skills = getSkills(player.getItemInHand());
        int i = 0;
        for(Skill skill : skills) {
            if(!player.hasPermission(skill.getPerm())) {
                continue;
            }
            ItemStack item = new ItemStack()
            buttons[i] = new FillerButton(item);
            buttons[i + 9] = null;
            i++;
        }

        return buttons;
    }

    public List<Skill> getSkills(ItemStack item) {
        if(Pickaxe.tryFromItem(item) != null) {
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
