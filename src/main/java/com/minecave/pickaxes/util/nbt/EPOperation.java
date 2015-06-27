/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.nbt;

public enum EPOperation {
    ADD_NUMBER(0),
    MULTIPLY_PERCENTAGE(1),
    ADD_PERCENTAGE(2);
    private int id;

    EPOperation(int id) {
        this.id = id;
    }

    public static EPOperation fromId(int id) {
        // Linear scan is very fast for small N
        for (EPOperation op : values()) {
            if (op.getId() == id) {
                return op;
            }
        }
        throw new IllegalArgumentException("Corrupt operation ID " + id + " detected.");
    }

    public int getId() {
        return id;
    }
}
