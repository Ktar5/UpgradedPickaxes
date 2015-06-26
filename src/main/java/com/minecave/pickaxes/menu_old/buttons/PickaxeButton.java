package com.minecave.pickaxes.menu_old.buttons;

public class PickaxeButton{}
//import com.minecave.pickaxes.EnhancedPicks;
//import com.minecave.pickaxes.item.PItem;
//import com.minecave.pickaxes.player.PlayerInfo;
//import org.bukkit.entity.Player;
//import org.bukkit.event.block.BlockBreakEvent;
//import org.bukkit.event.inventory.ClickType;
//import org.bukkit.inventory.ItemStack;
//
///**
// * @author Timothy Andis
// */
//public class PickaxeButton extends Button {
//
//    public PickaxeButton(ItemStack itemStack) {
//        super(itemStack, null);
//    }
//
//    public void click(Player player, ClickType type, int slot, Menu menu) {
//        PlayerInfo info = EnhancedPicks.getInstance().getPlayerManager().get(player);
//        if (info != null) {
//            PItem<BlockBreakEvent> pItem = EnhancedPicks.getInstance().getPItemManager()
//                    .getPItem(BlockBreakEvent.class, this.getItem());
//            info.removePickaxe(pItem);
//            player.getInventory().addItem(this.getItem());
//            menu.remove(slot, player);
//            pItem.update(player);
//        }
//    }
//}
