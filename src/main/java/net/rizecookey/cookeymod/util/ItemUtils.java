package net.rizecookey.cookeymod.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;

public final class ItemUtils {
    private ItemUtils() {}

    public static boolean isToolItem(Item item) {
        return item.components().has(DataComponents.TOOL);
    }
}
