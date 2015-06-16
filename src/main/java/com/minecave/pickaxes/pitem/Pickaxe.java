package com.minecave.pickaxes.pitem;

import com.minecave.pickaxes.drops.BlockValues;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.Skill;
import com.minecave.pickaxes.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Timothy Andis
 */
public class Pickaxe extends PItem {

    protected static Map<ItemStack, Pickaxe> pickaxeMap = new HashMap<>();

    private int blocksBroken = 0;

    public Pickaxe(ItemStack itemStack, String name) {
        super(itemStack, name);
    }

    public Pickaxe(ItemStack itemStack, Level level, int xp, String name, Skill skill) {
        super(itemStack, level, xp, name, skill);
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
        Pickaxe p = get(inhand);
        return p != null ? p : Utils.deserializePick(inhand);
    }

    public static Pickaxe get(ItemStack itemStack) {
        return pickaxeMap.get(itemStack);
    }

    @Override
    public void update(Player player) {
        super.update(player);
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(buildName(player) + ". Do /pick");
    }

    public void onBreak(BlockBreakEvent event) {
        blocksBroken++;
        for(PEnchant enchant : this.getEnchants().values()) {
            enchant.activate(event);
        }
        int xp = 1;
        if(BlockValues.getXp(event.getBlock()) > -1) {
            xp = BlockValues.getXp(event.getBlock());
        }
        incrementXp(xp, event.getPlayer());
        this.update(event.getPlayer());
    }

    public int getBlocksBroken() {
        return blocksBroken;
    }

    private String buildName(Player player) {
        return ChatColor.AQUA + player.getName() + String.format("'s Diamond Pickaxe: Level: %d XP: %d Blocks: %d",
                this.level.getId(), this.xp, this.blocksBroken);
    }

    public void setBlocksBroken(int blocksBroken) {
        this.blocksBroken = blocksBroken;
    }
}
