package com.minecave.pickaxes.pitem;

import com.minecave.pickaxes.drops.BlockValues;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.Skill;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Timothy Andis
 */
public class Pickaxe extends PItem {

    protected static Map<ItemStack, Pickaxe> pickaxeMap = new HashMap<>();

    public Pickaxe(ItemStack itemStack, String name) {
        super(itemStack, name);
    }

    public Pickaxe(ItemStack itemStack, Level level, int xp, List<PEnchant> enchants, String name, Skill skill) {
        super(itemStack, level, xp, enchants, name, skill);
    }

    public static Pickaxe tryFromItem(ItemStack inhand) {
        if (inhand == null ||
          (inhand.getType() != Material.DIAMOND_PICKAXE
            && inhand.getType() != Material.IRON_PICKAXE
            && inhand.getType() != Material.GOLD_PICKAXE
            && inhand.getType() != Material.STONE_PICKAXE
            && inhand.getType() != Material.WOOD_PICKAXE)) {
            return null;
        }
        return get(inhand);
    }

    public static Pickaxe get(ItemStack itemStack) {
        return pickaxeMap.get(itemStack);
    }

    public void onBreak(BlockBreakEvent event) {
        for(PEnchant enchant : this.getEnchants()) {
            enchant.activate(event);
        }
        int xp = 1;
        if(BlockValues.getXp(event.getBlock()) > -1) {
            xp = BlockValues.getXp(event.getBlock());
        }
        incrementXp(xp, event.getPlayer());
    }
}
