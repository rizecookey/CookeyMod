package net.rizecookey.cookeymod.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.minecraft.client.KeyMapping;

public class Keybinds {
    public final KeyMapping openOptions;

    public Keybinds() {
        KeyBindingRegistryImpl.addCategory("key.categories.cookeymod");
        openOptions = KeyBindingRegistryImpl.registerKeyBinding(
                new KeyMapping("key.cookeymod_options", InputConstants.UNKNOWN.getValue(), "key.categories.cookeymod"));
    }
}
