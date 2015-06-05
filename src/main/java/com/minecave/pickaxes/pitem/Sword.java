package com.minecave.pickaxes.pitem;

import com.tadahtech.pub.enchant.PEnchant;
import com.tadahtech.pub.level.Level;
import com.tadahtech.pub.skill.Skill;
import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Timothy Andis
 */
public class Sword extends PItem {

    protected static Map<ItemStack, Sword> swordMap = new HashMap<>();

    public Sword(ItemStack itemStack, String name) {
        super(itemStack, name);
    }

    public Sword(ItemStack itemStack, Level level, int xp, List<PEnchant> enchants, String name, Skill skill) {
        super(itemStack, level, xp, enchants, name, skill);
    }

    public static Sword tryFromItem(ItemStack inhand) {
        if (inhand == null ||
          (inhand.getType() != Material.DIAMOND_SWORD
            && inhand.getType() != Material.IRON_SWORD
            && inhand.getType() != Material.GOLD_SWORD
            && inhand.getType() != Material.STONE_SWORD
            && inhand.getType() != Material.WOOD_SWORD)) {
            return null;
        }
        return get(inhand);
    }

    public static Sword get(ItemStack itemStack) {
        return swordMap.get(itemStack);
    }

    public void onHit(EntityDamageByEntityEvent event) {
        for(PEnchant enchant : this.getEnchants()) {
            enchant.activate(event);
        }
    }
}
