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

import java.util.*;

@Value
@AllArgsConstructor
public class Metadata<P> {

    private final P parent;
    private Map<String, Object> metadataMap = new HashMap<>();

    public <T> T set(String key, T object) {
        metadataMap.put(key, object);
        return object;
    }

    public <T> T get(String key, Class<T> tClass) {
        if (!has(key)) {
            throw new IllegalArgumentException(key + " does not exist.");
        }
        Object object = metadataMap.get(key);
        if(object == null) {
            return null;
        }
        if (!tClass.isInstance(object)) {
            throw new IllegalArgumentException(key + " is not of type " + tClass.getSimpleName());
        }
        return tClass.cast(object);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(String key, Class<T> tClass) {
        if (!has(key)) {
            throw new IllegalArgumentException(key + " does not exist.");
        }
        if(tClass.isPrimitive()) {
            throw new IllegalArgumentException(tClass + " is of a primitive type. Disallowed type.");
        }
        Object object = metadataMap.get(key);
        if(object == null) {
            return null;
        }
        if(!(object instanceof List<?>)) {
            throw new IllegalArgumentException(key + " is not an instance of a List.");
        }
        List<?> list = (List<?>) object;
        if(list.isEmpty()) {
            return Collections.emptyList();
        }
        Object first = list.stream().findFirst();
        if(!tClass.isInstance(first)) {
            throw new IllegalArgumentException(key + " is not a list of type " + tClass.getSimpleName());
        }
        return (List<T>) list;
    }


    public boolean has(String key) {
        return metadataMap.containsKey(key);
    }

    public boolean hasList(String key) {
        return has(key) && metadataMap.get(key) instanceof List<?>;
    }

    public <T> T getIfNotSet(String key, Class<T> tClass, T object) {
        if(has(key)) {
            return get(key, tClass);
        }
        return set(key, object);
    }

    public <T> List<T> getIfNotSetList(String key, Class<T> tClass) {
        if(has(key)) {
            return getList(key, tClass);
        }
        List<T> list = new ArrayList<>();
        return set(key, list);
    }

    public <T> List<T> getIfNotSetList(String key, Class<T> tClass, List<T> objects) {
        if(has(key)) {
            return getList(key, tClass);
        }
        List<T> list = objects != null ? objects : new ArrayList<>();
        return set(key, list);
    }
}
