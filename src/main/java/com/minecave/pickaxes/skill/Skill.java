package com.minecave.pickaxes.skill;

import com.minecave.pickaxes.pitem.PItem;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Timothy Andis
 */
public abstract class Skill {

    private String name;
    private long cooldown;
    private int level;
    @Getter
    private int cost;
    @Getter
    private String perm;
    private Map<UUID, Long> cooldowns;
    protected final WorldGuardPlugin wg;

    public Skill(String name, long cooldown, int level, int cost, String perm) {
        wg = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        this.name = name;
        this.cooldown = cooldown;
        this.level = level;
        this.cost = cost;
        this.perm = perm;
        this.cooldowns = new HashMap<>();
    }

    public boolean highEnough(PItem item) {
        return item.getLevel().getId() >= level;
    }

    public boolean canUse(Player player, PItem item) {
        long seconds = getTimeDiff(player);
        System.out.println(cooldown <= seconds && highEnough(item));
        return cooldown <= seconds && highEnough(item);
    }

    public long getTimeDiff(Player player) {
        long current = System.currentTimeMillis();
        Long in = cooldowns.get(player.getUniqueId());
        if(in == null) {
            return cooldown;
        }
        return TimeUnit.MILLISECONDS.toSeconds(current) - TimeUnit.MILLISECONDS.toSeconds(in);
    }

    public void add(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void use(Player player, PlayerInteractEvent event) {
        player.sendMessage(ChatColor.GOLD + "You used " + this.getName() + ". Cooldown: " + cooldown + "s");
        this.use(event);
    }

    public abstract void use(PlayerInteractEvent event);

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public long getTimeLeft(Player player) {
        return cooldown - getTimeDiff(player);
    }
}
