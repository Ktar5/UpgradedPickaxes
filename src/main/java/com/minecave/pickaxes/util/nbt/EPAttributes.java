package com.minecave.pickaxes.util.nbt;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.UUID;

public class EPAttributes {
    // This may be modified
    public  ItemStack            stack;
    private EPNbtFactory.NbtList attributes;

    public EPAttributes(ItemStack stack) {
        // Create a CraftItemStack (under the hood)
        this.stack = EPNbtFactory.getCraftItemStack(stack);

        // Load NBT
        EPNbtFactory.NbtCompound nbt = EPNbtFactory.fromItemTag(this.stack);
        this.attributes = nbt.getList("AttributeModifiers", true);
    }

    /**
     * Retrieve the modified item stack.
     *
     * @return The modified item stack.
     */
    public ItemStack getStack() {
        return stack;
    }

    /**
     * Retrieve the number of attributes.
     *
     * @return Number of attributes.
     */
    public int size() {
        return attributes.size();
    }

    /**
     * Add a new attribute to the list.
     *
     * @param EPAttribute - the new attribute.
     */
    public void add(EPAttribute EPAttribute) {
        Preconditions.checkNotNull(EPAttribute.getName(), "must specify an attribute name.");
        attributes.add(EPAttribute.data);
    }

    /**
     * Remove the first instance of the given attribute.
     * <p/>
     * The attribute will be removed using its UUID.
     *
     * @param EPAttribute - the attribute to remove.
     * @return TRUE if the attribute was removed, FALSE otherwise.
     */
    public boolean remove(EPAttribute EPAttribute) {
        UUID uuid = EPAttribute.getUUID();

        for (Iterator<EPAttribute> it = values().iterator(); it.hasNext(); ) {
            if (Objects.equal(it.next().getUUID(), uuid)) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public void clear() {
        attributes.clear();
    }

    /**
     * Retrieve the attribute at a given index.
     *
     * @param index - the index to look up.
     * @return The attribute at that index.
     */
    public EPAttribute get(int index) {
        return new EPAttribute((EPNbtFactory.NbtCompound) attributes.get(index));
    }

    // We can't make Attributes itself iterable without splitting it up into separate classes
    public Iterable<EPAttribute> values() {
        return () -> Iterators.transform(attributes.iterator(),
                element -> new EPAttribute((EPNbtFactory.NbtCompound) element));
    }
}
