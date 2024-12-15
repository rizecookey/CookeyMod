package net.rizecookey.cookeymod.extension.minecraft;

public interface ArmedEntityRenderStateExtension {
    default boolean cookeyMod$shouldPoseBothArms() {
        throw new IllegalStateException("Extension has not been applied");
    }
    default void cookeyMod$setShouldPoseBothArms(boolean poseBothArms) {
        throw new IllegalStateException("Extension has not been applied");
    }
}
