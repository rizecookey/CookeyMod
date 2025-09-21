package net.rizecookey.cookeymod.extension.minecraft;

public interface AvatarRendererExtension {
    default void cookeyMod$setPlayerInvisible(boolean invisible) {
        throw new IllegalStateException("Extension has not been applied");
    }
}
