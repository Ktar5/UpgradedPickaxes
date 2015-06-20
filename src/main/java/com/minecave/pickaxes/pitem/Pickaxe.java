package com.minecave.pickaxes.pitem;

import com.minecave.pickaxes.drops.BlockValues;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.Skill;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Timothy Andis
 */
public class Pickaxe extends PItem {

    @Getter
    protected static List<Pickaxe> pickaxeList = new ArrayList<>();

    private int blocksBroken = 0;

    public Pickaxe(ItemStack itemStack, String name) {
        super(itemStack, name);
        pickaxeList.add(this);
    }

    public Pickaxe(ItemStack itemStack, Level level, int xp, String name, Skill skill) {
        super(itemStack, level, xp, name, skill);
        pickaxeList.add(this);
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
//        if(p == null) {
//            p = PItemSerializer.deserializePick(inhand);
//            pickaxeList.add(p);
//        }
        return p;
    }

    public static Pickaxe get(ItemStack itemStack) {
        for (Pickaxe p : pickaxeList) {
            System.out.println(p.getItemStack());
            System.out.println(itemStack);
            if (p.getItemStack().equals(itemStack) || p.getItemStack().isSimilar(itemStack)) {
                return p;
            }
        }
        return null;
//        return pickaxeList.get(itemStack);
    }

//    @Override
//    public void update(Player player) {
//        super.update(player);
//        ItemMeta meta = this.itemStack.getItemMeta();
//        meta.setDisplayName(buildName() + ". Do /pick");
//        this.itemStack.setItemMeta(meta);
//        player.updateInventory();
//    }

    public void onBreak(BlockBreakEvent event) {
        blocksBroken++;
        int xp = 1;
        if(BlockValues.getXp(event.getBlock()) > -1) {
            xp = BlockValues.getXp(event.getBlock());
        }
        incrementXp(xp, event.getPlayer());
        this.getEnchants().values().stream()
                .filter(enchant -> enchant != null && enchant.getLevel() > 0)
                .forEach(enchant -> enchant.activate(event));
        update(event.getPlayer());
    }

    public int getBlocksBroken() {
        return blocksBroken;
    }

    public String buildName() {
        return ChatColor.AQUA + name + String.format(": Level: %d XP: %d Blocks: %d",
                this.level.getId(), this.xp, this.blocksBroken);
    }

    public void setBlocksBroken(int blocksBroken) {
        this.blocksBroken = blocksBroken;
    }
}
