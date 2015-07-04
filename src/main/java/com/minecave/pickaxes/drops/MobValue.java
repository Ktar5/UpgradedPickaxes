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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;

@Data
public class MobValue {


    @Getter
    private int                     xp;

    public MobValue(int xp) {
        this.xp = xp;
    }

    public static int getXp(Entity entity) {
        String type = entity.getType().name();
        if(entity instanceof Skeleton) {
            Skeleton.SkeletonType skele = ((Skeleton) entity).getSkeletonType();
            switch(skele) {
                case WITHER:
                    type = "WITHER_SKELETON";
                    break;
                default:
                    break;
            }
        }
        MobValue value = EnhancedPicks.getInstance().getDropManager().getMobValues().get(type);
        if (value == null) {
            return 1;
        }
        return value.getXp();
    }
}
