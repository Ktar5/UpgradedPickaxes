/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.item;

import com.minecave.pickaxes.drops.BlockValue;
import com.minecave.pickaxes.drops.MobValue;
import com.minecave.pickaxes.enchant.PEnchant;
import com.minecave.pickaxes.level.Level;
import com.minecave.pickaxes.skill.PSkill;
import com.minecave.pickaxes.util.firework.FireworkBuilder;
import com.minecave.pickaxes.util.item.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Getter
@Setter
public class PItemSettings {

    private final PItemType      type;
    private final List<PSkill>   skillList;
    private final List<PEnchant> enchantList;
    private       String         key;
    private       String         name;
    private       int            startXp;
    private       int            startLevel;
    private       int            startPoints;
    private       int            pointsPerLevel;
    private       List<String>   nukerBlocks;

    private int maxLevel = 10;
    private Map<Integer, Level> levelMap;
    private Level               exampleLevel;
    private FireworkBuilder     defaultBuilder;
    private List<Integer>       blackList;
    private List<String>        levelUpMessage;

    public PItemSettings(String key, PItemType type) {
        this.key = key;
        this.name = key;
        this.type = type;
        this.skillList = new ArrayList<>();
        this.enchantList = new ArrayList<>();
        this.nukerBlocks = new ArrayList<>();
        this.startXp = 0;
        this.startLevel = 0;
        this.startPoints = 1;
        this.pointsPerLevel = 1;

        this.levelMap = new HashMap<>();
        this.defaultBuilder = null;
        this.exampleLevel = null;
        this.blackList = new ArrayList<>();
        this.levelUpMessage = new ArrayList<>();
    }

    public void addNukerBlocks(String s) {
        nukerBlocks.add(s);
    }

    public void addSkill(PSkill skill) {
        if (!skillList.contains(skill))
            skillList.add(skill);
    }

    public void addEnchant(PEnchant enchant) {
        if (!enchantList.contains(enchant))
            enchantList.add(enchant);
    }

    public boolean hasEnchant(String name) {
        for (PEnchant pEnchant : this.enchantList) {
            if(pEnchant.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public <P extends Event> PItem<P> generate(Class<P> pClass) {
        Level level = this.getLevel(this.startLevel);
        if (level == null) {
            level = this.getLevel(1);
        }
        ItemBuilder builder = ItemBuilder.wrap(new ItemStack(type.getType()));
        PItem<P> pItem = null;
        switch (type) {
            case PICK:
                if (!pClass.equals(BlockBreakEvent.class)) {
                    return null;
                }
                pItem = new PItem<>(this, pClass, this.name, type, builder.build());
                pItem.setAction((p, e) -> {
                    BlockBreakEvent blockBreakEvent = (BlockBreakEvent) e;
                    p.setBlocksBroken(p.getBlocksBroken() + 1);
                    int xp = BlockValue.getXp(blockBreakEvent.getBlock());
                    p.incrementXp(xp, blockBreakEvent.getPlayer());
                    p.activateEnchants(blockBreakEvent);
                    p.update(blockBreakEvent.getPlayer());
                });
                break;
            case SWORD:
                if (!pClass.equals(EntityDamageByEntityEvent.class)) {
                    return null;
                }
                pItem = new PItem<>(this, pClass, this.name, type, builder.build());
                pItem.setAction((p, e) -> {
                    EntityDamageByEntityEvent attackEvent = (EntityDamageByEntityEvent) e;
                    if (attackEvent.getEntity() instanceof LivingEntity) {
                        LivingEntity le = (LivingEntity) attackEvent.getEntity();
                        if (le.getHealth() <= attackEvent.getFinalDamage()) {
                            int xp = MobValue.getXp(attackEvent.getEntity());
                            p.incrementXp(xp, (Player) attackEvent.getDamager());
                        }
                    }
                    p.activateEnchants(attackEvent);
                    p.update((Player) attackEvent.getDamager());
                });
                break;
        }
        pItem.setLevel(level);
        pItem.setPoints(startPoints);
        pItem.setPointsPerLevel(pointsPerLevel);
        nukerBlocks.forEach(pItem::addNukerBlocks);
        for (PEnchant pEnchant : this.enchantList) {
            PEnchant clone = pEnchant.cloneEnchant();
            clone.setLevel(pEnchant.getLevel());
            clone.setMaxLevel(pEnchant.getMaxLevel());
            clone.setStartLevel(pEnchant.getStartLevel());
            pItem.addEnchant(clone);
        }
        this.skillList.forEach(pItem::addAvailableSkill);
        for (PEnchant pEnchant : pItem.getEnchants()) {
            pEnchant.apply(pItem);
        }
        pItem.setPItemSettings(this.key);
        pItem.updateMeta();
        return pItem;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\n");
        builder.append("  =====PItemSettings=====").append("\n")
                .append("  Key: ").append(key).append("\n")
                .append("  Name: ").append(name).append("\n")
                .append("  Type: ").append(type.name()).append("\n")
                .append("  Start XP: ").append(startXp).append("\n")
                .append("  Start Level: ").append(startLevel).append("\n")
                .append("  Start Points: ").append(startPoints).append("\n")
                .append("  Points per level: ").append(pointsPerLevel).append("\n")
                .append("  Skills: \n");
        for (PSkill pSkill : this.skillList) {
            builder.append("    Name: ").append(pSkill.getName()).append("\n")
                    .append("    Level: ").append(pSkill.getLevel()).append("\n")
                    .append("    Cost: ").append(pSkill.getCost()).append("\n")
                    .append("    Permission: ").append(pSkill.getPerm()).append("\n");
        }
        builder.append("  Enchants: \n");
        for (PEnchant pEnchant : this.enchantList) {
            builder.append("    Name: ").append(pEnchant.getName()).append("\n")
                    .append("    Display Name: ").append(pEnchant.getDisplayName()).append("\n")
                    .append("    Level: ").append(pEnchant.getLevel()).append("\n")
                    .append("    Max Level: ").append(pEnchant.getMaxLevel()).append("\n");
        }
        builder.append("  =====PItemSettings=====").append("\n");
        return builder.toString();
    }

    public Level getLevel(int id) {
        return levelMap.get(maxLevel < id ? maxLevel : (id > 0 ? id : 1));
    }

    public Level getMaxLevel() {
        return getLevel(maxLevel);
    }

    public int getMaxLevelInt() {
        return this.maxLevel;
    }

    public Supplier<String> compress() {
        return () -> toString().replace("\n", "..");
    }

    public PEnchant getEnchant(String name) {
        for (PEnchant pEnchant : this.enchantList) {
            if(pEnchant.getName().equals(name)) {
                return pEnchant;
            }
        }
        return null;
    }
}
