package com.minecave.pickaxes.pitem;

import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.Skill;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Timothy Andis
 */
public class Sword extends PItem {

    @Getter
    protected static List<Sword> swordList = new ArrayList<>();

    public Sword(ItemStack itemStack, String name) {
        super(itemStack, name);
        swordList.add(this);
    }

    public Sword(ItemStack itemStack, Level level, int xp, String name, Skill skill) {
        super(itemStack, level, xp, name, skill);
        swordList.add(this);
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
        Sword p = get(inhand);
//        if (p == null) {
//            p = PItemSerializer.deserializeSword(inhand);
//            swordList.add(p);
//        }
        return p;
    }

    public static Sword get(ItemStack itemStack) {
        for (Sword p : swordList) {
            if (p.getItemStack().equals(itemStack) || p.getItemStack().isSimilar(itemStack)) {
                return p;
            }
        }
        return null;
//        return swordList.get(itemStack);
    }

//    @Override
//    public void update(Player player) {
//        super.update(player);
//        ItemMeta meta = this.itemStack.getItemMeta();
//        meta.setDisplayName(buildName() + ". Do /sword");
//        this.itemStack.setItemMeta(meta);
//        player.updateInventory();
//    }

//    public String buildName() {
//        return ChatColor.AQUA + name + String.format(": Level: %d XP: %d",
//                this.level.getId(), this.xp);
//    }


    public void onHit(EntityDamageByEntityEvent event) {
        incrementXp(xp, (Player) event.getDamager());
        this.getEnchants().values().stream()
                .filter(enchant -> enchant != null && enchant.getLevel() > 0)
                .forEach(enchant -> enchant.activate(event));
        update((Player) event.getDamager());
    }
}
