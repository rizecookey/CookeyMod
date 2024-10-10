package net.rizecookey.cookeymod.extension.minecraft;

import java.util.function.Consumer;

public interface Updatable<T> {
    default void cookeyMod$registerOnChange(Consumer<T> listener) {
        throw new IllegalStateException("Extension has not been applied");
    }
    default void cookeyMod$unregisterOnChange(Consumer<T> listener) {
        throw new IllegalStateException("Extension has not been applied");
    }
}
