/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.drops;

import com.minecave.pickaxes.EnhancedPicks;
import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.EntityType;

@Data
public class MobValue {


    @Getter
    private int xp;
    @Getter
    private EntityType type;

    public MobValue(int xp, EntityType type) {
        this.xp = xp;
        this.type = type;
    }

    public static int getXp(EntityType type) {
        if (!EnhancedPicks.getInstance().getDropManager().getMobValues().containsKey(type) ||
                EnhancedPicks.getInstance().getDropManager().getMobValues().get(type) == null) {
            return 1;
        }
        return EnhancedPicks.getInstance().getDropManager().getMobValues().get(type).getXp();
    }
}
