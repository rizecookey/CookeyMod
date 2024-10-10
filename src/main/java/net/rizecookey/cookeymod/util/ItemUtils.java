package net.rizecookey.cookeymod.util;

import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;

public final class ItemUtils {
    private ItemUtils() {}

    public static boolean isToolItem(Item item) {
        return item instanceof SwordItem || item instanceof DiggerItem;
    }
}
