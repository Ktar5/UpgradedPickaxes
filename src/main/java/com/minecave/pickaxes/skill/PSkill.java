/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of pickaxes.
 * 
 * pickaxes can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 *
 * Created: 6/19/15 @ 7:47 PM
 */
package com.minecave.pickaxes.skill;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.skill.pick.Nuker;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public abstract class PSkill {

    protected final WorldGuardPlugin wg;
    private         String           name;
    protected          long             cooldown;
    private         int              level;
    @Getter
    private         int              cost;
    @Getter
    private         String           perm;
    private         Map<UUID, Long>  cooldowns;

    public PSkill(String name, long cooldown, int level, int cost, String perm) {
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
        return cooldown <= seconds && player.hasPermission(this.perm); // && highEnough(item);
    }

    public long getTimeDiff(Player player) {
        long current = System.currentTimeMillis();
        Long in = cooldowns.get(player.getUniqueId());
        if (in == null) {
            return cooldown;
        }
        return TimeUnit.MILLISECONDS.toSeconds(current) - TimeUnit.MILLISECONDS.toSeconds(in);
    }

    public void add(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void use(Player player, PlayerInteractEvent event) {
        if(this instanceof Nuker) {
            player.sendMessage(ChatColor.GOLD + "Nuker is a passive skill. Break some blocks.");
            return;
        }
        player.sendMessage(ChatColor.GOLD + "You used " + this.getName() + ". Cooldown: " + cooldown + "s.");
        EnhancedPicks.getInstance().getServer().getScheduler()
                .runTaskLater(EnhancedPicks.getInstance(), () -> {
                    player.sendMessage(ChatColor.GREEN + this.getName() + " is off cooldown.");
                }, cooldown * 20);
        this.use(event);
    }

    protected abstract void use(PlayerInteractEvent event);

    public void onBreak(BlockBreakEvent event) { }

    public int getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    public long getTimeLeft(Player player) {
        return cooldown - getTimeDiff(player);
    }

    public static int itemsDropped(int fortuneLevel){
        if(fortuneLevel == 0) {
            return 0;
        }
        int var3 = ThreadLocalRandom.current().nextInt(fortuneLevel + 2) - 1;

        double factor = EnhancedPicks.getInstance().getConfig("scale_factor").get("fortune_scale_factor", Double.class, 1D);

        if (var3 < 0)
            var3 = 0;
        if(factor > 0) {
            var3 *= factor;
        }
        return var3 + 1;
    }
}
