package net.rizecookey.cookeymod.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class Keybinds {
    private final KeyMapping openOptions;

    public Keybinds() {
        openOptions = KeyBindingHelper.registerKeyBinding(
                new KeyMapping("key.cookeymod_options",
                        InputConstants.UNKNOWN.getValue(),
                        KeyMapping.Category.register(Identifier.fromNamespaceAndPath(
                                "cookeymod",
                                "main"))));
    }

    public KeyMapping openOptions() {
        return openOptions;
    }
}
