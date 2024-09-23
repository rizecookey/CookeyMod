package net.rizecookey.cookeymod.extension.minecraft;

import java.util.function.Consumer;

public interface Updatable<T> {
    void cookeyMod$registerOnChange(Consumer<T> listener);
    void cookeyMod$unregisterOnChange(Consumer<T> listener);
}
