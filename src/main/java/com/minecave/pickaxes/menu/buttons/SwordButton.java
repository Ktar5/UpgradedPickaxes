package com.minecave.pickaxes.menu.buttons;

import com.minecave.pickaxes.EnhancedPicks;
import com.minecave.pickaxes.item.PItem;
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
            PItem<EntityDamageByEntityEvent> pItem = EnhancedPicks.getInstance().getPItemManager()
                    .getPItem(EntityDamageByEntityEvent.class, this.getItem());
            info.removeSword(pItem);
            player.getInventory().addItem(this.getItem());
            menu.remove(slot, player);
            pItem.update(player);
        }
    }
}
