package com.minecave.pickaxes.skill;

import com.minecave.pickaxes.pitem.PItem;
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
    private Map<UUID, Long> cooldowns;

    public Skill(String name, long cooldown, int level) {
        this.name = name;
        this.cooldown = cooldown;
        this.level = level;
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
