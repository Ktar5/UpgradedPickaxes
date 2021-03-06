package com.minecave.pickaxes.util.nbt;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Store meta-data in an ItemStack as attributes.
 * Thank you to Kristian & aadnk & Comphenix for allowing me to use these <3
 */
public class EPAttributeStorage {
    private final UUID      uniqueKey;
    private       ItemStack target;

    private EPAttributeStorage(ItemStack target, UUID uniqueKey) {
        this.target = Preconditions.checkNotNull(target, "target cannot be NULL");
        this.uniqueKey = Preconditions.checkNotNull(uniqueKey, "uniqueKey cannot be NULL");
    }

    /**
     * Construct a new attribute storage system.
     * <p/>
     * The key must be the same in order to retrieve the same data.
     *
     * @param target    - the item stack where the data will be stored.
     * @param uniqueKey - the unique key used to retrieve the correct data.
     */
    public static EPAttributeStorage newTarget(ItemStack target, UUID uniqueKey) {
        return new EPAttributeStorage(target, uniqueKey);
    }

    public static EPAttributeStorage newTarget(ItemStack target, String uniqueKey) {
        return new EPAttributeStorage(target, UUID.fromString(uniqueKey));
    }

    /**
     * Retrieve the data stored in the item's attribute.
     *
     * @param defaultValue - the default value to return if no data can be found.
     * @return The stored data, or defaultValue if not found.
     */
    public String getData(String defaultValue) {
        EPAttribute current = getAttribute(new EPAttributes(target), uniqueKey);
        return current != null ? current.getName() : defaultValue;
    }

    /**
     * Set the data stored in the attributes.
     *
     * @param data - the data.
     */
    public void setData(String data) {
        EPAttributes EPAttributes = new EPAttributes(target);
        EPAttribute current = getAttribute(EPAttributes, uniqueKey);

        if (current == null) {
            EPAttributes.add(
                    new EPAttributeBuilder().uuid(UUID.randomUUID()).operation(EPOperation.ADD_NUMBER).
                            name(data).
                            amount(0).
                            uuid(uniqueKey).
                            operation(EPOperation.ADD_NUMBER).
                            type(EPAttributeType.GENERIC_ATTACK_DAMAGE).
                            build()
            );
        } else {
            current.setName(data);
        }
        this.target = EPAttributes.getStack();
    }

    /**
     * Retrieve the target stack. May have been changed.
     *
     * @return The target stack.
     */
    public ItemStack getTarget() {
        return target;
    }

    /**
     * Retrieve an attribute by UUID.
     *
     * @param EPAttributes - the attribute.
     * @param id         - the UUID to search for.
     * @return The first attribute associated with this UUID, or NULL.
     */
    private EPAttribute getAttribute(EPAttributes EPAttributes, UUID id) {
        for (EPAttribute EPAttribute : EPAttributes.values()) {
            if (Objects.equal(EPAttribute.getUUID(), id)) {
                return EPAttribute;
            }
        }
        return null;
    }
}
