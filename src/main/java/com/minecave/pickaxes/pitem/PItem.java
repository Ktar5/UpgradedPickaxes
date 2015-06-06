package com.minecave.pickaxes.pitem;

import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.Skill;
import com.minecave.pickaxes.utils.Message;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Timothy Andis
 */
public abstract class PItem {

    private Level level;
    private int xp, points;
    private List<PEnchant> enchants;
    private ItemStack itemStack;
    private String name;
    private Skill skill;

    public PItem(ItemStack itemStack, String name) {
        this.itemStack = itemStack;
        this.xp = 0;
        this.name = name;
        this.enchants = new ArrayList<>();
        this.level = Level.ONE;
    }

    public PItem(ItemStack itemStack, Level level, int xp, List<PEnchant> enchants, String name, Skill skill) {
        this.level = level;
        this.xp = xp;
        this.enchants = enchants;
        this.itemStack = itemStack;
        this.name = name;
        this.skill = skill;
    }

    public void addEnchant(PEnchant enchant, Player player) {
        this.enchants.add(enchant);
        this.update(player);
    }

    public void addEnchant(Enchantment enchantment, int level, Player player) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.addEnchant(enchantment, level, true);
        itemStack.setItemMeta(meta);
        update(player);
    }

    public void update(Player player) {
        ItemStack rep = this.itemStack;
        ItemStack item = null;
        int slot = -1;
        for(int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if(itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            if(rep.isSimilar(itemStack)) {
                item = itemStack;
                slot = i;
                break;
            }
        }
        if(item == null || slot == -1) {
            Message.FAILURE.sendMessage(player, "I couldn't find the Item in your inventory! Please contact an Admin!");
            return;
        }
        if(Pickaxe.pickaxeMap.get(item) != null) {
            Pickaxe.pickaxeMap.remove(item);
        } else if(Sword.swordMap.get(item) != null) {
            Sword.swordMap.remove(item);
        }
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>(meta.getLore());
        String enchants = null;
        StringBuilder builder = new StringBuilder("Custom Enchants: ");
        for(String s : lore) {
            if(s.startsWith(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Custom Enchants")) {
                enchants = s;
                break;
            }
        }
        lore.remove(enchants);
        for(PEnchant enchant : this.enchants) {
            builder.append(enchant.toString());
        }
        lore.add(builder.toString());
        meta.setLore(lore);
        item.setItemMeta(meta);
        player.getInventory().setItem(slot, item);
        if(this instanceof Pickaxe) {
            Pickaxe.pickaxeMap.put(item, (Pickaxe) this);
        } else if(this instanceof Sword) {
            Sword.swordMap.put(item, (Sword) this);
        }
    }

    public int getXp() {
        return xp;
    }

    public Level getLevel() {
        return level;
    }

    public int incrementXp(int xp, Player player) {
        this.xp += xp;
        if(level.getNext().getXp() >= xp) {
            this.level = level.getNext();
            level.levelUp(player, this);
            this.points++;
        }
        return xp;
    }

    public int getPoints() {
        return points;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public List<PEnchant> getEnchants() {
        return enchants;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }
}
