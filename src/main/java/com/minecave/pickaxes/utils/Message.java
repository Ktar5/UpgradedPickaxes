package com.minecave.pickaxes.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;

/**
 * @author Tim [calebbfmv]
 */
public enum Message {

    SUCCESS(ChatColor.GRAY.toString() + "[" + ChatColor.GREEN + ChatColor.BOLD + "THub" + ChatColor.GRAY + "] " + ChatColor.YELLOW),
    FAILURE(ChatColor.GRAY.toString() + "[" + ChatColor.RED + ChatColor.BOLD + "THub" + ChatColor.GRAY + "] " + ChatColor.GRAY);
    private String text;

    Message(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    public void sendMessage(Player player, String message) {
        player.sendMessage(text + message);
    }

    public static void sendPretty(Conversable player, String... messages) {
        String line = ChatColor.GREEN.toString() + ChatColor.STRIKETHROUGH + ChatColor.BOLD + "=";
        String bullet = ChatColor.YELLOW.toString() + ChatColor.BOLD + "  • " + ChatColor.GRAY;
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 21; i++) {
            builder.append(line);
        }
        player.sendRawMessage(builder.toString());
        player.sendRawMessage("");
        player.sendRawMessage("");
        for (String string : messages) {
            player.sendRawMessage(bullet + string);
        }
        player.sendRawMessage("");
        player.sendRawMessage("");
        player.sendRawMessage(builder.toString());
    }


}
