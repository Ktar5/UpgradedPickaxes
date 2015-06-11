/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.skill;

import java.util.HashMap;
import java.util.Map;

public class Skills {

    private static Map<String, Skill> skills = new HashMap<>();

    static {
        skills = new HashMap<>();
    }

    public static Skill getSkill(String name) {
        return skills.get(name);
    }

    public static void add(String name, Skill skill) {
        skills.putIfAbsent(name, skill);
    }
}
