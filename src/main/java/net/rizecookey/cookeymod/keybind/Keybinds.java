package net.rizecookey.cookeymod.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public class Keybinds {
    private final KeyMapping openOptions;

    public Keybinds() {
        openOptions = KeyMappingHelper.registerKeyMapping(
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
