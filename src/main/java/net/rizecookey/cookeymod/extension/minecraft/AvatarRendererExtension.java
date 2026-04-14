package net.rizecookey.cookeymod.extension.minecraft;

public interface AvatarRendererExtension {
    default void cookeyMod$setPlayerInvisible(boolean invisible) {
        throw new IllegalStateException("Extension has not been applied");
    }

    default void cookeyMod$setOverlayCoords(int overlayCoords) {
        throw new IllegalStateException("Extension has not been applied");
    }
}
