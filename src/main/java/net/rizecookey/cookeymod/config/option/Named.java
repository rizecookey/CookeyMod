package net.rizecookey.cookeymod.config.option;

import net.minecraft.network.chat.Component;

public interface Named {
    String getInternalName();
    Component getDisplayName();
}
