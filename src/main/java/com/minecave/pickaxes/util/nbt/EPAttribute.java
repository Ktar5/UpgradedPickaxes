/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.nbt;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.UUID;

public class EPAttribute {
    EPNbtFactory.NbtCompound data;

    EPAttribute(EPAttributeBuilder builder) {
        data = EPNbtFactory.createCompound();
        setAmount(builder.amount);
        setOperation(builder.epOperation);
        setAttributeType(builder.type);
        setName(builder.name);
        setUUID(builder.uuid);
    }

    EPAttribute(EPNbtFactory.NbtCompound data) {
        this.data = data;
    }

    /**
     * Construct a new attribute builder with a random UUID and default operation of adding numbers.
     *
     * @return The attribute builder.
     */
    public static EPAttributeBuilder newBuilder() {
        return new EPAttributeBuilder().uuid(UUID.randomUUID()).operation(EPOperation.ADD_NUMBER);
    }

    public double getAmount() {
        return data.getDouble("Amount", 0.0);
    }

    public void setAmount(double amount) {
        data.put("Amount", amount);
    }

    public EPOperation getOperation() {
        return EPOperation.fromId(data.getInteger("Operation", 0));
    }

    public void setOperation(@Nonnull EPOperation EPOperation) {
        Preconditions.checkNotNull(EPOperation, "operation cannot be NULL.");
        data.put("Operation", EPOperation.getId());
    }

    public EPAttributeType getAttributeType() {
        return EPAttributeType.fromId(data.getString("AttributeName", null));
    }

    public void setAttributeType(@Nonnull EPAttributeType type) {
        Preconditions.checkNotNull(type, "type cannot be NULL.");
        data.put("AttributeName", type.getMinecraftId());
    }

    public String getName() {
        return data.getString("Name", null);
    }

    public void setName(@Nonnull String name) {
        Preconditions.checkNotNull(name, "name cannot be NULL.");
        data.put("Name", name);
    }

    public UUID getUUID() {
        return new UUID(data.getLong("UUIDMost", null), data.getLong("UUIDLeast", null));
    }

    public void setUUID(@Nonnull UUID id) {
        Preconditions.checkNotNull("id", "id cannot be NULL.");
        data.put("UUIDLeast", id.getLeastSignificantBits());
        data.put("UUIDMost", id.getMostSignificantBits());
    }


}
