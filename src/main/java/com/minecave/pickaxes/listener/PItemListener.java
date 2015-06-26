package com.minecave.pickaxes.listener;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.item.PItem;
import com.minecave.pickaxes.item.PItemType;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.skill.pick.Earthquake;
import com.minecave.pickaxes.skill.pick.Nuker;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Timothy Andis
 */
public class PItemListener implements Listener {

    private EnhancedPicks plugin = EnhancedPicks.getInstance();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack inhand = player.getItemInHand();
        if (inhand == null || inhand.getType() == Material.AIR) {
            return;
        }
        PItem<BlockBreakEvent> pItem = EnhancedPicks.getInstance().getPItemManager()
                .getPItem(BlockBreakEvent.class, player.getItemInHand());
        if (pItem != null) {
            pItem.setItem(inhand);
            pItem.onAction(event);
            if (pItem.getCurrentSkill() != null &&
                    pItem.getCurrentSkill() instanceof Nuker) {
                pItem.getCurrentSkill().onBreak(event);
            }
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR &&
                event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack inhand = player.getItemInHand();
        if (inhand == null || inhand.getType() == Material.AIR) {
            return;
        }
        PItem<?> pItem = null;
        if (inhand.getType() == PItemType.PICK.getType()) {
            pItem = EnhancedPicks.getInstance().getPItemManager()
                    .getPItem(BlockBreakEvent.class, player.getItemInHand());
        } else if (inhand.getType() == PItemType.SWORD.getType()) {
            pItem = EnhancedPicks.getInstance().getPItemManager()
                    .getPItem(EntityDamageByEntityEvent.class, player.getItemInHand());
        }
        if (pItem != null && pItem.getCurrentSkill() != null) {
            PSkill skill = pItem.getCurrentSkill();
            if (!skill.canUse(player, pItem)) {
                player.sendMessage(ChatColor.RED + "You cannot use " + skill.getName() +
                        " for another " + skill.getTimeLeft(player) + "s.");
            } else {
                skill.use(player, event);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamaage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            return;
        }
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }
        Player player = (Player) damager;
        ItemStack inhand = player.getItemInHand();
        if (inhand == null || inhand.getType() == Material.AIR) {
            return;
        }
        PItem<EntityDamageByEntityEvent> pItem = EnhancedPicks.getInstance().getPItemManager()
                .getPItem(EntityDamageByEntityEvent.class, player.getItemInHand());
        if (pItem != null) {
            pItem.onAction(event);
        }
    }

    @EventHandler
    public void onEarthquake(EntityChangeBlockEvent event) {
        if (event.getEntity() == null) {
            return;
        }
        if (event.getEntity() instanceof FallingBlock) {
            //TODO: earthquake give players item
            Block block = event.getBlock();
            FallingBlock fallingBlock = (FallingBlock) event.getEntity();
            Earthquake earthquake = ((Earthquake)plugin.getPSkillManager().getPSkill("earthquake"));
            if ((fallingBlock.getCustomName() != null &&
                    fallingBlock.getCustomName().contains("earthquake")) ||
                    earthquake.getFallingBlockList().contains(fallingBlock)) {
                block.setType(Material.AIR);
                event.setCancelled(true);
                earthquake.getFallingBlockList().remove(fallingBlock);
            }
        }
    }
}
