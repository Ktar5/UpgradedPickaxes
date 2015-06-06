package com.minecave.pickaxes.listener;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.pitem.Pickaxe;
import com.minecave.pickaxes.pitem.Sword;
import com.minecave.pickaxes.skill.Skill;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Timothy Andis
 */
public class PItemListener implements Listener {

    private PickaxesRevamped plugin = PickaxesRevamped.getInstance();

    public PItemListener() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack inhand = player.getItemInHand();
        if (inhand == null || inhand.getType() == Material.AIR) {
            return;
        }
        Pickaxe pickaxe = Pickaxe.tryFromItem(inhand);
        if(pickaxe == null) {
            return;
        }
        pickaxe.onBreak(event);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack inhand = player.getItemInHand();
        if (inhand == null || inhand.getType() == Material.AIR) {
            return;
        }
        Pickaxe pickaxe = Pickaxe.tryFromItem(inhand);
        if(pickaxe != null) {
            Skill skill = pickaxe.getSkill();
            if(skill == null) {
                return;
            }
            if(!skill.canUse(player)) {
                return;
            }
            skill.use(event);
            return;
        }
        Sword sword = Sword.tryFromItem(inhand);
        if(sword == null) {
            return;
        }
        Skill skill = sword.getSkill();
        if(skill == null) {
            return;
        }
        if(!skill.canUse(player)) {
            return;
        }
        skill.use(event);
    }

    @EventHandler
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
        Sword s = Sword.tryFromItem(inhand);
        if(s == null) {
            return;
        }
        s.onHit(event);
    }
}