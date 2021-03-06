/*
 * Copyright (C) 2011-Current Richmond Steele (Not2EXceL) (nasm) <not2excel@gmail.com>
 * 
 * This file is part of minecave.
 * 
 * minecave can not be copied and/or distributed without the express
 * permission of the aforementioned owner.
 */
package com.minecave.pickaxes.util.nbt;

import com.minecave.pickaxes.item.PItem;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.lang.reflect.Method;
import java.util.List;

public class EPNBTSerialization {
    private static Method WRITE_NBT;
    private static Method READ_NBT;

    public static String toBase64(Inventory inventory) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);
        NBTTagList itemList = new NBTTagList();

        // Save every element in the list
        for (int i = 0; i < inventory.getSize(); i++) {
            NBTTagCompound outputObject = new NBTTagCompound();
            CraftItemStack craft = getCraftVersion(inventory.getItem(i));

            // Convert the item stack to a NBT compound
            if (craft != null)
                CraftItemStack.asNMSCopy(craft).save(outputObject);
            itemList.add(outputObject);
        }

        // Now save the list
        writeNbt(itemList, dataOutput);

        // Serialize that array
        return Base64Coder.encodeLines(outputStream.toByteArray());
    }

    public static Inventory fromBase64(String data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
        NBTTagList itemList = (NBTTagList) readNbt(new DataInputStream(inputStream), 0);
        Inventory inventory = new CraftInventoryCustom(null, itemList.size());

        for (int i = 0; i < itemList.size(); i++) {
            NBTTagCompound inputObject = (NBTTagCompound) itemList.get(i);

            if (!inputObject.isEmpty()) {
                inventory.setItem(i, CraftItemStack.asCraftMirror(
                        net.minecraft.server.v1_8_R3.ItemStack.createStack(inputObject)));
            }
        }

        // Serialize that array
        return inventory;
    }

    private static void writeNbt(NBTBase base, DataOutput output) {
        if (WRITE_NBT == null) {
            try {
                WRITE_NBT = NBTCompressedStreamTools.class.getDeclaredMethod("a", NBTBase.class, DataOutput.class);
                WRITE_NBT.setAccessible(true);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to find private write method.", e);
            }
        }

        try {
            WRITE_NBT.invoke(null, base, output);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to write " + base + " to " + output, e);
        }
    }

    private static NBTBase readNbt(DataInput input, int level) {
        if (READ_NBT == null) {
            try {
                READ_NBT = NBTCompressedStreamTools.class.getDeclaredMethod("a", DataInput.class, int.class, NBTReadLimiter.class);
                READ_NBT.setAccessible(true);
            } catch (Exception e) {
                throw new IllegalStateException("Unable to find private read method.", e);
            }
        }

        try {
            return (NBTBase) READ_NBT.invoke(null, input, level, new NBTReadLimiter(1000));
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to read from " + input, e);
        }
    }

    public static Inventory getInventoryFromArray(ItemStack[] items) {
        CraftInventoryCustom custom = new CraftInventoryCustom(null, items.length);

        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                custom.setItem(i, items[i]);
            }
        }
        return custom;
    }

    public static <E extends Event> Inventory getInventoryFromArray(List<PItem<E>> items) {
        CraftInventoryCustom custom = new CraftInventoryCustom(null, items.size());

        for (int i = 0; i < items.size(); i++) {
            PItem<?> item = items.get(i);
            if (item != null && item.getItem() != null) {
                custom.setItem(i, item.getItem());
            }
        }
        return custom;
    }

    private static CraftItemStack getCraftVersion(ItemStack stack) {
        if (stack instanceof CraftItemStack)
            return (CraftItemStack) stack;
        else if (stack != null)
            return CraftItemStack.asCraftCopy(stack);
        else
            return null;
    }
}
