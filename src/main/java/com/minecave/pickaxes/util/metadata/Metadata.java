/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.metadata;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
@AllArgsConstructor
public class Metadata<P> {

    private final P parent;
    private Map<String, Object> metadata = new HashMap<>();

    public void set(String key, Object object) {
        this.metadata.put(key, object);
    }

    public <T> T get(String key, Class<T> tClass) {
        if (!metadata.containsKey(key)) {
            throw new IllegalArgumentException(key + " does not exist.");
        }
        Object object = metadata.get(key);
        if (!tClass.isInstance(object)) {
            throw new IllegalArgumentException(key + " is not of type " + tClass.getSimpleName());
        }
        return tClass.cast(object);
    }
}
