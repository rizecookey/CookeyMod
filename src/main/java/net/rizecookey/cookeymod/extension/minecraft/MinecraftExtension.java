package net.rizecookey.cookeymod.extension.minecraft;

public interface MinecraftExtension {
    default boolean cookeyMod$isHoldingDownOnBlock() {
        throw new IllegalStateException("Extension has not been applied");
    }
}
