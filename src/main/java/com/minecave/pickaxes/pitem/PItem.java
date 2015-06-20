package com.minecave.pickaxes.pitem;

import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.Skill;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Timothy Andis
 */
public abstract class PItem {

    protected Level level;
    protected int xp, points;
    @Getter
    private Map<String, PEnchant> enchants;
    protected ItemStack itemStack;
    protected final String name;
    @Getter
    private Set<Skill> purchasedSkills = new HashSet<>();
    @Getter
    private List<Skill> availableSkills = new ArrayList<>();
    protected Skill skill;
    @Getter
    @Setter
    private String pSettings = "";

    @Getter
    private static Map<String, PEnchant> enchantMap = new HashMap<>();

    public PItem(ItemStack itemStack, String name) {
        this(itemStack, Level.ONE, 0, name, null);
    }

    public PItem(ItemStack itemStack, Level level, int xp, String name, Skill skill) {
        this.level = level;
        this.xp = xp;
        this.itemStack = itemStack;
        this.name = name;
        this.skill = skill;
        this.enchants = new HashMap<>();
    }

    public void addEnchant(PEnchant enchant) {
        this.enchants.putIfAbsent(enchant.getTrueName(), enchant);
    }

    public void addEnchant(PEnchant enchant, Player player) {
        this.enchants.putIfAbsent(enchant.getTrueName(), enchant);
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
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack itemStack = player.getInventory().getItem(i);
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            if (rep.isSimilar(itemStack) || rep.equals(itemStack)) {
                item = itemStack;
                slot = i;
                break;
            }
        }
        if (item == null || slot == -1) {
//            Message.FAILURE.sendMessage(player, "I couldn't find the Item in your inventory! Please contact an Admin!");
            return;
        }
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("Custom Enchants: ");
        List<String> list = this.enchants.values().stream()
                .filter(enchant -> enchant.getLevel() > 0)
                .map(enchant -> ChatColor.AQUA + enchant.toString())
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            lore.add("None");
        } else {
            lore.addAll(list);
        }
        meta.setLore(lore);
        meta.setDisplayName(buildName());
        item.setItemMeta(meta);
//        player.getInventory().setItem(slot, item);
        player.updateInventory();
    }

    public String buildName() {
        return ChatColor.AQUA + name + String.format(": Level: %d XP: %d",
                this.level.getId(), this.xp);
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int i) {
        this.xp = i;
    }

    public Level getLevel() {
        return level;
    }

    public int incrementXp(int xp, Player player) {
        this.xp += xp;
        Level next = level.getNext();
        Level lvl = next != null ? next.getPrevious() : level;
        int total = 0;
        while (lvl != null && lvl.getId() >= 1) {
            total += lvl.getXp();
            lvl = lvl.getPrevious();
        }
        if (total <= this.xp) {
            if(level.getNext() != null) {
                this.level = next;
                level.levelUp(player, this);
            }
            this.points++;
        }
        update(player);
        return this.xp;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }

    public PEnchant getEnchant(String enchant) {
        if (!this.enchants.containsKey(enchant)) {
            throw new IllegalArgumentException("No Enchantment with that name");
        }
        return this.enchants.get(enchant);
    }

    public void setLevel(int level) {
        this.level = Level.ONE;
        for (int i = 1; i < level; i++) {
            this.level = this.level.getNext();
        }
    }

    public void addSkill(Skill skill) {
        this.availableSkills.add(skill);
    }
}
