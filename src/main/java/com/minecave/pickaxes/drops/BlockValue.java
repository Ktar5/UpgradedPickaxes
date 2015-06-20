package com.minecave.pickaxes.drops;

import com.minecave.pickaxes.EnhancedPicks;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * @author Timothy Andis
 */
@Data
public class BlockValue {

    @Getter
    private int xp;
    @Getter
    private Material type;

    public BlockValue(int xp, Material type) {
        this.xp = xp;
        this.type = type;
    }

    public static int getXp(Block block) {
        if (!EnhancedPicks.getInstance().getDropManager().getBlockValues().containsKey(block.getType()) ||
                EnhancedPicks.getInstance().getDropManager().getBlockValues().get(block.getType()) == null) {
            return 1;
        }
        return EnhancedPicks.getInstance().getDropManager().getBlockValues().get(block.getType()).getXp();
    }
}
