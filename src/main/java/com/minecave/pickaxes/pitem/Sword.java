package com.minecave.pickaxes.pitem;

import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.Skill;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Timothy Andis
 */
public class Sword extends PItem {

    protected static Map<ItemStack, Sword> swordMap = new HashMap<>();

    public Sword(ItemStack itemStack, String name) {
        super(itemStack, name);
        swordMap.put(itemStack, this);
    }

    public Sword(ItemStack itemStack, Level level, int xp, String name, Skill skill) {
        super(itemStack, level, xp, name, skill);
        swordMap.put(itemStack, this);
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
        if(p == null) {
            p = PItemSerializer.deserializeSword(inhand);
            swordMap.put(inhand, p);
        }
        return p;
    }

    public static Sword get(ItemStack itemStack) {
        return swordMap.get(itemStack);
    }

    @Override
    public void update(Player player) {
        super.update(player);
        ItemMeta meta = this.itemStack.getItemMeta();
        meta.setDisplayName(buildName() + ". Do /sword");
        this.itemStack.setItemMeta(meta);
        player.updateInventory();
    }

    public String buildName() {
        return ChatColor.AQUA + name + String.format(": Level: %d XP: %d",
                this.level.getId(), this.xp);
    }


    public void onHit(EntityDamageByEntityEvent event) {
        incrementXp(xp, (Player) event.getDamager());
        this.getEnchants().values().stream()
                .filter(enchant -> enchant != null && enchant.getLevel() > 0)
                .forEach(enchant -> enchant.activate(event));
        update((Player) event.getDamager());
    }
}
