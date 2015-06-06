package com.minecave.pickaxes.level;

import com.minecave.pickaxes.PickaxesRevamped;
import com.minecave.pickaxes.builder.MessageBuilder;
import com.minecave.pickaxes.pitem.PItem;
import com.minecave.pickaxes.utils.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Timothy Andis
 */
public class Level {

    private PickaxesRevamped plugin = PickaxesRevamped.getInstance();

    private int xp, rep;
    private List<String> commands;
    private FireworkBuilder fireworkBuilder;
    private static Map<Integer, Level> levels = new HashMap<>();
    private static List<String> LEVEL_UP_MESSAGE = new ArrayList<>();
    public static Level ONE;
    private Level previous, next;

    public Level(int xp, int rep, List<String> commands) {
        this.xp = xp;
        this.rep = rep;
        this.commands = commands;
        if (ONE == null) {
            ONE = this;
        }
        levels.put(rep, this);
        this.previous = prev();
        this.next = nxt();
    }

    public Level(int xp, int rep, List<String> commands, FireworkBuilder builder) {
        this(xp, rep, commands);
        this.fireworkBuilder = builder;
    }

    public void levelUp(Player player, PItem pItem) {
        List<String> messages = new ArrayList<>();
        for (String s : LEVEL_UP_MESSAGE) {
            MessageBuilder builder = new MessageBuilder(s);
            builder.replace(player)
              .replace(rep, MessageBuilder.IntegerType.PLAYER_LEVEL)
              .replace(next.xp, MessageBuilder.IntegerType.NEXT_XP)
              .replace(next.rep, MessageBuilder.IntegerType.NEXT_LEVEL)
              .replace(0, MessageBuilder.IntegerType.XP)
              .replace(pItem);
            messages.add(builder.build());
        }
        String[] to = messages.toArray(new String[messages.size()]);
        String title_line1 = new MessageBuilder(plugin.getConfigValues().getTitleLine1())
          .replace(player)
          .replace(rep, MessageBuilder.IntegerType.PLAYER_LEVEL)
          .replace(next.xp, MessageBuilder.IntegerType.NEXT_XP)
          .replace(next.rep, MessageBuilder.IntegerType.NEXT_LEVEL)
          .replace(0, MessageBuilder.IntegerType.XP)
          .replace(pItem)
          .build();
        String title_line2 = new MessageBuilder(plugin.getConfigValues().getTitleLine2())
          .replace(player)
          .replace(rep, MessageBuilder.IntegerType.PLAYER_LEVEL)
          .replace(next.xp, MessageBuilder.IntegerType.NEXT_XP)
          .replace(next.rep, MessageBuilder.IntegerType.NEXT_LEVEL)
          .replace(0, MessageBuilder.IntegerType.XP)
          .replace(pItem)
          .build();
        Title title = new Title(title_line1, title_line2);
        title.setFadeInTime(35);
        title.setFadeOutTime(30);
        title.setTimingsToTicks();
        for (String s : commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
        }
        player.sendMessage(to);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        title.send(player);
        if (fireworkBuilder != null) {
            fireworkBuilder.play(player);
        }
    }

    private Level prev() {
        return levels.get(this.rep - 1);
    }

    private Level nxt() {
        return levels.get(this.rep + 1);
    }

    public static void setLevelUpMessage(List<String> messages) {
        LEVEL_UP_MESSAGE.clear();
        LEVEL_UP_MESSAGE.addAll(messages);
    }

    public Level getPrevious() {
        return previous;
    }

    public Level getNext() {
        return next;
    }

    public int getXp() {
        return xp;
    }

    public int getId() {
        return rep;
    }
}
