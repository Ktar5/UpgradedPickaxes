/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.firework;

import com.minecave.pickaxes.EnhancedPicks;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * Created by Carter on 6/6/2015.
 * <p/>
 * This code sucks shit, I DIDN'T CODE THIS CRAP!
 * ~Ktar
 */
public class FireworkBuilder {

    private static final Random RANDOM = new Random();
    private boolean playerOnly, trail, flicker, mainTooBig, fadeTooBig;
    private Type        type;
    private int         range;
    private List<Color> mainColors, fadeColors;
    private int amount;

    public FireworkBuilder() {
        this.mainColors = new ArrayList<>();
        this.fadeColors = new ArrayList<>();
        this.type = Type.BALL;
        this.playerOnly = true;
        this.trail = true;
        this.flicker = true;
        this.range = -1;
        this.amount = 1;
    }

    public FireworkBuilder addColor(Color color) {
        if (mainTooBig) {
            return this;
        }
        this.mainColors.add(color);
        if (mainColors.size() >= 4) {
            this.mainTooBig = true;
        }
        return this;
    }

    public FireworkBuilder addFadeColor(Color color) {
        if (fadeTooBig) {
            return this;
        }
        this.fadeColors.add(color);
        if (fadeColors.size() >= 4) {
            this.fadeTooBig = true;
        }
        return this;
    }

    public FireworkBuilder addColor(String color) {
        if (color.equalsIgnoreCase("RANDOM")) {
            if (!color.equalsIgnoreCase("RANDOM")) {
                EnhancedPicks.getInstance().getLogger().warning("Tried building a firework, but the Color String: " + color + " is not known!");
                return this;
            }
            return this.addColor(Color.fromRGB(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255)));
        }
        DyeColor dyeColor = DyeColor.valueOf(color.toUpperCase());
        return this.addColor(dyeColor.getFireworkColor());
    }

    public FireworkBuilder addFadeColor(String color) {
        if (color.equalsIgnoreCase("RANDOM")) {
            if (!color.equalsIgnoreCase("RANDOM")) {
                EnhancedPicks.getInstance().getLogger().warning("Tried building a firework, but the Color String: " + color + " is not known!");
                return this;
            }
            return this.addFadeColor(Color.fromRGB(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255)));
        }
        DyeColor dyeColor = DyeColor.valueOf(color.toUpperCase());
        return this.addFadeColor(dyeColor.getFireworkColor());
    }

    public FireworkBuilder playerOnly(boolean playerOnly) {
        this.playerOnly = playerOnly;
        return this;
    }

    public FireworkBuilder flicker(boolean flicker) {
        this.flicker = flicker;
        return this;
    }

    public FireworkBuilder trail(boolean trail) {
        this.trail = trail;
        return this;
    }

    public FireworkBuilder type(Type type) {
        this.type = type;
        return this;
    }

    public FireworkBuilder range(int range) {
        this.range = range;
        return this;
    }

    public FireworkBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public void play(Player player) {
        FireworkEffect effect = FireworkEffect.builder()
                .with(type)
                .withColor(mainColors)
                .withFade(fadeColors)
                .flicker(flicker)
                .trail(trail).build();
        if (playerOnly) {
            for (int i = 0; i < amount; i++) {
                InstantFirework.spawn(player.getEyeLocation().clone().add(0, 3, 0), effect, player);
            }
            return;
        }
        List<Player> players = player.getNearbyEntities(range, range, range).stream()
                .filter(ent -> ent instanceof Player)
                .map(ent -> (Player) ent)
                .collect(Collectors.toList());
        players.add(player);
        for (int i = 0; i < amount; i++) {
            InstantFirework.spawn(player.getEyeLocation().clone().add(0, 3, 0), effect, players.toArray(new Player[players.size()]));
        }
        players.clear();
    }


}
