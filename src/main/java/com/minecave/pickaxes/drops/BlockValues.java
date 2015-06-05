package com.minecave.pickaxes.drops;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Timothy Andis
 */
public class BlockValues {

    private int xp;
    private Material type;
    private static Map<Material, Integer> values = new HashMap<>();

    public BlockValues(int xp, Material type) {
        this.xp = xp;
        this.type = type;
        values.put(type, xp);
    }

    public static int getXp(Block block) {
        if(values.get(block.getType()) == null) {
            return -1;
        }
        return values.get(block.getType());
    }
}
