package net.rizecookey.cookeymod.config.setting;

import net.minecraft.network.chat.Component;

public interface Named {
    String getInternalName();
    Component getDisplayName();
}
