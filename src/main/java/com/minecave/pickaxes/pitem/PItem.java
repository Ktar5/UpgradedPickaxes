package com.minecave.pickaxes.pitem;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.enchant.enchants.LuckEnchant;
import com.minecave.pickaxes.enchant.enchants.NormalEnchant;
import com.minecave.pickaxes.enchant.enchants.TnTEnchant;
import com.minecave.pickaxes.items.GlowEnchant;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.Skill;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
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
    protected String name;
    @Getter
    private Set<Skill> purchasedSkills = new HashSet<>();
    protected Skill skill;

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
        FileConfiguration config = PickaxesRevamped.getInstance().getConfigValues().getEnchants();
        List<String> enchants = config.getStringList("availableEnchants");
        for (String s : enchants) {
            switch (s) {
                case "tnt":
                    this.addEnchant(new TnTEnchant());
                    break;
                case "luck":
                    this.addEnchant(new LuckEnchant());
                    break;
                default:
                    if (NormalEnchant.VanillaPick.has(s)) {
                        NormalEnchant.VanillaPick pick = NormalEnchant.VanillaPick.valueOf(s.toUpperCase());
                        if (pick != null && (this instanceof Pickaxe)) {
                            this.addEnchant(new NormalEnchant(pick.getEnchantment()));
                        }
                    } else if (NormalEnchant.VanillaSword.has(s)) {
                        NormalEnchant.VanillaSword sword = NormalEnchant.VanillaSword.valueOf(s.toUpperCase());
                        if (sword != null && (this instanceof Sword)) {
                            this.addEnchant(new NormalEnchant(sword.getEnchantment()));
                        }
                    }
            }
        }
        this.enchants.values().forEach(e -> {
            if (e.getLevel() > 0 && !(e instanceof NormalEnchant)) {
                GlowEnchant.apply(this.itemStack);
            }
        });
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
            if (rep.isSimilar(itemStack)) {
                item = itemStack;
                slot = i;
                break;
            }
        }
        if (item == null || slot == -1) {
//            Message.FAILURE.sendMessage(player, "I couldn't find the Item in your inventory! Please contact an Admin!");
            return;
        }
        if (Pickaxe.pickaxeMap.get(item) != null) {
            Pickaxe.pickaxeMap.remove(item);
        } else if (Sword.swordMap.get(item) != null) {
            Sword.swordMap.remove(item);
        }
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("Custom Enchants: ");
        lore.addAll(this.enchants.values().stream()
                .map(enchant -> ChatColor.AQUA + enchant.toString())
                .collect(Collectors.toList()));
        meta.setLore(lore);
        boolean sword = this instanceof Sword;
        name = ChatColor.AQUA + player.getName() + "'s Diamond " +
                (sword ? "Sword" : "Pickaxe") +
                ": Level: " + level.getId() + " XP: " + xp +
                (sword ? "" : (" Blocks: " + ((Pickaxe) this).getBlocksBroken()));
        meta.setDisplayName(this.name);
        item.setItemMeta(meta);
        player.getInventory().setItem(slot, item);
        if (this instanceof Pickaxe) {
            Pickaxe.pickaxeMap.put(item, (Pickaxe) this);
        } else if (this instanceof Sword) {
            Sword.swordMap.put(item, (Sword) this);
        }
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
        if (level.getNext().getXp() >= xp) {
            this.level = level.getNext();
            level.levelUp(player, this);
            this.points++;
        }
        update(player);
        return xp;
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
}
