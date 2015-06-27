package com.minecave.pickaxes.skill.pick;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.drops.BlockValue;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.item.OreConversion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Timothy Andis
 */
public class Earthquake extends PSkill {

    private int radius;
    private Random             random           = new Random();
    private List<FallingBlock> fallingBlockList = new ArrayList<>();

    public Earthquake(int radius, String name, long cooldown, int level, int cost, String perm) {
        super(name, cooldown, level, cost, perm);
        this.radius = radius;
    }

    @Override
    public void use(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PItem<?> pItem = EnhancedPicks.getInstance().getPItemManager().getPItem(player.getItemInHand());
        if (pItem == null) {
            return;
        }
        Location location = player.getLocation();
        int playerX = location.getBlockX();
        int playerZ = location.getBlockZ();
        int y = location.getBlockY();
        int curCount = 0;
        int cap = ThreadLocalRandom.current().nextInt(30) + 1;
        for (int x = playerX - radius; x <= (playerX + radius); x++) {
            if(curCount >= cap) {
                break;
            }
            for (int z = playerZ - radius; z <= (playerZ + radius); z++) {
                if(curCount >= cap) {
                    break;
                }
                Location loc = new Location(location.getWorld(), x, y, z);
                if(ThreadLocalRandom.current().nextInt(10) < 5) {
                    continue;
                }
                if (!this.wg.canBuild(event.getPlayer(), loc)) {
                    continue;
                }
                if (loc.getBlock().getType() == Material.AIR || loc.getBlock().getType() == Material.BEDROCK) {
                    continue;
                }
                Collection<ItemStack> items = loc.getBlock().getDrops(player.getItemInHand());
                ItemStack[] array = new ItemStack[items.size()];
                int i = 0;
                for(ItemStack stack : items) {
                    stack.setType(OreConversion.convertToItem(stack.getType()));
                    array[i++] = stack;
                }
                int xp = BlockValue.getXp(loc.getBlock());
                pItem.incrementXp(xp, player);
                pItem.addBlockBroken();
                pItem.update(player);
                player.getInventory().addItem(array);
                player.updateInventory();
                Material type = loc.getBlock().getType();
                FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(loc, type, loc.getBlock().getData());
                fallingBlock.setVelocity(new Vector(Math.random() * 4.1, (random.nextInt(3) * Math.random()), Math.random() * 4.1));
                fallingBlock.setDropItem(false);
                fallingBlock.setCustomName("earthquake");
                fallingBlockList.add(fallingBlock);
                //not sure if they want this
                loc.getBlock().setType(Material.AIR);
                curCount++;
            }
        }
        this.add(player);
    }

    public List<FallingBlock> getFallingBlockList() {
        return this.fallingBlockList;
    }
}
