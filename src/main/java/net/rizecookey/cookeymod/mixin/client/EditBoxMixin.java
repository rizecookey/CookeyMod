package net.rizecookey.cookeymod.mixin.client;

import net.minecraft.client.gui.components.EditBox;
import net.rizecookey.cookeymod.extension.minecraft.Updatable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mixin(EditBox.class)
public abstract class EditBoxMixin implements Updatable<String> {
    @Unique
    private List<Consumer<String>> changeListeners = null;

    @Override
    public void cookeyMod$registerOnChange(Consumer<String> listener) {
        if (changeListeners == null) {
            changeListeners = new ArrayList<>();
        }
        changeListeners.add(listener);
    }

    @Override
    public void cookeyMod$unregisterOnChange(Consumer<String> listener) {
        if (changeListeners == null) {
            return;
        }

        changeListeners.remove(listener);

        if (changeListeners.isEmpty()) {
            changeListeners = null;
        }
    }

    @Inject(method = "onValueChange", at = @At("TAIL"))
    private void updateListeners(String string, CallbackInfo ci) {
        if (changeListeners == null) {
            return;
        }

        this.changeListeners.forEach(listener -> listener.accept(string));
    }
}
