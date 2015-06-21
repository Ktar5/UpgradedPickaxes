package com.minecave.pickaxes.drops;

import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Timothy Andis
 */
public abstract class Drop {

    protected static Map<Integer, List<MobDrop>>   mobDrops   = new HashMap<>();
    protected static Map<Integer, List<BlockDrop>> blockDrops = new HashMap<>();
    protected String command;
    protected int    weight;
    protected        Random                        random     = new Random();

    public Drop(int weight, String command, int level) {
        this.weight = weight;
        this.command = command;
        add(level);
    }

    public abstract void give(Player player);

    public boolean doGive(Player player) {
        return (random.nextInt(100) + 1) < weight;
    }

    public void add(int level) {
        if (this instanceof MobDrop) {
            List<MobDrop> drops = new ArrayList<>();
            if (mobDrops.get(level) != null) {
                drops = mobDrops.get(level);
            }
            drops.add((MobDrop) this);
        } else {
            List<BlockDrop> drops = new ArrayList<>();
            if (blockDrops.get(level) != null) {
                drops = blockDrops.get(level);
            }
            drops.add((BlockDrop) this);
        }
    }

}
