/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.nbt;

import java.util.UUID;

// Makes it easier to construct an attribute
public class EPAttributeBuilder {
    EPOperation epOperation = EPOperation.ADD_NUMBER;
    EPAttributeType type;
    String          name;
    UUID            uuid;
    double          amount;

    EPAttributeBuilder() {
        // Don't make this accessible
    }

    public EPAttributeBuilder amount(double amount) {
        this.amount = amount;
        return this;
    }

    public EPAttributeBuilder operation(EPOperation epOperation) {
        this.epOperation = epOperation;
        return this;
    }

    public EPAttributeBuilder type(EPAttributeType type) {
        this.type = type;
        return this;
    }

    public EPAttributeBuilder name(String name) {
        this.name = name;
        return this;
    }

    public EPAttributeBuilder uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public EPAttribute build() {
        return new EPAttribute(this);
    }
}
