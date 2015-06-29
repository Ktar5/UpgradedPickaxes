/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.kit;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class Kit {
    private String pSettingsKey;
    private boolean pick;
    private boolean sword;
}
