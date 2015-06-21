package com.minecave.pickaxes.skill.pick;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.skill.PSkill;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * @author Timothy Andis
 */
public class Bomber extends PSkill {

    private int boomblocks;
    private int ticks;
    protected Random random = new Random();

    public Bomber(String name, long cooldown, int level, int cost, String perm, int boomblocks, int ticks, boolean seconds) {
        super(name, cooldown, level, cost, perm);
        this.boomblocks = boomblocks;
        this.ticks = seconds ? ticks * 20 : ticks;
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location location = player.getEyeLocation();
        Vector dir = location.getDirection();
        Vector to = dir.multiply(Math.random() * 4);
        TNTPrimed tnt = location.getWorld().spawn(location.clone().add(0, 0.2, 0), TNTPrimed.class);
        tnt.setFuseTicks(ticks + 1);
        tnt.setVelocity(to);
        int amount = random.nextInt(boomblocks) + 1;
        new BukkitRunnable() {
            @Override
            public void run() {
                tnt.remove();
                Location location = tnt.getLocation();
                if (!wg.canBuild(event.getPlayer(), location)) {
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    Location loc = location.clone()
                            .add((Math.random() * 8 - 3),
                                    (Math.random() * 2 - 3),
                                    (Math.random() * 8 - 3));
                    if (!wg.canBuild(event.getPlayer(), loc) ||
                            loc.getBlock().getType() == Material.BEDROCK ||
                            loc.getBlock().getType() == Material.AIR) {
                        continue;
                    }
                    player.getInventory().addItem(loc.getBlock().getDrops().toArray(new ItemStack[loc.getBlock().getDrops().size()]));
                    player.updateInventory();
                    loc.getBlock().setType(Material.AIR);
                    player.playSound(loc, Sound.EXPLODE, 1.0F, 1.0F);
                    player.playEffect(loc, Effect.LARGE_SMOKE, 1);
                }
            }
        }.runTaskLater(EnhancedPicks.getInstance(), ticks);
        this.add(player);
    }
}