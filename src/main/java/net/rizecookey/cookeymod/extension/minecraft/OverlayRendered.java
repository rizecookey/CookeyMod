package net.rizecookey.cookeymod.extension.minecraft;

public interface OverlayRendered {
    default void cookeyMod$setOverlayCoords(int overlayCoords) {
        throw new IllegalStateException("Extension has not been applied");
    }
}
