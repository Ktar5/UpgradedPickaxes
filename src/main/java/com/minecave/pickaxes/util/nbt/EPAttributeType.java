/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.nbt;

import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;

public class EPAttributeType {
    private static ConcurrentMap<String, EPAttributeType> LOOKUP = Maps.newConcurrentMap();
    private final String minecraftId;
    public static final EPAttributeType GENERIC_MAX_HEALTH           = new EPAttributeType("generic.maxHealth").register();
    public static final EPAttributeType GENERIC_FOLLOW_RANGE         = new EPAttributeType("generic.followRange").register();
    public static final EPAttributeType GENERIC_ATTACK_DAMAGE        = new EPAttributeType("generic.attackDamage").register();
    public static final EPAttributeType GENERIC_MOVEMENT_SPEED       = new EPAttributeType("generic.movementSpeed").register();
    public static final EPAttributeType GENERIC_KNOCKBACK_RESISTANCE = new EPAttributeType("generic.knockbackResistance").register();

    /**
     * Construct a new attribute type.
     * <p/>
     * Remember to {@link #register()} the type.
     *
     * @param minecraftId - the ID of the type.
     */
    public EPAttributeType(String minecraftId) {
        this.minecraftId = minecraftId;
    }

    /**
     * Retrieve the attribute type associated with a given ID.
     *
     * @param minecraftId The ID to search for.
     * @return The attribute type, or NULL if not found.
     */
    public static EPAttributeType fromId(String minecraftId) {
        return LOOKUP.get(minecraftId);
    }

    /**
     * Retrieve every registered attribute type.
     *
     * @return Every type.
     */
    public static Iterable<EPAttributeType> values() {
        return LOOKUP.values();
    }

    /**
     * Retrieve the associated minecraft ID.
     *
     * @return The associated ID.
     */
    public String getMinecraftId() {
        return minecraftId;
    }

    /**
     * Register the type in the central registry.
     *
     * @return The registered type.
     */
    // Constructors should have no side-effects!
    public EPAttributeType register() {
        EPAttributeType old = LOOKUP.putIfAbsent(minecraftId, this);
        return old != null ? old : this;
    }
}
