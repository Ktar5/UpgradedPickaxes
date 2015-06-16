package com.minecave.pickaxes.skill;

import com.minecave.pickaxes.pitem.PItem;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import lombok.Getter;
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

    //The fuck is this shit?
    public boolean highEnough(PItem item) {
        return item.getLevel().getId() >= level;
    }

    public boolean canUse(Player player) {
        long current = System.currentTimeMillis();
        Long in = cooldowns.get(player.getUniqueId());
        if(in == null) {
            return true;
        }
        long seconds = TimeUnit.MILLISECONDS.toSeconds(current) - TimeUnit.MILLISECONDS.toSeconds(in);
        return cooldown <= seconds;
    }

    public void add(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public abstract void use(PlayerInteractEvent event);

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }
}
