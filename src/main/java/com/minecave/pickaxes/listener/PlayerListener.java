package com.minecave.pickaxes.listener;

import com.tadahtech.pub.PickaxesRevamped;
import com.tadahtech.pub.sql.PlayerInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Timothy Andis
 */
public class PlayerListener implements Listener {

    private PickaxesRevamped plugin = PickaxesRevamped.getInstance();

    public PlayerListener() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getSqlManager().init(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerInfo info = PlayerInfo.get(player);
        if(info == null) {
            return;
        }
        info.logOff();
    }
}
