package com.minecave.pickaxes.menu.buttons;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.menu.Button;
import com.minecave.pickaxes.menu.Menu;
import com.minecave.pickaxes.player.PlayerInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * @author Timothy Andis
 */
public class SwordButton extends Button {

    public SwordButton(ItemStack itemStack) {
        super(itemStack, null);
    }

    public void click(Player player, ClickType type, int slot, Menu menu) {
        PlayerInfo info = EnhancedPicks.getInstance().getPlayerManager().get(player);
        if (info != null) {
            info.removeSword(EnhancedPicks.getInstance().getPItemManager()
                    .getPItem(EntityDamageByEntityEvent.class, this.getItem()));
            player.getInventory().addItem(this.getItem());
            menu.remove(slot, player);
        }
    }
}
