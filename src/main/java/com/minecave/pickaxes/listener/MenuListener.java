package com.minecave.pickaxes.listener;

import com.minecave.pickaxes.EnhancedPicks;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {

    private EnhancedPicks plugin = EnhancedPicks.getInstance();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
    }
}
